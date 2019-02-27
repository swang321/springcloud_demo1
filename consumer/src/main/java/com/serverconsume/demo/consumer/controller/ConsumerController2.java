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
public class ConsumerController2 {

    @Autowired
    private LoadBalancerClient loadBalancerClient;


    @GetMapping("/info2")
    public List consume() {
        // 2.第二种方式，借助LoadBalancerClient获取服务实例，缺点：需要拼接url依旧不灵活
        RestTemplate restTemplate = new RestTemplate();
        // 参数传的是服务注册的spring.application.name
        ServiceInstance si = loadBalancerClient.choose("spring-cloud-producer");
        String url = String.format("http://%s:%s", si.getHost(), si.getPort() + "list");

        return restTemplate.getForObject(url, List.class);

    }

}
