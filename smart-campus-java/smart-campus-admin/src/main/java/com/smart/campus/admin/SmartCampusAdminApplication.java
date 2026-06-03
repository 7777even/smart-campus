package com.smart.campus.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.smart.campus.admin", "com.campus"})
public class SmartCampusAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCampusAdminApplication.class, args);
    }
}
