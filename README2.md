# spring cloud demo

#### 介绍
spring cloud 练习

参考    http://www.ityouknow.com/spring-cloud.html

#### 使用说明

8   spring cloud-config 结合 rabbitmq 做消息总线  （config-client-bus）
    
    0   修改 config-client 项目
    1   添加依赖
                <!--增加对消息总线的支持-->
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
                </dependency>
    2   安装rabbitmq
    3   再bootstrap.yml中添加配置
        spring:
          cloud:
            bus:
              refresh:
                enabled: true         
              trace:
                enabled: true         # 开启消息跟踪
        
          # 登陆rabbitmq 后台 是 ip+15672
          # 后台里面有一个  port and contexts amqp                       5672
          #                                clustering                 25672
          #                                RabbitMQ Management        15672
          #  对应不同的端口   这个位置的端口需要填写  5672
          rabbitmq:
            host: *******
            port: 5672
            username: *******
            password: *******

        management:
          endpoints:
            web:
              exposure:
                include: "*"    # 也可以改为"*"来暴露所有接口  这个位置需要暴露 bus-refresh接口
    4   新建一个module 与config-client完全一样  ，把端口改成不一样就行了，分别启动
        eureka-server，cloud-config，config-client，config-client-bus
        (config-client，config-client-bus)这两个项目可以看作是一样得服务，部署再不同得机器上面，
        当配置中心得配置文件更新是，这两个项目都需要更新读取配置，从而需要刷新，访问  http://localhost:8085/actuator/bus-refresh
        再访问 http://localhost:8086/getConfigText，http://localhost:8086/getConfigText
        都是读取得更新后得文件，
    
  8.1   改进版本
    
    1   应该保证服务得单一职责原则，应该让配置中心去刷新 配置文件得更新，对配置中心服务添加 rabbitmq得支持，然后
        webhooks  post指向配置中心 刷新就可以刷新 了
        
    
 
    