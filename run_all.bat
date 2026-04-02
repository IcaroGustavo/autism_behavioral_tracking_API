@echo off
setlocal ENABLEDELAYEDEXPANSION

echo [+] Subindo Postgres...
docker run -d --name autism-tracker-db ^
  -e POSTGRES_DB=autism_tracker ^
  -e POSTGRES_USER=postgres ^
  -e POSTGRES_PASSWORD=postgres ^
  -p 5432:5432 ^
  -v pgdata:/var/lib/postgresql/data ^
  postgres:16-alpine

if errorlevel 1 (
  echo [!] Falha ao subir o Postgres. Verifique o Docker Desktop e tente novamente.
  exit /b 1
)

echo [+] Buildando imagem da aplicacao...
docker build -t autism-tracker-api:latest .
if errorlevel 1 (
  echo [!] Falha no build da imagem.
  exit /b 1
)

echo [+] Subindo aplicacao...
docker run -d --name autism-tracker-app ^
  --link autism-tracker-db:db ^
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/autism_tracker ^
  -e SPRING_DATASOURCE_USERNAME=postgres ^
  -e SPRING_DATASOURCE_PASSWORD=postgres ^
  -e APP_SECURITY_JWT_SECRET=3q2+7wAAAAAAAAAAAAAAAAAAAAAAAABhYmNkZWZnaGlqa2xtbm9wcQ== ^
  -e APP_SECURITY_JWT_EXPIRATIONMILLIS=3600000 ^
  -p 8080:8080 ^
  autism-tracker-api:latest

if errorlevel 1 (
  echo [!] Falha ao subir a aplicacao.
  exit /b 1
)

echo [OK] Aplicacao em execucao em http://localhost:8080
echo [OK] Swagger: http://localhost:8080/swagger-ui.html
endlocal

