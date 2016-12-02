package com.andresolarte.integration.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessingService {

    private static Log log = LogFactory.getLog(OrderProcessingService.class);


    public void handleOrder(Integer i) {
        log.info("=====> OrderProcessingService invoked, handling order: " + i);
    }
}
