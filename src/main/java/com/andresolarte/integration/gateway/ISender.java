package com.andresolarte.integration.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;


public interface ISender {
    void sendMessage(String message);
}
