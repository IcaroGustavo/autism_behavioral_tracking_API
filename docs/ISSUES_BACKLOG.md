### Backlog de Issues – Autism Behavioral Tracking API

Este documento consolida as issues propostas para evolução da API, organizadas por nível de complexidade (Baixa, Média, Alta) e com uma ordem sugerida de implementação para reduzir riscos, alinhar dependências (segurança, multi-tenant, auditoria) e maximizar entrega de valor incremental.


## Ordem Sugerida de Implementação (macro roadmap)
1) Segurança – RBAC Granular por Tenant e Vínculo Terapeuta–Paciente (Alta)
2) Multi-Tenancy – Hardening e Testes de Isolamento (Média)
3) Conformidade – AuditLog Imutável (Alta)
4) Segurança – Criptografia em Repouso (Alta)
5) Autenticação – Fluxo de Refresh Token Seguro (Média)
6) ABA – Métricas Avançadas em BehaviorEvent (Média)
7) ABA – Níveis de Ajuda/Prompting e Escore de Independência (Média)
8) Módulo Multidisciplinar – Entidade SensoryProfile (Média)
9) Módulo Multidisciplinar – Registro de Comunicação Alternativa (Média)
10) Gestão de Saúde – Entidades de Medicamentos e Administração (Média)
11) Integração com PredictiveTrigger pós-Medicação (Alta)
12) Analytics – Endpoints de Agregação para Gráficos (Média)
13) Inteligência – Motor de Alerta de Risco (Alta)
14) Observabilidade – Métricas, Logs Estruturados e Tracing (Baixa)
15) Documentação – Atualização do README e API Reference (Baixa)

Justificativa resumida: começar por controles de acesso/isolamento (RBAC, multi-tenant hardening), garantir trilha de auditoria imutável e proteção de dados (encryption), estabilizar autenticação (refresh). Em seguida, evoluir o modelo clínico (ABA, multidisciplinar, saúde) e só então avançar para correlações/analytics/alertas. Observabilidade e documentação ocorrem em paralelo com o ciclo, com marcos dedicados.


## Issues por Complexidade

### Complexidade Baixa

#### 14) Observabilidade – Métricas, Logs Estruturados e Tracing
Descrição Técnica:
- Padronizar logs estruturados (JSON) com `tenantId`, `userId`, `correlationId`.
- Expor métricas (Micrometer/Prometheus): latência dos novos endpoints, erros por tenant.
- Tracing distribuído (OpenTelemetry) ao menos na borda HTTP e camada de serviço.

Checklist (Critérios de Aceitação):
- [ ] Logs estruturados com campos de contexto.
- [ ] Métricas registradas e acessíveis no endpoint `/actuator`.
- [ ] Traços exportados para o coletor configurado.
- [ ] Testes/validações em ambiente local.
- [ ] Documentação de observabilidade.


#### 15) Documentação – Atualização do README e API Reference
Descrição Técnica:
- Atualizar `README.md` e docs de API com novos recursos, entidades e exemplos de cURL.
- Adicionar seções de segurança (RBAC, criptografia, auditoria imutável).

Checklist:
- [ ] README atualizado com visão geral e links.
- [ ] `docs/API_CURL.md` com exemplos dos novos endpoints.
- [ ] Notas de migração/esquema (Flyway/Liquibase).
- [ ] Checklist de conformidade (LGPD/HIPAA) inicial.


### Complexidade Média

#### 12) Multi-Tenancy – Hardening e Testes de Isolamento
Descrição Técnica:
- Validar que todas as novas entidades/queries aplicam `tenant_id` e o Hibernate Filter.
- Adicionar testes de integração multi-tenant (dados de dois tenants e isolamento garantido).
- Garantir propagação do `tenant_id` via contexto de requisição/JWT.

Checklist:
- [ ] Migrações adicionam `tenant_id` e índices compostos quando aplicável.
- [ ] Repositórios/queries verificados para filtro tenant.
- [ ] Testes multi-tenant cobrindo leituras/escritas.
- [ ] Documentação de como prover `tenant_id` em ambientes.


#### 13) Autenticação – Fluxo de Refresh Token Seguro
Descrição Técnica:
- Manter access token 15m; adicionar `refresh token` com rotação e revogação.
- Endpoints: `POST /api/v1/auth/refresh`, blacklist/invalidations.
- Armazenar refresh tokens com fingerprint e expiração; atentar para LGPD/HIPAA (sem PHI).

Checklist:
- [ ] Endpoint de refresh com rotação e invalidação do anterior.
- [ ] Armazenamento seguro e hash/fingerprint do device/opcional.
- [ ] Testes para fluxo feliz e ataques comuns (reuse detection).
- [ ] Documentação de uso para clientes.


#### 1) ABA – Métricas Avançadas em BehaviorEvent (Frequência, Duração, Latência, IRT)
Descrição Técnica:
- Estender a entidade `BehaviorEvent` para suportar campos: `frequency` (int), `durationSeconds` (long), `latencySeconds` (long), `irtSeconds` (long).
- Precisão de segundos; validar não-negatividade e coerência (`irtSeconds` exige `frequency` > 1).
- Atualizar DTOs, mapeamentos JPA e migrações.
- Garantir respeito ao filtro de multi-tenancy existente e auditoria.

Checklist:
- [ ] Campos adicionados na entidade `BehaviorEvent` e nas migrações.
- [ ] Validações Bean Validation aplicadas e testadas.
- [ ] Endpoints `POST/PUT /api/v1/behavior-events` aceitam e persistem novos campos.
- [ ] Respostas `GET` retornam os novos campos.
- [ ] Testes unitários e de integração cobrindo casos válidos/erro.
- [ ] Multi-tenant e auditoria funcionando sem regressões.


#### 2) ABA – Níveis de Ajuda/Prompting e Escore de Independência
Descrição Técnica:
- Criar `PromptingLevel` (ENUM: FULL_PHYSICAL, PARTIAL_PHYSICAL, MODELING, GESTURAL, VERBAL, INDEPENDENT, OTHER).
- Incluir em `BehaviorEvent` os campos `promptingLevel` e `independenceScore` (0–100).
- Normalizar regras: `INDEPENDENT` implica `independenceScore` alto (ex.: ≥80).

Checklist:
- [ ] ENUM criado e persistido de forma estável (String).
- [ ] Campos adicionados em entidade/DTO/migrações.
- [ ] Validações de consistência entre `promptingLevel` e `independenceScore`.
- [ ] Endpoints `POST/PUT` aceitam/validam; `GET` retorna corretamente.
- [ ] Testes unitários/integração para as regras.
- [ ] Documentação da API atualizada.


#### 3) Módulo Multidisciplinar – Entidade SensoryProfile
Descrição Técnica:
- Criar entidade `SensoryProfile` vinculada ao `Patient` (1:N), com atributos para gatilhos e sensibilidades: `light`, `sound`, `texture`, `crowd`, `temperature`, `notes`.
- Relacionar opcionalmente `SensoryProfile` a eventos (cruzamento posterior por `patientId` + timestamp).
- CRUD completo e versionamento básico (opcional `active` + histórico).

Checklist:
- [ ] Entidade, repositório e serviço criados.
- [ ] Endpoints `GET/POST/PUT/DELETE /api/v1/sensory-profiles`.
- [ ] Respeito a multi-tenancy e RBAC (após RBAC granular).
- [ ] Migrações aplicadas.
- [ ] Testes cobrindo criação, atualização e filtros por paciente/tenant.
- [ ] Documentação da API.


#### 4) Módulo Multidisciplinar – Registro de Comunicação Alternativa (CAA/PECS)
Descrição Técnica:
- Criar entidade `AlternativeCommunicationUsage` (vinculada a `Patient` e opcionalmente a `Therapist`): `toolType` (PECS, AAC_APP, OTHER), `context`, `success`, `notes`, timestamps.
- CRUD e endpoints dedicados; relacionar com `BehaviorEvent` via `patientId` e proximidade temporal em consultas.

Checklist:
- [ ] Entidade/DTOs/migrações criadas.
- [ ] Endpoints `GET/POST/PUT/DELETE /api/v1/alt-communication`.
- [ ] Validações (ex.: `toolType` obrigatório).
- [ ] Multi-tenancy e auditoria preservados.
- [ ] Testes unitários/integração.
- [ ] Documentação atualizada.


#### 5) Gestão de Saúde – Entidades de Medicamentos e Administração
Descrição Técnica:
- Criar entidades: `Medication` (catálogo por tenant), `PatientMedication` (prescrição: dose, posologia, início/fim, médico), `MedicationAdministration` (administração efetiva: dose, via, horário, observações).
- CRUD completo; validações de datas/doses.

Checklist:
- [ ] Entidades + migrações criadas.
- [ ] Endpoints:
  - [ ] `GET/POST/PUT/DELETE /api/v1/medications`
  - [ ] `.../patient-medications`
  - [ ] `.../medication-administrations`
- [ ] Regras de negócio validadas (ex.: admin dentro do período da prescrição).
- [ ] Multi-tenant/RBAC respeitados.
- [ ] Testes cobrindo CRUD e regras.
- [ ] Documentação da API.


#### 10) Analytics – Endpoints de Agregação para Gráficos
Descrição Técnica:
- Criar endpoints somente leitura formatados para gráficos:
  - `GET /api/v1/analytics/crises-by-hour?patientId&dateRange`
  - `GET /api/v1/analytics/independence-trend?patientId&dateRange`
  - `GET /api/v1/analytics/prompting-distribution?patientId&dateRange`
- Retornos otimizados (series temporais, buckets horários), com cache por janela/tenant.

Checklist:
- [ ] Consultas agregadas implementadas e eficientes (índices necessários).
- [ ] Formato de resposta pronto para gráficos (labels, datasets).
- [ ] Cache configurado com invalidação apropriada.
- [ ] Testes cobrindo diferentes janelas e limites.
- [ ] Documentação da API com exemplos de payloads.


### Complexidade Alta

#### 9) Segurança – RBAC Granular por Tenant e Vínculo Terapeuta–Paciente
Descrição Técnica:
- Implementar RBAC com papéis: `ADMIN_TENANT`, `THERAPIST`, `SUPERVISOR`, `READ_ONLY`.
- Mapear relação `TherapistPatient` (quais pacientes um terapeuta pode acessar).
- Aplicar `@PreAuthorize`/filtros em serviços/controladores; queries sempre restritas por `tenant_id` + vínculo.

Checklist:
- [ ] Tabelas/mapeamentos de papéis e vínculos criados.
- [ ] Anotações de segurança aplicadas nos endpoints críticos.
- [ ] Testes de autorização (acesso negado fora do vínculo).
- [ ] Migração segura de usuários existentes para novos papéis.
- [ ] Documentação de papéis e matrizes de permissão.


#### 8) Conformidade – AuditLog Imutável (Append-Only)
Descrição Técnica:
- Tornar `AuditLog` imutável: bloquear `UPDATE/DELETE` via políticas do banco (trigger/política) e na aplicação (repositório/serviço).
- Opcional: encadear registros com hash (integridade) e armazenar checksum.
- Expor endpoint somente leitura para auditoria com paginação e filtros.

Checklist:
- [ ] Restrições no banco aplicadas (trigger/política).
- [ ] Camada de aplicação impede mutações indevidas.
- [ ] Campos de integridade (hash/prevHash) opcionais implementados.
- [ ] Endpoint `GET /api/v1/audit-logs` apenas leitura.
- [ ] Testes garantindo imutabilidade.
- [ ] Documentação de governança e retenção.


#### 7) Segurança – Criptografia em Repouso (Encryption at Rest) em Campos Sensíveis
Descrição Técnica:
- Definir campos sensíveis (ex.: notas clínicas, `SensoryProfile.notes`, `AlternativeCommunicationUsage.notes`, prescrições).
- Implementar crypto em nível de aplicação com `JPA AttributeConverter` + KMS/cofre (chave por tenant).
- Estratégia de rotação de chaves e migração de dados existentes.

Checklist:
- [ ] Converter(s) JPA criado(s) e aplicado(s) aos campos mapeados.
- [ ] Integração com provedor de chaves (config por ambiente).
- [ ] Migração de dados antiga → nova forma criptografada.
- [ ] Testes garantindo transparência na leitura/escrita.
- [ ] Monitoramento de falhas de decriptação e auditoria.
- [ ] Documentação de operação e rotação de chaves.


#### 6) Integração com PredictiveTrigger pós-Medicação
Descrição Técnica:
- Estender `PredictiveTrigger`/serviço correlacionando mudanças em `MedicationAdministration` com variação em `BehaviorEvent` (janela temporal configurável).
- Estratégia inicial: regras heurísticas (ex.: aumento de 30% na frequência de crises em 72h).
- Expor endpoint para consulta de correlações: `GET /api/v1/predictive/medication-effects?patientId=...`.

Checklist:
- [ ] Serviço de correlação implementado (estratégia, janela, thresholds por tenant).
- [ ] Endpoint de consulta com filtros.
- [ ] Testes com dados simulados e casos-limite.
- [ ] Observabilidade: logs métricos do motor de correlação.
- [ ] Documentação com limitações e próximos passos (ML futuro).


#### 11) Inteligência – Motor de Alerta de Risco (Sono/Alimentação x Eventos Críticos)
Descrição Técnica:
- Regra inicial: combinar logs diários (sono, alimentação) e `BehaviorEvent` críticos (crises, auto/heteroagressão) para calcular score de risco.
- Parâmetros configuráveis por tenant (ex.: horas de sono < threshold + N crises em 24h).
- Endpoints:
  - `GET /api/v1/risk/score?patientId&dateRange`
  - `POST /api/v1/risk/alerts:preview` (simulação com parâmetros).

Checklist:
- [ ] Serviço com regras configuráveis e extensíveis.
- [ ] Endpoints implementados com autenticação/tenant filter.
- [ ] Testes cobrindo cenários típicos e extremos.
- [ ] Logs/métricas para tuning das regras.
- [ ] Documentação das fórmulas e parâmetros.


## Referências
- Repositório: `https://github.com/IcaroGustavo/autism_behavioral_tracking_API`
- Issues do repositório: `https://github.com/IcaroGustavo/autism_behavioral_tracking_API/issues`

