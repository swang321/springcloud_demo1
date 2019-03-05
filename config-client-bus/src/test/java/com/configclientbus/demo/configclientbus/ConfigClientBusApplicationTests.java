package com.configclientbus.demo.configclientbus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigClientBusApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void send() {
        for (int i = 0; i < 100; i++) {
            amqpTemplate.convertAndSend("myQueue", "第" + i + "条消息");
        }
    }

}
