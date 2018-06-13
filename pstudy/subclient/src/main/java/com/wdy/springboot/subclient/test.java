package com.wdy.springboot.subclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created on 2018/6/13 20:22
 * <p>Title:       []/p>
 * <p>Description: []</p>
 * <p>Company:     羚羊极速</p>
 *
 * @author [wudey]
 * @version 1.0
 */
@RestController
@RequestMapping("/")
public class test {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/hello")
    public String testService()
    {
        String url = "http://service/hello";
        return restTemplate.getForEntity(url, String.class).getBody();
    }
}
