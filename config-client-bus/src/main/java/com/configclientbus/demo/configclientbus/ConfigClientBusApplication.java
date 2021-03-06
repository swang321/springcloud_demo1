package com.configclientbus.demo.configclientbus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ConfigClientBusApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientBusApplication.class, args);
    }

}
