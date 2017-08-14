package com.cloud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan({"com.cloud.config", "com.cloud.common"})
@EnableAspectJAutoProxy
public class MainConfig {
    public static void main(String[] args) {
        //TODO: make external config
        SpringApplication.run(MainConfig.class);
    }


    @Autowired
    private CharacterEncodingFilter encodingFilter;


    @PostConstruct
    public void init() {
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
    }
}
