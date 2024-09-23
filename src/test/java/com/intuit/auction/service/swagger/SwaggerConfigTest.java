package com.intuit.auction.service.swagger;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SwaggerConfigTest {

    @Autowired
    private OpenAPI openAPI;

    @Test
    void customOpenAPI_shouldReturnProperOpenAPIObject() {
        assertThat(openAPI).isNotNull();

        Info info = openAPI.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Bidding System API");
        assertThat(info.getVersion()).isEqualTo("1.0");
        assertThat(info.getDescription()).isEqualTo("This API provides endpoints to manage auctions/bidding in our system.");

        Contact contact = info.getContact();
        assertThat(contact).isNotNull();
        assertThat(contact.getName()).isEqualTo("API Support");
        assertThat(contact.getEmail()).isEqualTo("mkmayank39@gmail.com");
    }
}
