package com.wdy.springcloud.subservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@EnableEurekaClient
@RestController
@RequestMapping("/")
public class SubClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubClientApplication.class, args);
    }

    @Autowired
    private DiscoveryClient client;

    @RequestMapping("/hello")
    public String serviceTest()
    {
        List<String> lst = client.getServices();
        if(null == lst)
        {
            return "";
        }
        return String.join(",", lst);
    }
}
