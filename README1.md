# spring cloud demo

#### 介绍
spring cloud 练习

参考    http://www.ityouknow.com/spring-cloud.html

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