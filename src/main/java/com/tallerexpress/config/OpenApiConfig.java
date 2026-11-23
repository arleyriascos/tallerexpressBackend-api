package com.tallerexpress.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/** Documentación Swagger con el esquema Bearer para probar desde el navegador. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tallerExpressOpenApi() {
        return new OpenAPI()
                .info(new Info().title("TallerExpress API")
                        .description("Gestión de órdenes de reparación de un taller mecánico")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearer"))
                .components(new Components().addSecuritySchemes("bearer",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")));
    }
}
