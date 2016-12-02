package com.andresolarte.integration.controller;

import com.andresolarte.integration.gateway.ISender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller can be hit at http://localhost:8080/send/234
 */
@RestController
public class SendController {

    @Autowired
    private ISender sender;

    @RequestMapping(value = "/send/{msg}" )
    public void send(@PathVariable("msg") String msg) {
        sender.sendMessage(msg);
    }
}
