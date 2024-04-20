package com.fiap.techchallenger4.mscliente.infra.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("APIs do Microserviço de Clientes")
                        .version("v1")
                        .description("APIs do Microserviço de Clientes criada exclusivamente para o TechChallenge 4 da FIAP.")
                );
    }
}
