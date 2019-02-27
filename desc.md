

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
    
//  路由熔断  服务降级