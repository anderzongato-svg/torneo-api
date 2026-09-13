package com.torneomalaga.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de la documentacion automatica de servicios (OpenAPI / Swagger).
 * La documentacion interactiva de cada servicio queda disponible en /docs
 * y el contrato en formato JSON en /api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI torneoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Torneo Primera Malaga")
                        .description("Servicios REST para la gestion de equipos, jugadores, "
                                + "arbitros, partidos, goles y tarjetas del torneo de futbol "
                                + "amateur del municipio de Malaga, Santander. "
                                + "Proyecto formativo SENA - ADSO.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Anderzon")
                                .email("soporte@torneoprimeramalaga.local")));
    }
}
