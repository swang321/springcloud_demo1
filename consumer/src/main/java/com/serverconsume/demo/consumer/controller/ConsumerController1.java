package com.serverconsume.demo.consumer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @Author whh
 */
@RestController
public class ConsumerController1 {

    @GetMapping("/info1")
    public List consume() {
        RestTemplate restTemplate = new RestTemplate();
        // 1.第一种方式，直接使用。缺点：需要指定url地址，不灵活，也无法适应多个地址
        return restTemplate.getForObject("http://localhost:8081/list", List.class);

    }

}
