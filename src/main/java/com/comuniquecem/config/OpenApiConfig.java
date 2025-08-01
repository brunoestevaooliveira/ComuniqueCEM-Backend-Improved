package com.comuniquecem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do OpenAPI/Swagger para documentação da API
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ComuniqueCEM API")
                        .version("2.0.0")
                        .description("API REST moderna para sistema de comunicação educacional")
                        .contact(new Contact()
                                .name("ComuniqueCEM Team")
                                .email("suporte@comuniquecem.com")
                                .url("https://github.com/comuniquecem"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor Local"),
                        new Server()
                                .url("https://api.comuniquecem.com")
                                .description("Servidor de Produção")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT para autenticação. Format: 'Bearer {token}'")));
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("01-authentication")
                .displayName("Autenticação")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi usersApi() {
        return GroupedOpenApi.builder()
                .group("02-users")
                .displayName("Usuários")
                .pathsToMatch("/api/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi institutionsApi() {
        return GroupedOpenApi.builder()
                .group("03-institutions")
                .displayName("Instituições")
                .pathsToMatch("/api/institutions/**")
                .build();
    }

    @Bean
    public GroupedOpenApi newsApi() {
        return GroupedOpenApi.builder()
                .group("04-news")
                .displayName("Notícias")
                .pathsToMatch("/api/news/**")
                .build();
    }

    @Bean
    public GroupedOpenApi questionsApi() {
        return GroupedOpenApi.builder()
                .group("05-questions")
                .displayName("Perguntas")
                .pathsToMatch("/api/questions/**")
                .build();
    }

    @Bean
    public GroupedOpenApi schedulesApi() {
        return GroupedOpenApi.builder()
                .group("06-schedules")
                .displayName("Agendamentos")
                .pathsToMatch("/api/schedules/**")
                .build();
    }

    @Bean
    public GroupedOpenApi chatsApi() {
        return GroupedOpenApi.builder()
                .group("07-chats")
                .displayName("Chat")
                .pathsToMatch("/api/chats/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("08-admin")
                .displayName("Administração")
                .pathsToMatch("/api/admin/**")
                .build();
    }
}
