@echo off
REM Script de déploiement de la microservice ConfigService
REM Nécessite Maven et WildFly installés

echo.
echo ====================================
echo Déploiement ConfigService
echo ====================================
echo.

REM Vérifier que Maven est accessible
where mvn >nul 2>nul
if errorlevel 1 (
    echo [ERREUR] Maven n'est pas dans le PATH
    echo Assurez-vous que Maven est installé et accessible
    pause
    exit /b 1
)

REM Construire la microservice
echo [1/3] Construction de la microservice...
cd ConfigService
call mvn clean package -DskipTests
if errorlevel 1 (
    echo [ERREUR] Compilation échouée
    cd ..
    pause
    exit /b 1
)

echo [2/3] Déploiement sur WildFly...
call mvn wildfly:deploy
if errorlevel 1 (
    echo [ERREUR] Déploiement échoué
    echo Assurez-vous que WildFly est en cours d'exécution
    echo Sur Windows: $JBOSS_HOME\bin\standalone.bat
    cd ..
    pause
    exit /b 1
)

cd ..

echo.
echo [3/3] Vérification du déploiement...
timeout /t 5 /nobreak

echo.
REM Vérifier la disponibilité du service
powershell -Command "$response = try { Invoke-WebRequest -Uri 'http://localhost:8080/config-service/api/config/health' -UseBasicParsing } catch { $null }; if ($response -and $response.StatusCode -eq 200) { Write-Host '[OK] Service disponible!' -ForegroundColor Green } else { Write-Host '[ERREUR] Service indisponible' -ForegroundColor Red }"

echo.
echo ====================================
echo Déploiement terminé!
echo ====================================
echo.
echo Service disponible à:
echo   http://localhost:8080/config-service/api/config/health
echo.
pause
