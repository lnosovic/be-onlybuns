package com.example.onlybuns.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

/*
konfiguracija omogucava generisanje metapodataka API-ju, poput vlasnika, uslova koriscenja, licence i sl.
moguce je konfigurisati i vise servera
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        ArrayList<Server> servers = new ArrayList<>(3);
        servers.add(new Server().url("http://localhost:8080/").description("development server"));
        servers.add(new Server().url("http://qa:8081/").description("test server"));
        servers.add(new Server().url("http://www.rest-example.com/").description("production server"));

        return new OpenAPI()
                .info(new Info().title("JavaInUse Authentication Service"))
                .addSecurityItem(new SecurityRequirement().addList("JavaInUseSecurityScheme"))
                .components(new Components().addSecuritySchemes("JavaInUseSecurityScheme", new SecurityScheme()
                        .name("JavaInUseSecurityScheme").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));

    }

}

