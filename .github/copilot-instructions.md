<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# ComuniqueCEM Backend - Copilot Instructions

Este é um projeto Spring Boot moderno para um sistema de comunicação educacional com as seguintes características:

## Arquitetura e Padrões
- Utiliza Spring Boot 3.x com Java 17+
- Arquitetura em camadas (Controller, Service, Repository)
- Padrões DTO com MapStruct para mapeamento
- Separação clara de responsabilidades

## Segurança
- Spring Security 6 com JWT para autenticação
- Autorização baseada em roles (STUDENT, TEACHER, ADMIN, SUPER_ADMIN)
- Rate limiting para proteção contra ataques
- Validação robusta com Bean Validation

## Tecnologias Principais
- Spring Boot 3.x
- Spring Security 6
- Spring Data JPA
- PostgreSQL (produção) / H2 (testes)
- Redis para cache
- WebSocket para chat em tempo real
- Flyway para migrations
- TestContainers para testes de integração

## Convenções de Código
- Use nomes em inglês para classes, métodos e variáveis
- Aplique validações apropriadas nos DTOs
- Implemente tratamento de exceções personalizado
- Use logs estruturados com SLF4J
- Escreva testes abrangentes (unitários e integração)

## Estrutura de Pacotes
- `config`: Configurações do Spring
- `controller`: Controllers REST
- `dto`: Data Transfer Objects
- `entity`: Entidades JPA
- `exception`: Exceções customizadas
- `mapper`: Interfaces MapStruct
- `repository`: Repositórios JPA
- `service`: Lógica de negócio
- `security`: Configurações de segurança
- `websocket`: Configurações WebSocket

## Boas Práticas
- Use ResponseEntity para responses HTTP
- Implemente paginação quando apropriado
- Cache dados frequentemente acessados
- Monitore a aplicação com Actuator
- Documente APIs com OpenAPI/Swagger
