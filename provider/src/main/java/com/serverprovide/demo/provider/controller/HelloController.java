package com.serverprovide.demo.provider.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author whh
 */
@RestController
public class HelloController {

    @RequestMapping("list")
    public List<String> list() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        return list;
    }

}
