# autism_behavioral_tracking_API
API REST para registro de eventos comportamentais, logs diários e análises para pessoas TEA (Transtorno do Espectro Autista)

## Autism Behavioral Tracking API

API REST para registro de eventos comportamentais, logs diários e análises.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Exemplos de cURL (para testar no Postman): veja `docs/API_CURL.md`

### Requisitos
- Para build local: JDK 21+ e Maven 3.9+
- Alternativa sem JDK local: utilizar Docker (o `Dockerfile` já compila com Temurin 21)

### Autenticação
Use `POST /api/auth/login` com as credenciais seed:
- `parent@example.com` / `password`
- `therapist@example.com` / `password`

O serviço retorna um `token` (JWT). Utilize `Authorization: Bearer <TOKEN>` nos endpoints protegidos.

### Execução local
1. Java 17+ e Maven/Gradle configurados
2. Rodar a aplicação (ex.: pelo IDE ou `mvn spring-boot:run`)
3. Acessar Swagger UI para explorar e testar

### Executar com Docker (apenas aplicação)
Compilar e rodar a aplicação (necessário banco externo acessível via variáveis):

```bash
docker build -t autism-tracker-api:latest .
docker run --name autism-tracker-app --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/autism_tracker" \
  -e SPRING_DATASOURCE_USERNAME="postgres" \
  -e SPRING_DATASOURCE_PASSWORD="postgres" \
  -e APP_SECURITY_JWT_SECRET="3q2+7wAAAAAAAAAAAAAAAAAAAAAAAABhYmNkZWZnaGlqa2xtbm9wcQ==" \
  -e APP_SECURITY_JWT_EXPIRATIONMILLIS="3600000" \
  autism-tracker-api:latest
```

### Executar com Docker Compose (app + Postgres)
Um compose foi fornecido (nome do arquivo: `Dockerfil.compose`). Para subir banco e app juntos:

```bash
docker compose -f Dockerfil.compose up -d --build
```

Isso irá:
- subir `postgres:16-alpine` em `localhost:5432` (db=autism_tracker, user=postgres, pass=postgres)
- aguardar o banco ficar saudável
- subir a aplicação em `localhost:8080`

### Scripts Windows (sem YAML)
Para quem prefere evitar YAML, há scripts `.bat`:

- Subir tudo (banco + app):

```bat
run_all.bat
```

- Derrubar tudo (parar/remover containers):

```bat
down.bat
```

Os scripts usam `docker run` e configuram as variáveis necessárias (Postgres + app). Certifique-se que o Docker Desktop esteja em execução.
