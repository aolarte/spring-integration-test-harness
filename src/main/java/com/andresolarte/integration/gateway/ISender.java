package com.andresolarte.integration.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface ISender {
    @Gateway(requestChannel="jmsOutboundChannel")
    void sendMessage(String message);
}
