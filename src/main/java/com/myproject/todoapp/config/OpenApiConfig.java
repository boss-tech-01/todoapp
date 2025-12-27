package com.myproject.todoapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Todo App API Documentation")
                        .version("1.0.0")
                        .description("RESTful API documentation for the Todo Application project. This API supports user management and todo item operations.") // 💡 API Açıklaması
                        .termsOfService("http://localhost:8080/terms")
                        .license(new License().name("Apache 2.0")
                                .url("http://springdoc.org")));
    }

    //http://localhost:8080/swagger-ui.html
}