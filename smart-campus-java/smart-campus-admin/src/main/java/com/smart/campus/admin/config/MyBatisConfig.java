package com.smart.campus.admin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"com.smart.campus.admin.mappers", "com.campus.mappers"})
public class MyBatisConfig {
}
