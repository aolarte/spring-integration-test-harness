package com.andresolarte.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;

@ComponentScan("com.andresolarte.integration.config")
@SpringBootApplication
public class SpringIntegrationApp {

    public static void main(String... args) {
        SpringApplication.run(SpringIntegrationApp.class, args);
    }

}