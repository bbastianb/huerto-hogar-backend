package com.abs.huerto_hogar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI huertoHogarOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Huerto Hogar API")
                        .description("Documentación de la API del proyecto Huerto Hogar (productos, usuarios, contacto, etc.)")
                        .version("1.0.0"));
    }
}
