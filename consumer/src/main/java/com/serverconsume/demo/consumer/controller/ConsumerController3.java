package com.serverconsume.demo.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @Author whh
 */
@RestController
public class ConsumerController3 {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/info3")
    public List consume() {
        return restTemplate.getForObject("http://spring-cloud-producer/list", List.class);
    }

}
