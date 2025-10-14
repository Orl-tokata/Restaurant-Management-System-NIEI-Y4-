package com.resturant.management.ResturantManagementSystem.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AppEnv {
    private final Environment env;

    public AppEnv(Environment env) {
        this.env = env;
    }

    @Bean
    public boolean isDev() {
        return env.matchesProfiles("dev");
    }

    @Bean
    public boolean isProd() {
        return env.matchesProfiles("prod");
    }
}
