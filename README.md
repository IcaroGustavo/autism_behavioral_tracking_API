# autism_behavioral_tracking_API
API REST para registro de eventos comportamentais, logs diários e análises para pessoas TEA (Transtorno do Espectro Autista)

## Autism Behavioral Tracking API

API REST para registro de eventos comportamentais, logs diários e análises.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Exemplos de cURL (para testar no Postman): veja `docs/API_CURL.md`

### Autenticação
Use `POST /api/auth/login` com as credenciais seed:
- `parent@example.com` / `password`
- `therapist@example.com` / `password`

O serviço retorna um `token` (JWT). Utilize `Authorization: Bearer <TOKEN>` nos endpoints protegidos.

### Execução local
1. Java 17+ e Maven/Gradle configurados
2. Rodar a aplicação (ex.: pelo IDE ou `mvn spring-boot:run`)
3. Acessar Swagger UI para explorar e testar
