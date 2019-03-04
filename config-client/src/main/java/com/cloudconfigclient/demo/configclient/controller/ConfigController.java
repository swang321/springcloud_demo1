package com.cloudconfigclient.demo.configclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author whh
 */

/**
 * // 使用该注解的类，会在接到SpringCloud配置中心配置刷新的时候，自动将新的配置更新到该类对应的字段中。
 */
@RefreshScope
@RestController
public class ConfigController {

    @Value("${yml.text}")
    String configText;

    @RequestMapping("/getConfigText")
    public String from() {
        return this.configText;
    }

    @RequestMapping("/getConfigText1")
    public String hello() {
        return "getConfigText1";
    }

}
