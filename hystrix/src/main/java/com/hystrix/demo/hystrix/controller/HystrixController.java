package com.hystrix.demo.hystrix.controller;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author whh
 */
@RestController
public class HystrixController {


    @Autowired
    private RestTemplate restTemplate;

    /**
     * 注解指定可降级的服务，fallbackMethod参数指向的是回调函数，函数名称可自定义
     */
    @HystrixCommand(fallbackMethod = "fallback")
    @GetMapping("getList")
    public List getList() {
        return restTemplate.getForObject("http://spring-cloud-producer/list", List.class);
    }

    /**
     * 出发降级后的回调函数
     */
    public List fallback() {
        List<String> list = new ArrayList<>();
        list.add("hystrix open");
        return list;
    }

}
