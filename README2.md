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
        
9   service-zuul (服务网关)
    
    1   启动类添加   @EnableZuulProxy 开启网关代理 添加到注册中心
    2   添加依赖
            <!--zuul 依赖包-->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
            </dependency> 
    3    添加zuul 配置
        zuul:
          routes:
            api-a:                              #   api-a 自己定义得  随意填
              path: /api-a/**                  #   以/api-a/开头得请求都转发给 服务名为 a  服务
              serviceId: spring-cloud-producer     #   servicedId  对应服务名字
            api-b:
              path: /api-b/**
              serviceId: spring-cloud-consumer
    4   访问  http://localhost:8087/api-a/list    list是  spring-cloud-producer暴露出来的接口    
             http://localhost:8087/api-b/info1    info1  spring-cloud-consumer暴露出来的接口
    5   应用场景    zuul  pre & post 过滤器,   限流,     权限校验,   跨域
            网关不要连接任何服务的关系型数据库
            获取数据应该通过调用服务接口的方式进行获取
            经常需要获取的数据有必要缓存到redis中，例如需要进行简单的权限缓存
    
9.1   service-zuul (服务网关)    filter  鉴权
    
    1   修改 zuul  module  添加 tokenfilter 类  注册 bean   
    2   访问  http://localhost:8087/api-a/list  返回    token is empty
        访问  http://localhost:8087/api-a/list?token=test  返回  <List><item>1</item><item>2</item></List>

9.2   service-zuul (服务网关)    路由熔断  服务可以自动进行降级

    1   添加 ProviderFallback 实现 FallbackProvider的方法  
    2   请求 http://localhost:8087/api-a/list 接口  而 spring-cloud-producer 服务挂掉时  返回 the service is unavailable.
    
9.3   service-zuul (服务网关)    路由重试      
    
    1   eg.有时候因为网络或者其它原因，服务可能会暂时的不可用，这个时候我们希望可以再次对服务进行重试
    2   添加依赖 
                <dependency>
                    <groupId>org.springframework.retry</groupId>
                    <artifactId>spring-retry</artifactId>
                </dependency>
    3   添加配置
        zuul:
          retryable: true   #  是否开启重试功能
        spring:
          cloud:
            loadbalancer:
              retry:
                enabled: true        #打开负载局衡器支持重试开关
    4   更改 服务 spring-cloud-producer  的接口   (/list)添加
            log.info("come in");
            try {
                Thread.sleep(1000000);
            } catch (InterruptedException e) {
                log.error(" hello two error", e);
            }    
    5   访问    http://localhost:8087/api-a/list      
        spring-cloud-producer 服务 会输出  两条日志   说明总共请求了两回
