#!/bin/bash

# Script de déploiement de la microservice ConfigService
# Nécessite Maven et WildFly installés

echo ""
echo "===================================="
echo "Déploiement ConfigService"
echo "===================================="
echo ""

# Vérifier que Maven est accessible
if ! command -v mvn &> /dev/null; then
    echo "[ERREUR] Maven n'est pas dans le PATH"
    echo "Assurez-vous que Maven est installé et accessible"
    exit 1
fi

# Construire la microservice
echo "[1/3] Construction de la microservice..."
cd ConfigService
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "[ERREUR] Compilation échouée"
    cd ..
    exit 1
fi

echo "[2/3] Déploiement sur WildFly..."
mvn wildfly:deploy

if [ $? -ne 0 ]; then
    echo "[ERREUR] Déploiement échoué"
    echo "Assurez-vous que WildFly est en cours d'exécution"
    echo "Démarrez WildFly avec: \$JBOSS_HOME/bin/standalone.sh"
    cd ..
    exit 1
fi

cd ..

echo ""
echo "[3/3] Vérification du déploiement..."
sleep 5

echo ""
# Vérifier la disponibilité du service
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/config-service/api/config/health)

if [ "$HTTP_CODE" == "200" ]; then
    echo "[OK] Service disponible!" 
else
    echo "[ERREUR] Service indisponible (Code HTTP: $HTTP_CODE)"
fi

echo ""
echo "===================================="
echo "Déploiement terminé!"
echo "===================================="
echo ""
echo "Service disponible à:"
echo "  http://localhost:8080/config-service/api/config/health"
echo ""
