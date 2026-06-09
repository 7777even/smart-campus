package com.smart.campus.web.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"com.smart.campus.web.mappers", "com.smart.campus.admin.mappers", "com.campus.mappers"})
public class MyBatisConfig {
}
