package br.com.fiap.ms.avaliacoes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microsserviço de Avaliações")
                        .description("API responsável pelas avaliações colaborativas de acessibilidade dos locais")
                        .version("v1"))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Direto na instância (porta aleatória)"),
                        new Server()
                                .url("/ms-avaliacoes")
                                .description("Via API Gateway (porta 8082)")
                ));
    }
}
