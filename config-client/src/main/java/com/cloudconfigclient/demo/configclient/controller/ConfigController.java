package com.cloudconfigclient.demo.configclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author whh
 */
@RestController
public class ConfigController {

    @Value("${from}")
    private String text;

    @RequestMapping("/from")
    public String from() {
        return this.text;
    }

    @RequestMapping
    public String hello() {
        return "hello";
    }

}
