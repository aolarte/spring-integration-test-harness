package com.andresolarte.integration.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@EnableWebMvc
@Configuration
@ComponentScan({"com.andresolarte.integration.controller",
        "com.andresolarte.integration.service"})
public class WebConfig {
}
