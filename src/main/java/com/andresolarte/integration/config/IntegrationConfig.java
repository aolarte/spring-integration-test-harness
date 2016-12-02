package com.andresolarte.integration.config;

import com.andresolarte.integration.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.file.Files;
import org.springframework.integration.dsl.jms.Jms;
import org.springframework.integration.dsl.support.Transformers;
import org.springframework.integration.router.AbstractMappingMessageRouter;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.jms.ConnectionFactory;
import java.io.File;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableIntegration
@IntegrationComponentScan("com.andresolarte.integration.gateway")
public class IntegrationConfig {
    private static Log log = LogFactory.getLog(IntegrationConfig.class);
    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedRate(500).get();
    }


    @Bean
    public MessageSource fileMessageSource() {
        return Files.inboundAdapter(new File("/tmp/int"))
                .get();
    }

    @Bean
    public IntegrationFlow readFromFile() {
        return IntegrationFlows.from(fileMessageSource())
                .transform(Transformers.fileToString())
                .log()
                .channel(orderChannel())
                .get();

    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        return executor;
    }

    @Bean
    public MessageChannel orderChannel() {
        //return MessageChannels.executor(taskExecutor()).get();
        return MessageChannels.direct().get();
        //return MessageChannels.queue().get();
    }

    @Bean
    public IntegrationFlow processOrders() {
        return IntegrationFlows.from(orderChannel())
                .transform(Util::threadInfo)
                .transform(String::trim)
                //Router
                .route(new AbstractMappingMessageRouter() {
                    @Override
                    protected List<Object> getChannelKeys(Message<?> message) {
                        if (message.getPayload().toString().chars().allMatch(Character::isDigit)) {
                            return Arrays.asList("numericOrderChannel");
                        }
                        return Arrays.asList("alphaOrderChannel");
                    }
                })
                // FILTER
//                .<String>filter(s -> s.chars().allMatch(Character::isDigit))
//                .channel(numericOrderChannel())
                .get();
    }


    @Bean
    public IntegrationFlow jmsInboundFlow(ConnectionFactory jmsConnectionFactory) {
        return IntegrationFlows
                .from(Jms.inboundAdapter(jmsConnectionFactory)
                        .destination("orders"))
                .channel(orderChannel())
                .get();
    }

    @Bean
    public MessageChannel numericOrderChannel() {
        return MessageChannels.queue().get();
    }

    @Bean
    public IntegrationFlow processNumericOrders() {
        return IntegrationFlows.from(numericOrderChannel())
                .transform(Util::threadInfo)
                .transform(String::trim)
                .<String, Integer>transform(Integer::parseInt)
                //.handle(this::handleOrder)
                .handle("orderProcessingService","handleOrder")
                .get();
    }

    @Bean
    public MessageChannel alphaOrderChannel() {
        return MessageChannels.queue().get();
    }

    @Bean
    public IntegrationFlow processAlphaOrders() {
        return IntegrationFlows.from(alphaOrderChannel())
                .transform(Util::threadInfo)
                .transform(String::trim)
                .handle(m -> {
                    log.error("=====> Alphanumeric orders not yet implemented !!! " + m.getPayload());
                })
                .get();
    }


    private void handleOrder(Message<?> i) {
        log.info("=====> Handling order: " + i);
    }

    @Bean
    public MessageChannel errorChannel() {
        return MessageChannels.direct().get();
    }


    @Bean
    public IntegrationFlow processErrors() {
        return IntegrationFlows.from(errorChannel())
                .handle(System.err::println)
                .get();
    }


    ////// JMS outgoing config
    @Bean
    public MessageChannel jmsOutboundChannel() {
        return MessageChannels.queue().get();
    }

//    @Bean
//    public ISender sender(ConfigurableListableBeanFactory beanFactory) throws Exception {
//        GatewayProxyFactoryBean factoryBean = new GatewayProxyFactoryBean(ISender.class);
//        factoryBean.setDefaultRequestChannel(jmsOutboundChannel());
//        beanFactory.initializeBean(factoryBean, null);
//        Object o = factoryBean.getObject();
//        return (ISender) o;
//    }

    @Bean
    public IntegrationFlow jmsOutboundFlow(ConnectionFactory jmsConnectionFactory) {
        return IntegrationFlows.from(jmsOutboundChannel())
                .handle(Jms.outboundAdapter(jmsConnectionFactory)
                        .destination("orders"))
                .get();
    }


}
