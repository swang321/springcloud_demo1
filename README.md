# spring cloud demo

#### 介绍
spring cloud 练习


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
    
    spring cloud-config 配置中心
    
    http://localhost:8084/config/pro
    http://localhost:8084/config/dev
    