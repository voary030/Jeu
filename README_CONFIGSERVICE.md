# Intégration Microservice ConfigService

## Overview

La microservice `ConfigService` est maintenant intégrée au projet pour gérer centralement les configurations du jeu (terrain, points de vie des pièces).

## Architecture

### Composants

1. **ConfigService (Microservice)**
   - Entités JPA: `BoardConfigEntity`, `PieceHealthConfigEntity`
   - EJB Stateless: `ConfigServiceBean` - logique métier
   - REST API: `ConfigResource` - endpoints HTTP
   - Application: `ConfigApplication` - JAX-RS application

2. **Pong Game (Client)**
   - `ConfigServiceClient` - client HTTP pour communiquer avec la microservice
   - `ConfigLoader` - charge les configurations avec fallback sur fichiers CSV

## Endpoints REST

### Board Configuration

```
GET /config-service/api/config/board
  → Récupère la configuration du plateau
  
PUT /config-service/api/config/board
  → Sauvegarde la configuration du plateau
  Body: {"numberOfPawns": 8}
```

### Piece Health Configuration

```
GET /config-service/api/config/pieces
  → Récupère toutes les configurations des pièces
  
GET /config-service/api/config/pieces/{pieceName}
  → Récupère la configuration d'une pièce spécifique
  → Exemple: GET /config-service/api/config/pieces/KING
  
PUT /config-service/api/config/pieces/{pieceName}
  → Met à jour la configuration d'une pièce
  → Exemple: PUT /config-service/api/config/pieces/KING
  Body: {"healthPoints": 100}
```

### Health Check

```
GET /config-service/api/config/health
  → Vérifie la disponibilité du service
```

## Déploiement

### Option 1: WildFly (Recommandé pour EJB)

```bash
cd ConfigService
mvn clean package
mvn wildfly:deploy
```

Accédez à: `http://localhost:8080/config-service/api/config/health`

### Option 2: Tomcat (Simplifié)

Déployez le WAR généré:
```bash
cp ConfigService/target/config-service.war $CATALINA_HOME/webapps/
```

## Utilisation dans le jeu

Le `ConfigLoader` essaie maintenant d'abord de récupérer les configurations via la microservice:

```java
// Au démarrage du jeu
ConfigLoader.loadAllConfigurations();
```

### Logique de Fallback

1. **Chargement**: Service REST → Fichiers CSV → Valeurs par défaut
2. **Sauvegarde**: Service REST → Fichiers CSV

Les messages de log indiquent la source utilisée:
- `📡` = Microservice
- `⬇️` = Basculement automatique
- `✓` = Succès

## Configuration

### URL du Service

Par défaut: `http://localhost:8080/config-service/api/config`

Pour changer, modifiez `ConfigServiceClient.java`:
```java
private static final String CONFIG_SERVICE_URL = "http://votre-url/config-service/api/config";
```

### Base de Données

La microservice utilise H2 (in-memory par défaut). Pour utiliser une DB persistante:

Modifier `persistence.xml`:
```xml
<property name="javax.persistence.jdbc.url" 
          value="jdbc:h2:file:./configdb"/>
```

## Avantages

✅ **Centralisation**: Configuration partagée entre plusieurs clients  
✅ **Scalabilité**: Service indépendant, déployable séparément  
✅ **Résilience**: Fallback automatique sur fichiers CSV  
✅ **Flexibilité**: Reste compatible avec l'approche fichier  
✅ **Monitoring**: Health check pour vérifier la disponibilité  

## Dépendances

- Java 11+
- Maven
- WildFly 18+ (ou Tomcat 9+)
- H2 Database (inclus)
- Hibernate (fourni par WildFly)

## Tests

### Via cURL

```bash
# Board config
curl http://localhost:8080/config-service/api/config/board
curl -X PUT -H "Content-Type: application/json" \
  -d '{"numberOfPawns":8}' \
  http://localhost:8080/config-service/api/config/board

# Piece config
curl http://localhost:8080/config-service/api/config/pieces
curl http://localhost:8080/config-service/api/config/pieces/KING
curl -X PUT -H "Content-Type: application/json" \
  -d '{"healthPoints":100}' \
  http://localhost:8080/config-service/api/config/pieces/KING

# Health
curl http://localhost:8080/config-service/api/config/health
```

## Maintenance

### Logs
Les logs du service se trouvent dans:
- WildFly: `$JBOSS_HOME/domain/servers/*/log/`
- Tomcat: `$CATALINA_HOME/logs/`

### Base de Données
Pour réinitialiser la DB, supprimez le fichier `configdb.h2.db` et redémarrez le service.
