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

### Multi‑tenancy (Shared DB + Discriminator)
- Todas as entidades do domínio herdam `BaseTenantEntity` e possuem `tenant_id`. O isolamento é aplicado automaticamente via Hibernate Filter.
- O `tenant_id` é propagado no contexto pelo `JwtAuthenticationFilter` lendo o claim `tenant_id` do JWT.
- Durante o login, informe o cabeçalho `X-Tenant-ID` para incluir o tenant correto no token:
  - Exemplo: `POST /api/auth/login` com header `X-Tenant-ID: tenant-abc`.
- Nunca inclua PHI no token. O token contém apenas: `sub`, `tenant_id`, `roles`, `iat`, `exp`.

### Segurança (JWT curto e sem PHI)
- Access Token com TTL curto (15 minutos): `app.security.jwt.expirationMillis=900000`.
- O token é assinado HS256 com segredo base64 em `app.security.jwt.secret`.
- Não há PHI, apenas metadados mínimos para autorização e isolamento.

### Auditoria
- `AuditLog` registra eventos sensíveis (`who/when/what`) de forma append-only.
- `AuditingConfig` + `AuditorAwareImpl` preenchem automaticamente o “quem” via `SecurityContext`.
- Recomendado: armazenar cópias WORM (S3/GCS) e bloquear UPDATE/DELETE em `audit_logs` via política/trigger.

### Entidades avançadas
- `PhysiologicalMetric`: integra dados de wearables (HR, HRV, SpO2, etc.).
- `PredictiveTrigger`: correlaciona ABC (Antecedent/Behavior/Consequence) com fatores ambientais (sono, dieta, clima).

### Testes
- Adicionados testes unitários para:
  - `JwtService` (claims mínimos e extração de `tenant_id`)
  - `AuditLogger` (persistência com `tenant_id` do contexto)
  - `TenantFilterActivationFilter` (habilitação do filtro Hibernate por requisição)


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

### Executar com Docker Compose (app + Postgres + ngrok)
Um compose padrão foi adicionado (`docker-compose.yml`). Para subir banco, app e ngrok juntos:

```bash
cd C:\java\Estudos\projeto_autismo_back
set NGROK_AUTHTOKEN=SEU_TOKEN_NGROK
docker compose up -d --build
```

Isso irá:
- subir `postgres:16-alpine` em `localhost:5432` (db=autism_tracker, user=postgres, pass=postgres);
- aguardar o banco ficar saudável;
- subir a aplicação em `localhost:8080`;
- iniciar o túnel `ngrok` apontando para `app:8080` (dashboard em `http://localhost:4040`).

Para ver a URL pública do ngrok (HTTPS), acesse `http://localhost:4040` e copie o campo `Forwarding`.
No app mobile, em “Host customizado”, utilize a URL com sufixo `/api`, por exemplo:
`https://<subdominio>.ngrok-free.app/api`.

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

### Testes
Para executar os testes localmente:

```bash
mvn -q -e test
```

Observação: o build de imagem Docker usa `-Dmaven.test.skip=true` para agilizar empacotamento do container.
