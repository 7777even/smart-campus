package com.smart.campus.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.smart.campus.web", "com.campus"})
public class SmartCampusWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCampusWebApplication.class, args);
    }
}
