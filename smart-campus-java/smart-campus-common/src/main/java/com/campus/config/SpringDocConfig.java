package com.campus.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智慧校园 API 文档")
                        .version("1.0.0")
                        .description("AI 驱动型智慧校园数字基座 — 后端接口文档\n\n" +
                                "认证方式：在 Authorize 按钮中输入 Bearer {token}")
                        .contact(new Contact()
                                .name("智慧校园开发团队")
                                .email("dev@campus.edu"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
