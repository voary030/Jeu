# 🚀 Guide de Démarrage Rapide - ConfigService

## Installation et Déploiement en 5 minutes

### Prérequis
- Java 11 ou supérieur
- Maven 3.6+
- WildFly 18+ (ou Tomcat 9+)

### Étape 1: Compiler la microservice

```bash
cd ConfigService
mvn clean package -DskipTests
```

**Résultat attendu**: `ConfigService/target/config-service.war`

### Étape 2: Démarrer le serveur d'application

#### Si WildFly:
```bash
# Windows
$JBOSS_HOME\bin\standalone.bat

# Linux/Mac
$JBOSS_HOME/bin/standalone.sh
```

#### Si Tomcat:
```bash
# Windows
$CATALINA_HOME\bin\startup.bat

# Linux/Mac
$CATALINA_HOME/bin/startup.sh
```

### Étape 3: Déployer la microservice

#### Option A: Avec Maven (WildFly)
```bash
cd ConfigService
mvn wildfly:deploy
```

#### Option B: Copier le WAR
```bash
# WildFly
cp ConfigService/target/config-service.war $JBOSS_HOME/standalone/deployments/

# Tomcat
cp ConfigService/target/config-service.war $CATALINA_HOME/webapps/
```

### Étape 4: Vérifier le déploiement

```bash
# Avec curl
curl http://localhost:8080/config-service/api/config/health

# Attendu: {"status":"UP","service":"ConfigService"}
```

### Étape 5: Lancer le jeu

```bash
cd ..
mvn clean javafx:run
```

## ✅ Vérifications

### Service est actif ?
```bash
curl http://localhost:8080/config-service/api/config/health
```
**Réponse**: `{"status":"UP","service":"ConfigService"}` ✅

### Configuration du plateau ?
```bash
curl http://localhost:8080/config-service/api/config/board
```
**Réponse**: `{"numberOfPawns":8,"configVersion":"1.0"}` ✅

### Santé d'une pièce ?
```bash
curl http://localhost:8080/config-service/api/config/pieces/KING
```
**Réponse**: `{"pieceName":"KING","healthPoints":100}` ✅

## 📊 Logs de Vérification

Au démarrage du jeu, vous verrez:

```
📂 Chargement des configurations...
  📡 Chargement du terrain via ConfigService...
  ✓ Terrain (service): 8 pions
  📡 Chargement des HP via ConfigService...
  ✓ HP (service): Roi=100, Reine=90, Tour=50, Fou=40, Cavalier=30, Pion=10
✅ Configurations chargées avec succès!
```

Si le service est indisponible:
```
📂 Chargement des configurations...
  ⚠️ Erreur chargement via service: ...
  ⬇️ Basculement sur fichier CSV...
  ✓ Terrain (fichier): 8 pions
✅ Configurations chargées avec succès!
```

## 🔧 Modification de Configurations

### Via l'API REST

```bash
# Modifier le nombre de pions
curl -X PUT -H "Content-Type: application/json" \
  -d '{"numberOfPawns": 10}' \
  http://localhost:8080/config-service/api/config/board

# Modifier les PV du Roi
curl -X PUT -H "Content-Type: application/json" \
  -d '{"healthPoints": 150}' \
  http://localhost:8080/config-service/api/config/pieces/KING
```

### Via le code Java

```java
// Dans ConfigLoader.java ou votre code
ConfigServiceClient.saveBoardConfig(10);
ConfigServiceClient.savePieceHealthConfig("KING", 150);
```

## 🐛 Troubleshooting

### Le jeu dit "Service indisponible"

1. **Vérifier WildFly/Tomcat est lancé**
   ```bash
   netstat -an | grep 8080
   ```

2. **Vérifier le WAR est déployé**
   - WildFly: vérifier dans `$JBOSS_HOME/standalone/deployments/`
   - Tomcat: vérifier dans `$CATALINA_HOME/webapps/`

3. **Consulter les logs**
   - WildFly: `$JBOSS_HOME/standalone/log/server.log`
   - Tomcat: `$CATALINA_HOME/logs/catalina.out`

### Erreur 404 sur les endpoints

- Vérifier l'URL: `http://localhost:8080/config-service/api/config/...`
- Vérifier que le WAR est nommé `config-service.war`
- Redémarrer le serveur

### Erreur de compilation

```bash
# Nettoyer et réessayer
cd ConfigService
mvn clean install -U
```

## 📚 Ressources

- [README_CONFIGSERVICE.md](README_CONFIGSERVICE.md) - Documentation complète
- [Javadoc des classes](#) - Documentation du code
- [Exemples d'utilisation](#) - ConfigServiceExample.java

## 🎯 Prochaines étapes

1. ✅ Service déployé et actif
2. ✅ Jeu charge les configurations
3. 🔜 Intégrer les écrans de configuration dans l'UI
4. 🔜 Ajouter la persistance en base de données
5. 🔜 Implémenter la synchronisation multi-joueurs

---

**Besoin d'aide ?** Consultez les logs ou lancez `ConfigServiceExample.java` pour voir les appels API en action.
