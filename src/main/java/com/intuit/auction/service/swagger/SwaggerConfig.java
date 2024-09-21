package com.intuit.auction.service.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bidding System API")
                        .version("1.0")
                        .description("This API provides endpoints to manage auctions/bidding in our system.")
                        .contact(new Contact()
                                .name("API Support")
                                .email("mkmayank39@gmail.com")));
    }
}