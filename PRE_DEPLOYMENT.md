# ✅ PRE-DEPLOYMENT CHECKLIST

Avant de déployer, vérifiez que tout est en place.

## 🔧 Configuration Système

### Java
```bash
java -version
# Vérifié: Java 11+? ____
```

### Maven
```bash
mvn --version
# Vérifié: Maven 3.6+? ____
```

### WildFly ou Tomcat
```bash
# WildFly: $JBOSS_HOME/bin/standalone.sh (ou .bat)
# Tomcat:  $CATALINA_HOME/bin/startup.sh (ou .bat)

# Vérifié: Serveur lancé? ____
# Vérifié: Port 8080 accessible? ____
```

## 📦 Projet - Structure

### ConfigService
```
ConfigService/
├── pom.xml                                    ✅ Présent?
├── src/main/java/com/echecpong/config/
│   ├── entity/
│   │   ├── BoardConfigEntity.java            ✅ Présent?
│   │   └── PieceHealthConfigEntity.java      ✅ Présent?
│   ├── ejb/
│   │   └── ConfigServiceBean.java            ✅ Présent?
│   └── rest/
│       ├── ConfigResource.java               ✅ Présent?
│       └── ConfigApplication.java            ✅ Présent?
├── src/main/resources/META-INF/
│   ├── persistence.xml                       ✅ Présent?
│   └── beans.xml                             ✅ Présent?
├── src/main/resources/sql/
│   └── init-config-db.sql                    ✅ Présent?
└── src/main/webapp/WEB-INF/
    ├── web.xml                               ✅ Présent?
    └── jboss-web.xml                         ✅ Présent?
```

### Pong Game
```
src/main/java/com/pong/config/
├── ConfigServiceClient.java                  ✅ Présent?
├── ConfigServiceConfig.java                  ✅ Présent?
├── ConfigServiceExample.java                 ✅ Présent?
└── ConfigLoader.java                         ✅ Modifié?
```

### Documentation
```
├── README.txt                                 ✅ Présent?
├── OVERVIEW.md                                ✅ Présent?
├── QUICKSTART.md                              ✅ Présent?
├── ARCHITECTURE.md                            ✅ Présent?
├── FILE_STRUCTURE.md                          ✅ Présent?
├── INDEX.md                                   ✅ Présent?
├── CHECKLIST.md                               ✅ Présent?
├── IMPLEMENTATION_SUMMARY.md                  ✅ Présent?
├── DELIVERY_MANIFEST.md                       ✅ Présent?
└── VISUAL_SUMMARY.md                          ✅ Présent?
```

### Scripts
```
├── deploy-config-service.bat                 ✅ Présent?
├── deploy-config-service.sh                  ✅ Présent?
└── config-service.properties                 ✅ Présent?
```

## 🔨 Compilation

### ConfigService
```bash
cd ConfigService
mvn clean package -DskipTests
# ✅ BUILD SUCCESS?
# ✅ config-service.war généré?
# Vérifié: Compilation OK? ____
```

### Pong Game
```bash
cd ..
mvn clean compile
# ✅ Pas d'erreurs?
# ✅ Pas de warnings graves?
# Vérifié: Compilation OK? ____
```

## 🚀 Déploiement

### Méthode 1: Script automatisé
```bash
./deploy-config-service.sh    # Linux/Mac
deploy-config-service.bat     # Windows

# Vérifié: Déploiement OK? ____
# Vérifié: Pas d'erreurs? ____
```

### Méthode 2: Maven
```bash
cd ConfigService
mvn wildfly:deploy
# ✅ Déploiement successful?

# Vérifié: WAR déployé? ____
```

### Méthode 3: Manuel
```bash
# WildFly
cp ConfigService/target/config-service.war $JBOSS_HOME/standalone/deployments/

# Tomcat
cp ConfigService/target/config-service.war $CATALINA_HOME/webapps/

# Vérifié: WAR copié? ____
# Vérifié: Serveur redémarré? ____
```

## ✅ Vérification du Service

### Health Check
```bash
curl http://localhost:8080/config-service/api/config/health
# Attendu: {"status":"UP","service":"ConfigService"}
# Vérifié: Health check OK? ____
```

### Board Config
```bash
curl http://localhost:8080/config-service/api/config/board
# Attendu: {"numberOfPawns":8,"configVersion":"1.0"}
# Vérifié: Board config OK? ____
```

### Pieces Config
```bash
curl http://localhost:8080/config-service/api/config/pieces
# Attendu: JSON avec KING, QUEEN, ROOK, etc.
# Vérifié: Pieces config OK? ____
```

### Specific Piece
```bash
curl http://localhost:8080/config-service/api/config/pieces/KING
# Attendu: {"pieceName":"KING","healthPoints":100}
# Vérifié: Specific piece OK? ____
```

## 🎮 Test du Jeu

### Lancer le jeu
```bash
mvn clean javafx:run
# Vérifié: Jeu démarre? ____
```

### Vérifier les logs
```
Au démarrage, vous devez voir:

📂 Chargement des configurations...
  📡 Chargement du terrain via ConfigService...
  ✓ Terrain (service): 8 pions
  📡 Chargement des HP via ConfigService...
  ✓ HP (service): Roi=100, Reine=90, Tour=50, Fou=40, Cavalier=30, Pion=10
✅ Configurations chargées avec succès!

Symboles:
  📡 = Utilise le service REST
  ⬇️  = Fallback sur CSV
  ✓  = Configuration trouvée
  ❌ = Erreur

Vérifié: Logs corrects? ____
Vérifié: Service utilisé (📡)? ____
```

### Jouer au jeu
- [ ] Jeu lance sans erreur
- [ ] Interface affichée correctement
- [ ] Pas de messages d'erreur dans la console

## 🔄 Test du Fallback

### Arrêter le service
```bash
# WildFly: Terminez le processus
# Tomcat: bin/shutdown.sh (ou .bat)

# Vérifié: Service arrêté? ____
```

### Relancer le jeu
```bash
mvn clean javafx:run
# Attendu: Logs avec ⬇️ (fallback sur CSV)

📂 Chargement des configurations...
  ⚠️ Erreur chargement via service: ...
  ⬇️ Basculement sur fichier CSV...
  ✓ Terrain (fichier): 8 pions

Vérifié: Fallback marche? ____
Vérifié: Jeu fonctionne? ____
```

### Relancer le service
```bash
# WildFly: $JBOSS_HOME/bin/standalone.sh (ou .bat)
# Tomcat: $CATALINA_HOME/bin/startup.sh (ou .bat)

# Vérifié: Service redémarré? ____
```

## 💾 Sauvegarde de Configuration

### Via UI (si implémentée)
- [ ] Accédez à ConfigScreen
- [ ] Modifiez une configuration
- [ ] Cliquez Save
- [ ] Vérifiez dans la BD
- [ ] Redémarrez le jeu - Configuration persistée?

### Via API
```bash
# Modifier le nombre de pions
curl -X PUT -H "Content-Type: application/json" \
  -d '{"numberOfPawns": 10}' \
  http://localhost:8080/config-service/api/config/board

# Vérifier
curl http://localhost:8080/config-service/api/config/board
# Attendu: {"numberOfPawns":10,...}

# Vérifié: Sauvegarde OK? ____
```

## 🐛 Dépannage Rapide

### Si erreur "Service indisponible"
- [ ] WildFly/Tomcat lancé?
- [ ] Port 8080 disponible? (netstat -an | grep 8080)
- [ ] WAR déployé? (ls deployments/)
- [ ] Logs du serveur normaux?

### Si 404 sur les endpoints
- [ ] WAR nommé "config-service.war"?
- [ ] Contexte correct (/config-service)?
- [ ] Classes compilées?
- [ ] Dépendances résolues? (mvn dependency:tree)

### Si erreur de compilation
- [ ] Java 11+? (java -version)
- [ ] Maven 3.6+? (mvn --version)
- [ ] Aucun fichier corrompu?
- [ ] Clean et rebuild: mvn clean package

### Si le fallback ne marche pas
- [ ] Fichiers CSV présents? (src/main/resources/config/)
- [ ] Chemins corrects dans ConfigLoader.java?
- [ ] Permissions de lecture?

## 📋 Formulaire de Validation

```
Date: _______________
Validé par: _______________

CONFIGURATION SYSTÈME:
  Java 11+         [ ] OUI  [ ] NON
  Maven 3.6+       [ ] OUI  [ ] NON
  Serveur lancé    [ ] OUI  [ ] NON
  Port 8080 libre  [ ] OUI  [ ] NON

COMPILATION:
  ConfigService    [ ] OK   [ ] ERREUR
  Pong Game        [ ] OK   [ ] ERREUR

DÉPLOIEMENT:
  WAR généré       [ ] OK   [ ] ERREUR
  WAR déployé      [ ] OK   [ ] ERREUR

SERVICE:
  Health check     [ ] OK   [ ] ERREUR
  Board config     [ ] OK   [ ] ERREUR
  Pieces config    [ ] OK   [ ] ERREUR

JEU:
  Démarre          [ ] OUI  [ ] NON
  Logs corrects    [ ] OUI  [ ] NON
  Service utilisé  [ ] OUI  [ ] NON
  Fallback marche  [ ] OUI  [ ] NON

SAUVEGARDE:
  REST PUT         [ ] OK   [ ] ERREUR
  Persistance      [ ] OK   [ ] ERREUR

RÉSULTAT FINAL:   [ ] ✅ PRÊT  [ ] ❌ À CORRIGER
```

## 🚨 Points Critiques

Avant d'aller en prod, vérifiez:

- [ ] ConfigService compile sans erreurs
- [ ] Service répond au health check (HTTP 200)
- [ ] Jeu démarre sans erreur
- [ ] ConfigLoader log "📡 service" au démarrage
- [ ] Sauvegarde des configs marche
- [ ] Fallback CSV fonctionne si service down
- [ ] Documentation lue et comprise
- [ ] Pas de warnings dans les logs

## ✅ Checkpoint Final

```
Avant de lancer en production:

TOUT COMPLET?
  [ ] Tous les fichiers présents
  [ ] Compilation OK
  [ ] Déploiement OK
  [ ] Tests passent

DOCUMENTATION?
  [ ] README.txt
  [ ] QUICKSTART.md
  [ ] ARCHITECTURE.md

SUPPORT?
  [ ] INDEX.md consultable
  [ ] TROUBLESHOOTING disponible
  [ ] Exemples fournis

QUALITÉ?
  [ ] Code commenté
  [ ] Pas d'erreurs
  [ ] Logs explicites

STATUS: _________________
        (DÉPLOYER / CORRIGER)
```

---

## 🎯 Conclusion

Vous êtes prêt si et seulement si:
1. ✅ Tous les fichiers sont présents
2. ✅ Compilation sans erreurs
3. ✅ Service répond au health check
4. ✅ Jeu démarre et charge les configs
5. ✅ Fallback marche

Une fois validé, vous êtes **🟢 PRÊT POUR LA PRODUCTION**.

Consultez [QUICKSTART.md](QUICKSTART.md) si vous avez besoin d'aide.

Bonne chance! 🚀
