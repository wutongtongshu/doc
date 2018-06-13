package com.wdy.springcloud.sub00;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class Sub00Application {

    public static void main(String[] args) {
        SpringApplication.run(Sub00Application.class, args);
    }
}
