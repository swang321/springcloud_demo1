package com.serverprovide.demo.provider.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author whh
 */
@Slf4j
@RestController
public class HelloController {

    @RequestMapping("/list")
    public List<String> list() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");


//        log.info("come in");
//        try {
//            Thread.sleep(1000000);
//        } catch (InterruptedException e) {
//            log.error(" hello two error", e);
//        }

        return list;
    }


    @RequestMapping("/test1")
    public String zuulTest() {
        return "provider zuu1";
    }


    @RequestMapping("/test2")
    public String zuulTes2() {
        return "provider zuu2";
    }


    @RequestMapping("/hi")
    public String hello(@RequestParam String name) {
        return "hello: " + name;
    }


}
