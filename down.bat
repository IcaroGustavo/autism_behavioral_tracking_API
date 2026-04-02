@echo off
echo [+] Parando e removendo containers...
docker stop autism-tracker-app 2>nul
docker rm autism-tracker-app 2>nul
docker stop autism-tracker-db 2>nul
docker rm autism-tracker-db 2>nul
echo [OK] Containers removidos.

