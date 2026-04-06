## Autism Behavioral Tracking API — Exemplos de cURL (para Postman também)

Base URL padrão (local): `http://localhost:8080`

Headers comuns (JWT):
- Authorization: Bearer <TOKEN>
- Content-Type: application/json
- Accept: application/json
- Observação multi-tenant: o cabeçalho `X-Tenant-ID` é utilizado APENAS no login para gerar o token já vinculado ao tenant correto. Os demais endpoints não requerem esse cabeçalho (o isolamento é automático via `tenant_id` do JWT).

Observações:
- Datas: use ISO 8601.
  - Date (LocalDate): `YYYY-MM-DD` (ex.: `2026-04-02`)
  - DateTime (LocalDateTime): `YYYY-MM-DDTHH:mm:ss` (ex.: `2026-04-02T14:30:00`)
- Enums aceitos:
  - EventIntensity: `MILD`, `MODERATE`, `SEVERE`
  - DietQuality: `GOOD`, `REGULAR`, `POOR`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 1) Autenticação

Endpoint: `POST /api/auth/login`

Payload:
```json
{
  "email": "parent@example.com",
  "password": "password"
}
```

Exemplo cURL:
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "X-Tenant-ID: tenant-abc" \
  -d '{
    "email": "parent@example.com",
    "password": "password"
  }'
```

Resposta esperada:
```json
{
  "token": "JWT_AQUI"
}
```

Notas de segurança:
- O JWT é curto (15 min) e NÃO contém PHI. Claims mínimos: `sub`, `tenant_id`, `roles`, `iat`, `exp`.
- O isolamento de dados é automático por `tenant_id` via filtro do Hibernate (não é necessário enviar `X-Tenant-ID` após o login).

No Postman: use o token retornado em "Authorization" > "Bearer Token".
No bash, você pode exportar para facilitar:
```bash
TOKEN="JWT_AQUI"
```

---

### 2) Behavior Events

Base path: `/api/events`

2.1) Criar evento — `POST /api/events`
Payload:
```json
{
  "eventDateTime": "2026-04-02T14:30:00",
  "intensity": "MODERATE",
  "durationMinutes": 15,
  "antecedent": "Barulho alto antes do episódio",
  "behavior": "Agitação",
  "consequence": "Acalmou após reduzir estímulos"
}
```

Exemplo cURL:
```bash
curl -X POST "http://localhost:8080/api/events" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "eventDateTime": "2026-04-02T14:30:00",
    "intensity": "MODERATE",
    "durationMinutes": 15,
    "antecedent": "Barulho alto antes do episódio",
    "behavior": "Agitação",
    "consequence": "Acalmou após reduzir estímulos"
  }'
```

2.2) Listar eventos — `GET /api/events`
```bash
curl -X GET "http://localhost:8080/api/events" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Accept: application/json"
```

2.3) Atualizar evento — `PUT /api/events/{id}`
Payload (campos atualizáveis):
```json
{
  "intensity": "SEVERE",
  "durationMinutes": 20,
  "antecedent": "Mudança repentina de rotina",
  "behavior": "Choro e isolamento",
  "consequence": "Consolado com ambiente calmo"
}
```

Exemplo cURL:
```bash
EVENT_ID="UUID_DO_EVENTO"
curl -X PUT "http://localhost:8080/api/events/$EVENT_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "intensity": "SEVERE",
    "durationMinutes": 20,
    "antecedent": "Mudança repentina de rotina",
    "behavior": "Choro e isolamento",
    "consequence": "Consolado com ambiente calmo"
  }'
```

---

### 3) Daily Logs

Base path: `/api/daily-logs`

3.1) Criar daily log — `POST /api/daily-logs`
Payload:
```json
{
  "date": "2026-04-02",
  "sleepHours": 8,
  "dietQuality": "GOOD",
  "notes": "Dia tranquilo, sem intercorrências"
}
```

Exemplo cURL:
```bash
curl -X POST "http://localhost:8080/api/daily-logs" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "date": "2026-04-02",
    "sleepHours": 8,
    "dietQuality": "GOOD",
    "notes": "Dia tranquilo, sem intercorrências"
  }'
```

3.2) Listar daily logs (opcionalmente com filtros) — `GET /api/daily-logs`

- Sem filtros:
```bash
curl -X GET "http://localhost:8080/api/daily-logs" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Accept: application/json"
```

- Com período (`startDate` e/ou `endDate`):
```bash
curl -X GET "http://localhost:8080/api/daily-logs?startDate=2026-04-01&endDate=2026-04-30" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Accept: application/json"
```

---

### 4) Análises

Base path: `/api/events/analysis`

4.1) Gatilhos mais frequentes (palavras do antecedente) — `GET /api/events/analysis/triggers?limit=10`
```bash
curl -X GET "http://localhost:8080/api/events/analysis/triggers?limit=10" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Accept: application/json"
```

---

### Dicas para uso no Postman
- Crie uma Collection "Autism Behavioral Tracking".
- Adicione uma variável de Collection `baseUrl` = `http://localhost:8080`.
- Em Authorization da Collection, selecione "Bearer Token" e cole o `TOKEN`.
- Nos requests, use `{{baseUrl}}/api/...` e herde o Authorization da Collection.

