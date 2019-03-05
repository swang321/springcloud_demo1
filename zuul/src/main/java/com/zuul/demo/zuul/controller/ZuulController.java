package com.zuul.demo.zuul.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author whh
 */
@RestController
public class ZuulController {

    @RequestMapping("/api-zuul/test1")
    public String zuulTest() {
        return "zuu1";
    }



    @RequestMapping("/test2")
    public String zuulTes2() {
        return "zuu2";
    }

}
