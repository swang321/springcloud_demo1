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
   
7.2     读取服务端 

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
                //  yml.text  是 spring-cloud-config-client-pro.yml  里面得属性值
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