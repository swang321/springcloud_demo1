# spring cloud demo

#### 介绍
spring cloud 练习

参考    http://www.ityouknow.com/spring-cloud.html

1   注册中心 eureka
2   服务提供与调用
3   熔断器 hystrix
4   熔断监控    Hystrix Dashboard 和 turbine
5   配置中心git 
6   配置中心高可用 
7   配置中心    spring cloud-config 结合 rabbitmq 做消息总线 
8   服务网关    zuul
9   sleuth-zipkin 链路追踪



#### 使用说明

1 新建 maven 空项目  spring cloud 作为父工程

2 新建module  eureka-server 服务注册与发现中心
    
    1  继承 父工程的依赖，
    2  注意 springboot 与 spring cloud的版本对应关系
    3  @EnableEurekaServer 注解
    4  配置 yml  
    
3  新建 module 服务端 服务提供 provider
    
    1   添加 EnableDiscoveryClient  项目就具有服务注册功能
    2   写 controller 跟普通一样 
    3   配置 yml  
    

4  新建 module 客户端 消费 consumer

    1 添加 @EnableDiscoveryClient 
    2 服务之间的通信
    
        2-1:    ConsumerController1
        2-2:    ConsumerController2
        2-3:    ConsumerController3 &&  RestTemplateConfig3
        
    3 配置yml
    

5   熔断器 路由熔断  服务降级

    1   添加依赖  
         <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
         </dependency>      
    2   启动类添加注解  @EnableCircuitBreaker
    3   请求方法 添加@ HystrixCommand(fallbackMethod = "回调函数") 回调函数名自定义
    4   新建 以 回调函数 为名的方法，返回值和参数 必须和正式方法相同  不然会报错
    
6   熔断监控    Hystrix Dashboard   turbine

    1   添加依赖    
        <dependency>
        	<groupId>org.springframework.cloud</groupId>
        	<artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
        </dependency>
        <dependency>
        	<groupId>org.springframework.boot</groupId>
        	<artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    2   启动类添加注解     @EnableHystrixDashboard
    3   启动类添加配置servlet bean 
            @Bean
            public ServletRegistrationBean getServlet() {
                HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
                ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
                registrationBean.setLoadOnStartup(1);
                registrationBean.addUrlMappings("/actuator/hystrix.stream");
                registrationBean.setName("HystrixStreamServletBean");
                return registrationBean;
            }
    3   访问  http://localhost:8083/hystrix  首页
    4   输入  http://localhost:8083/actuator/hystrix.stream   点击 Monitor Stream 出现 监控页面  熔断监控完成
    5   turbine 是  hystrix.stream 的集合显示
        
        5-1 添加依赖    
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-netflix-turbine</artifactId>
                </dependency>
        5-2 启动类添加注解    @EnableTurbine    //启用 turbine
        5-3 修改配置bean   （添加 /hystrix.stream mapping）   因版本不同
                @Bean
                public ServletRegistrationBean getServlet() {
                    HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
                    ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
                    registrationBean.setLoadOnStartup(1);
                    String[] urlMappings = {"/actuator/hystrix.stream", "/hystrix.stream"};
                    registrationBean.addUrlMappings(urlMappings);
                    registrationBean.setName("HystrixStreamServletBean");
                    return registrationBean;
                }
                
7    spring cloud-config 配置中心  （git）
     
7.1    读取 config 中心
     
     1  在 git 上面创建一个文件夹 放上配置文件，  例  （config-repo）
     2  添加依赖
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
     3  yml
         spring:
           cloud:
             config:
               server:
                 git:
                   uri: *******  # 配置git仓库的地址
                   search-paths: /config-repo                      # git仓库地址下的相对地址，可以配置多个，用,分割。
                   username: *******                             # git仓库的账号
                   password: *******                           # git仓库的密码
     4  启动类添加 @EnableConfigServer 激活配置中心支持
     5  访问  http://localhost:8084/config/pro        http://localhost:8084/config/dev 读取配置数据
             http://localhost:8084/config-pro.yml    http://localhost:8084/config-dev.yml
   
7.2     读取服务端 配置中心的配置文件 

     1  导入依赖
     2  启动类添加注解 @EnableDiscoveryClient
     3  添加 bootstrap.yml
     
        spring:
          cloud:
            config:
              name: spring-cloud-config-client
              profile: pro                                 #   环境
              uri: http://localhost:8084/                   #  注册中心得地址
              label: master                               # 远程仓库的分支
   
     eg: spring-cloud-config-client-pro.yml
         spring.cloud.config.name  ->  spring-cloud-config-client
         spring.cloud.config.profile  ->  pro
    4   新建 controller
                //  ${yml.text}  是 spring-cloud-config-client-pro.yml  里面得属性值
                @Value("${yml.text}")
                String configText;
            
                @RequestMapping("/getConfigText")
                public String from() {
                    return this.configText;
                }
            
                @RequestMapping
                public String hello() {
                    return "hello";
                }
                
    5  访问       http://localhost:8085/getConfigText    im pro
    
    6  开启更新机制  @RefreshScope
        1   在需要动态更新的类上面加上 @RefreshScope 
        2   需要把 refresh 的接口暴露出来才可以访问
        3   在 bootstrap 中添加
            management:
              endpoints:
                web:
                  exposure:
                    include: refresh,health,info
        4   访问 http://localhost:8085/getConfigText       返回信息
        5   在git仓库里面把  配置文件（spring-cloud-config-client-pro.yml）的 yml.text 改成其他的
        6   访问 http://localhost:8085/actuator/refresh 返回 一串信息，再访问第四步，返回的是更新后的数据
        7   每次手动刷新客户端也很麻烦 可以配置  webhook 服务端刷新，码云 github  都支持
        8   配置 webhook 需要填写一个公网的 HTTP URL     可以用免费的 natapp内网穿透（自己百度）
        9   再码云填写完毕后 就会报错 400   
            Failed to read HTTP message: org.springframework.http.converter.HttpMessageNotReadableException: 
            JSON parse error: Cannot deserialize instance of `java.lang.String` out of START_ARRAY token; 
            nested exception is com.fasterxml.jackson.databind.exc.MismatchedInputException: 
            Cannot deserialize instance of `java.lang.String` out of START_ARRAY token
        10  报错是因为无法正常序列化json  配置了 webhook  ，每次更新会发送很多数据给后台，而 refresh接口不需要 参数就可以，
            写拦截器或者过滤器 对请求参数body 的数据做一个修改就行了。过滤器拦截不到此接口（没有深入理解，自行百度）
            故写的一个拦截器来拦截对body数据重新封装
        11  填写url后  每次配置文件 更新提交后  就自动刷新配置文件了。

7.3     读取服务端 配置中心的配置文件 （高可用）

        把 uri  去掉  加上 service-id   当配置中心 换一个服务器也不用改客户端  根据 配置中心的服务名字就可以找到
        bootstrap.yml
        spring:
          application:
            name: config-client
          cloud:
          
            config:
              name: spring-cloud-config-client
              profile: pro                                 #   环境
        #      uri: http://localhost:8084/
              label: master                               # 远程仓库的分支
              discovery:
                service-id: cloud-config
                enabled: true


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

10   sleuth-zipkin (分布式链路跟踪)
    
    1   新建 module sleuth-zipkin，引入依赖
                <!-- 引入zipkin-server -->
                <dependency>
                    <groupId>io.zipkin.java</groupId>
                    <artifactId>zipkin-server</artifactId>
                    <version>2.9.4</version>
                </dependency>
        
                <!-- 引入zipkin-server 图形化界面 -->
                <dependency>
                    <groupId>io.zipkin.java</groupId>
                    <artifactId>zipkin-autoconfigure-ui</artifactId>
                    <version>2.9.4</version>
                </dependency>
    2   启动器添加注解 @EnableZipkinServer     //zipkin服务器 默认使用http进行通信
    3   在 父项目 服务添加 依赖
           
                   <!-- 这个依赖包含了sleuth和zipkin -->
                   <dependency>
                       <groupId>org.springframework.cloud</groupId>
                       <artifactId>spring-cloud-starter-zipkin</artifactId>
                   </dependency>
        使服务支持 sleuth 和 zipkin
    4   在   spring-cloud-producer   spring-cloud-consumer  服务中 添加配置 
    
        spring:
          zipkin:
            base-url: http://localhost:9411/  # zipkin服务器的地址
            sender:
              type: web  # 设置使用http的方式传输数据
          sleuth:
            sampler:
              probability: 1  # 设置抽样采集为100%，默认为0.1，即10%
    5 启动服务 spring-cloud-eurerka spring-cloud-producer spring-cloud-consumer spring-cloud-sleuth-zipkin
      访问 zipkin 首页 localhost:9411 然后访问 spring-cloud-producer  和 spring-cloud-consumer 
      的接口，就会显示两个服务的链路



