package com.resturant.management.ResturantManagementSystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI restaurantOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Restaurant Management System API")
                        .description("API documentation for Restaurant Management System (Spring Boot + Angular)")
                        .version("1.0.0"));
    }
}
