package com.wdy.springboot.subclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
public class SubclientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubclientApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate getTemple()
    {
        return new RestTemplate();
    }
}
