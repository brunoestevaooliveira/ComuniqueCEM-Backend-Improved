# ComuniqueCEM Backend - Versão Melhorada

Sistema de comunicação educacional moderno e robusto construído com Spring Boot 3.x, oferecendo uma arquitetura escalável e funcionalidades avançadas.

##  Funcionalidades Principais

###  Melhorias em relação à versão anterior:
- **Arquitetura Moderna**: Spring Boot 3.x com Java 17+
- **Segurança Robusta**: JWT + Spring Security 6 com autorização baseada em roles
- **Chat em Tempo Real**: WebSocket para comunicação instantânea
- **Cache Inteligente**: Redis para otimização de performance
- **Documentação Automática**: OpenAPI 3.0 (Swagger UI)
- **Testes Abrangentes**: JUnit 5 + TestContainers
- **Monitoramento**: Spring Boot Actuator + Micrometer
- **Rate Limiting**: Proteção contra ataques
- **Auditoria**: Rastreamento automático de mudanças
- **Migrations**: Flyway para versionamento de banco

###  Funcionalidades do Sistema:
- **Gestão de Instituições**: CRUD completo com hierarquia de permissões
- **Usuários Multi-Role**: Estudantes, Professores, Administradores
- **Sistema de Notícias**: Publicação e gerenciamento de conteúdo
- **Quiz Inteligente**: Sistema de questões com feedback
- **Chat Avançado**: Mensagens em tempo real com status de entrega
- **Cronograma**: Gestão de atividades e prazos
- **Upload de Arquivos**: Suporte robusto para imagens e documentos

##  Tecnologias Utilizadas

### Backend Core:
- **Spring Boot 3.2.0**: Framework principal
- **Java 17**: Linguagem de programação
- **Spring Security 6**: Autenticação e autorização
- **Spring Data JPA**: Persistência de dados
- **Spring WebSocket**: Comunicação em tempo real

### Banco de Dados:
- **PostgreSQL**: Banco principal (produção)
- **H2**: Banco em memória (testes)
- **Flyway**: Migrations e versionamento

### Cache e Performance:
- **Redis**: Cache distribuído
- **Bucket4j**: Rate limiting

### Documentação e Testes:
- **OpenAPI 3.0**: Documentação automática da API
- **JUnit 5**: Testes unitários
- **TestContainers**: Testes de integração
- **Jacoco**: Cobertura de testes

### Utilidades:
- **MapStruct**: Mapeamento de DTOs
- **SLF4J + Logback**: Logging estruturado
- **Micrometer**: Métricas e monitoramento

## Arquitetura

```
src/main/java/com/comuniquecem/
├── config/           # Configurações do Spring
├── controller/       # Controllers REST
├── dto/             # Data Transfer Objects
├── entity/          # Entidades JPA
├── exception/       # Exceções customizadas
├── mapper/          # Interfaces MapStruct
├── repository/      # Repositórios JPA
├── service/         # Lógica de negócio
├── security/        # Configurações de segurança
└── websocket/       # Configurações WebSocket
```

##  Como Executar

### Pré-requisitos:
- Java 17+
- Maven 3.8+
- PostgreSQL 12+
- Redis 6+

### Configuração do Ambiente:

1. **Clone o repositório**:
```bash
git clone <repository-url>
cd comunique-backend-improved
```

2. **Configure o banco de dados**:
```sql
CREATE DATABASE comuniquecem;
CREATE USER comunique_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE comuniquecem TO comunique_user;
```

3. **Configure as variáveis de ambiente**:
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=comuniquecem
export DB_USERNAME=comunique_user
export DB_PASSWORD=your_password
export REDIS_HOST=localhost
export REDIS_PORT=6379
export JWT_SECRET=your_jwt_secret_key_here
```

4. **Execute a aplicação**:
```bash
mvn spring-boot:run
```

### Docker (Opcional):
```bash
docker-compose up -d  # Inicia PostgreSQL e Redis
mvn spring-boot:run   # Executa a aplicação
```

##  Endpoints da API

### Documentação Interativa:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Principais Endpoints:

#### Autenticação:
- `POST /api/auth/login` - Login de usuário
- `POST /api/auth/refresh` - Renovar token
- `POST /api/auth/logout` - Logout

#### Usuários:
- `GET /api/users` - Listar usuários (paginado)
- `POST /api/users` - Criar usuário
- `PUT /api/users/{id}` - Atualizar usuário
- `DELETE /api/users/{id}` - Deletar usuário

#### Instituições:
- `GET /api/institutions` - Listar instituições
- `POST /api/institutions` - Criar instituição
- `PUT /api/institutions/{id}` - Atualizar instituição

#### Chat:
- `WebSocket /ws/chat` - Conexão WebSocket para chat
- `GET /api/chats` - Listar conversas
- `POST /api/chats` - Iniciar nova conversa

##  Testes

### Executar todos os testes:
```bash
mvn test
```

### Gerar relatório de cobertura:
```bash
mvn clean test jacoco:report
```

### Testes de integração com TestContainers:
```bash
mvn test -Dtest=*IntegrationTest
```

##  Monitoramento

### Actuator Endpoints:
- **Health**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **Info**: http://localhost:8080/actuator/info

### Logs:
- Logs estruturados em JSON
- Níveis configuráveis por ambiente
- Rastreamento de requisições com correlation ID

##  Segurança

### Autenticação:
- JWT com refresh tokens
- Expiração configurável
- Logout com blacklist de tokens

### Autorização:
- 4 níveis de acesso: STUDENT, TEACHER, ADMIN, SUPER_ADMIN
- Controle granular por endpoint
- Validação de propriedade de recursos

### Proteções:
- Rate limiting (100 req/min por IP)
- Validação rigorosa de entrada
- CORS configurado
- Headers de segurança

##  Configuração

### Profiles Disponíveis:
- `dev`: Desenvolvimento local
- `test`: Execução de testes
- `prod`: Produção

### Configurações Principais:
```yaml
# application.yml
spring:
  profiles:
    active: dev
  
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:comuniquecem}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}

jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: 86400000 # 24 hours
```

##  Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

##  Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

##  Autores

- **Desenvolvedor Principal**: Bruno - Versão melhorada e modernizada
- **Projeto Original**: Pedro Facchinetti / Gabriel Victor

---

**ComuniqueCEM v2.0** - Sistema de comunicação educacional de nova geração 🎓✨
