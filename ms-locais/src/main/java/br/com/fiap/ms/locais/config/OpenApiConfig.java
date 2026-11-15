package br.com.fiap.ms.locais.config;

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
                        .title("Microsserviço de Locais")
                        .description("API responsável pelo mapeamento de locais e seus recursos de acessibilidade")
                        .version("v1"))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Direto na instância (porta aleatória)"),
                        new Server()
                                .url("/ms-locais")
                                .description("Via API Gateway (porta 8082)")
                ));
    }
}
