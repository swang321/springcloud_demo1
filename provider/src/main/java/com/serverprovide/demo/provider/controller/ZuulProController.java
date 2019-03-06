//package com.serverprovide.demo.provider.controller;
//
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @Author whh
// */
//@RestController
//@RequestMapping("/zuul")
//public class ZuulProController {
//
//    @RequestMapping("/list")
//    public List<String> list() {
//        List<String> list = new ArrayList<>();
//        list.add("1");
//        list.add("2");
//        return list;
//    }
//
//    @RequestMapping("/test1")
//    public String zuulTest() {
//        return "provider zuu1";
//    }
//
//
//    @RequestMapping("/test2")
//    public String zuulTes2() {
//        return "provider zuu2";
//    }
//
//
//    @RequestMapping("/hi")
//    public String hello(@RequestParam String name) {
//        return "hello: " + name;
//    }
//
//
//}
