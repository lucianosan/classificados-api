# Classificados API

## Visão Geral

- API REST para anúncios classificados com autenticação JWT, favoritos e IDs públicos curtos.
- Stack: Spring Boot 3.3, Java 17, Spring Security, JPA/Hibernate, PostgreSQL, Springdoc OpenAPI.
- Gera `public_id` (10 caracteres) para uso em URLs curtas, mantendo `UUID` como chave primária.

## Requisitos

- Java 17 (configure `JAVA_HOME` para JDK 17 e `java -version` deve indicar 17)
- Maven 3.8+
- PostgreSQL 13+

## Configuração

- Variáveis de ambiente suportadas:
  - `DB_URL` (default `jdbc:postgresql://localhost:5432/postgres`)
  - `DB_USERNAME` (default `postgres`)
  - `DB_PASSWORD` (default `postgres`)
  - `JWT_SECRET` (default `dev-secret`)
- Porta padrão do servidor: `8080`.
- Arquivo de configuração: `src/main/resources/application.yml`.

## Banco de Dados

- DDL principal em `src/main/resources/db/ddl.sql`.
- Seed seguro e idempotente em `src/main/resources/db/seed.sql`.
- Inicialização mínima automática em runtime via `DataInitializer` para criar admin e alguns anúncios se vazio (`src/main/java/com/example/classificados/config/DataInitializer.java`).

## Execução

- Desenvolvimento: `mvn spring-boot:run`
- Produção (jar):
  - `mvn clean package`
  - `java -jar target/classificados-api-0.0.1.jar`

## Documentação e Swagger

- OpenAPI: `GET /v3/api-docs`
- Swagger UI: `GET /swagger-ui.html`

## Autenticação

- Fluxo: registro e login retornam `token` JWT. Envie `Authorization: Bearer <token>`.
- Claims: `sub` (UUID do usuário), `role`.
- Regras de acesso:
  - Público: `GET /api/listings`, `GET /api/listings/{id}`, `GET /api/categories`, `POST /api/listings/{id}/views`, `/api/auth/**`, `/swagger-ui*`, `/v3/api-docs/**`.
  - Protegido: demais endpoints exigem JWT.

## Endpoints

- Autenticação (`src/main/java/com/example/classificados/web/AuthController.java`)
  - `POST /api/auth/register`
    - body: `{ "name": "Ana", "email": "ana@example.com", "password": "123456" }`
    - resp: `{ token, user: { id, name, email, role } }`
- `POST /api/auth/login`
  - body: `{ "email": "ana@example.com", "password": "123456" }`
  - resp: `{ token, user: { id, name, email, role }, geo: { ip, country, region, city, lat, lon } }`
  - `GET /api/auth/me` (com JWT)
    - resp: `{ id, name, email, role }`

- Categorias (`src/main/java/com/example/classificados/web/CategoryController.java`)
  - `GET /api/categories`
    - resp: lista de categorias.

- Anúncios (`src/main/java/com/example/classificados/web/ListingController.java`)
  - `GET /api/listings`
    - params: `q`, `category`, `city`, `favoritesOnly` (bool), `userId` (UUID), `page`, `size`
    - resp: `{ items: [ ... ], total }`
  - `GET /api/listings?id={id}` ou `GET /api/listings/{id}`
    - aceita `public_id` (≤ 10 chars) ou `UUID`.
  - `POST /api/listings` (com JWT)
    - body: `{ "ownerId": "<UUID opcional>", "title": "...", "description": "...", "price": 100.00, "category": "...", "city": "...", "state": "...", "contactPhone": "...", "images": ["url1", "url2"] }`
    - resp: DTO do anúncio (inclui `id` curto em `id` e `uid` com UUID).
  - `POST /api/listings/{id}/views`
    - incrementa `views` por `UUID`.
  - `DELETE /api/listings/{id}` (com JWT)
    - remove por `UUID`.

- Favoritos (`src/main/java/com/example/classificados/web/FavoriteController.java`)
  - `GET /api/users/{userId}/favorites` (com JWT)
    - resp: lista de `{ listingId: UUID }` do usuário.
  - `POST /api/users/{userId}/favorites/{listingId}` (com JWT)
  - `DELETE /api/users/{userId}/favorites/{listingId}` (com JWT)
  - Observação: favoritos usam `listingId` como `UUID` (não `public_id`).

## IDs curtos (`public_id`)

- Campo `public_id` em `listings` (10 chars, único), exposto como `id` nas respostas.
- Geração:
  - Novo anúncio: aleatório alfanumérico com verificação de unicidade (`ListingService.java`).
  - Migração/seed: derivado dos primeiros 10 caracteres do `UUID` sem hífen (`seed.sql` e seed frontend).

## Exemplos (curl)

- Registro:
  - `curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d '{"name":"Ana","email":"ana@example.com","password":"123456"}'`
- Login:
  - `curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"email":"ana@example.com","password":"123456"}'`
- Criar anúncio (JWT em `TOKEN`):
  - `curl -X POST http://localhost:8080/api/listings -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"Bicicleta","description":"Aro 29","price":1200.00,"category":"Esportes","city":"São Paulo","state":"SP","contactPhone":"11999999999","images":["/assets/bike.jpg"]}'`
- Buscar anúncios:
  - `curl 'http://localhost:8080/api/listings?q=bike&city=São Paulo&page=0&size=12'`
- Obter por ID curto:
  - `curl 'http://localhost:8080/api/listings/abcdefghij'`
- Incrementar views (UUID):
  - `curl -X POST 'http://localhost:8080/api/listings/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/views'`
- Favoritos (UUIDs):
  - `curl -X POST -H "Authorization: Bearer $TOKEN" 'http://localhost:8080/api/users/<user-uuid>/favorites/<listing-uuid>'`

## Seeds e Dados de Exemplo

- Executar DDL/Seed manualmente no banco (opcional):
  - `psql < src/main/resources/db/ddl.sql`
  - `psql < src/main/resources/db/seed.sql`
- Em runtime, `DataInitializer` cria admin `admin@site.com` (senha `admin123`) e 2 anúncios se o banco estiver vazio.

## Build, Testes e Qualidade

- Build: `mvn clean package`
- Artefato: `target/classificados-api-0.0.1.jar`
- Testes: `mvn test`
  - Serviços: `AuthServiceTest`, `ListingServiceTest`
  - Web (MockMvc standalone): `ListingControllerTest`
  - Executar teste específico: `mvn -Dtest=com.example.classificados.service.AuthServiceTest test`
  - Relatórios: `target/surefire-reports/`

### Cobertura (JaCoCo)

- Executar: `mvn verify`
- Relatório: `target/site/jacoco/index.html`
- Artefato de CI: publicado como `jacoco-report` na pipeline

### Integração Contínua (GitHub Actions)

- Workflow: `.github/workflows/ci.yml`
- Jobs: build+test+coverage em `ubuntu-latest` com JDK 17
- Passos: checkout, setup-java, `mvn verify`, upload do relatório JaCoCo

## Solução de Problemas

- Erro de coluna ausente (`role`, `public_id`): executar `ddl.sql`/`seed.sql` ou deixar o JPA atualizar (`spring.jpa.hibernate.ddl-auto=update`).
- `password_hash` nulo: seeds atualizados usam SHA-256 compatível com backend.
- `public_id` inexistente em dados antigos: seeds adicionam a coluna e populam a partir do `UUID`.

## Licença

- Uso interno de exemplo. Adapte conforme sua necessidade.

## Boas Práticas

- Configurar `JWT_SECRET` seguro em produção.
- Usar migrações consistentes: aplicar `ddl.sql` antes de `seed.sql` em ambientes novos.
- Controlar CORS no gateway/frontend; em dev está liberado (`@CrossOrigin("*")`).
- Validar entrada no frontend; o backend não inclui validações complexas nos DTOs.
- Evitar expor `UUID` publicamente; usar `public_id` nas URLs.
- Monitorar `views` e `is_active` para moderar conteúdo.

## Roadmap

- Suporte a favoritos por `public_id` além de `UUID`.
- Paginação real no repositório (`Pageable`) em `/api/listings`.
- Upload de imagens e armazenamento externo (S3/minio).
- Filtro por `state` com index dedicado.
- Perfis de ambiente (`application-dev.yml`, `application-prod.yml`).
- Testes automatizados (unitários e de integração).

## Como Contribuir

- Abrir issues descrevendo a melhoria ou bug.
- Seguir padrão de código do projeto e mensagens de commit claras.
- Incluir testes quando alterar regras de negócio.
- Validar manualmente endpoints com Swagger antes de abrir PR.

## Arquitetura

```mermaid
flowchart LR
  A[Frontend (classificados-web)] -- REST --> B[API (Spring Boot)]
  B -- JPA/Hibernate --> C[(PostgreSQL)]
  B -- JWT (assinatura/verificação) --> D[JwtUtil]
  subgraph Config
    E[application.yml]
    F[Env: DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET]
  end
  B --- E
  E --- F
```

## Endpoints (Resumo)

| Método | Caminho | Autenticação | Descrição |
|--------|---------|--------------|-----------|
| GET | `/swagger-ui.html` | Público | UI para explorar a API |
| GET | `/v3/api-docs` | Público | Esquema OpenAPI JSON |
| POST | `/api/auth/register` | Público | Registrar usuário |
| POST | `/api/auth/login` | Público | Login e obtenção de JWT |
| GET | `/api/auth/me` | JWT | Dados do usuário autenticado |
| GET | `/api/categories` | Público | Listar categorias |
| GET | `/api/listings` | Público | Buscar anúncios (filtros e paginação simples) |
| GET | `/api/listings?id={id}` | Público | Obter anúncio por `public_id` (≤10) ou `UUID` |
| GET | `/api/listings/{id}` | Público | Obter anúncio por `public_id` (≤10) ou `UUID` |
| POST | `/api/listings` | JWT | Criar anúncio |
| POST | `/api/listings/{id}/views` | Público | Incrementar `views` por `UUID` |
| DELETE | `/api/listings/{id}` | JWT | Remover anúncio por `UUID` |
| GET | `/api/users/{userId}/favorites` | JWT | Listar favoritos do usuário (por `UUID`) |
| POST | `/api/users/{userId}/favorites/{listingId}` | JWT | Adicionar favorito (por `UUID`) |
| DELETE | `/api/users/{userId}/favorites/{listingId}` | JWT | Remover favorito (por `UUID`) |

Observações:
- `public_id` é aceito nos GET de anúncios, mas favoritos usam `UUID`.
- A lista de anúncios retorna `id` como `public_id` quando disponível e `uid` como `UUID` sempre.

## Modelo de Dados

```mermaid
erDiagram
  USERS ||--o{ LISTINGS : owns
  LISTINGS ||--o{ LISTING_IMAGES : has
  USERS ||--o{ FAVORITES : marks
  LISTINGS ||--o{ FAVORITES : favorited

  USERS {
    UUID id PK
    TEXT name
    TEXT email UNIQUE
    TEXT password_hash
    TIMESTAMP created_at
    TEXT role
  }
  LISTINGS {
    UUID id PK
    TEXT public_id UNIQUE
    UUID owner_id FK
    TEXT title
    TEXT description
    NUMERIC price
    TEXT category
    TEXT city
    TEXT state
    TEXT contact_phone
    TIMESTAMP created_at
    INT views
    BOOLEAN is_active
  }
  LISTING_IMAGES {
    BIGSERIAL id PK
    UUID listing_id FK
    TEXT url
  }
  FAVORITES {
    BIGSERIAL id PK
    UUID user_id FK
    UUID listing_id FK
  }
```
