package com.yogastudio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI yogaStudioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Yoga Studio Management API")
                        .description("REST API for managing yoga classes, instructors, "
                                + "students and bookings, with waitlist logic "
                                + "and capacity validation.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Iuliana Paun")
                                .url("https://github.com/veseletea/yoga-studio")));
    }
}