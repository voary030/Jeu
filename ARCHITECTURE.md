# 🏗️ Architecture - ConfigService Integration

## Vue d'ensemble

```
┌─────────────────────────────────────────────────────────────┐
│                     Pong Game Client                         │
│  (JavaFX Application - src/main/java/com/pong/)              │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  Main.java ──────────> ConfigLoader ──────────────────┐      │
│                             ▲                         │      │
│  BoardConfig              │                         │      │
│  PieceHealthConfig        │                         ▼      │
│  GameController   <────────┼──> ConfigServiceClient  │      │
│  etc.                       │         ▲             │      │
│                             │         │             │      │
│                ┌────────────┴─────────┴─────────┐   │      │
│                │                                  │   │      │
│         Try HTTP Request                  Fallback  │      │
│                │                                  │   │      │
│                ▼                                  ▼   ▼      │
│          REST API Call                    CSV Files         │
│          (JSON)                           (Resources)       │
└────────────────┬──────────────────────────────────────────┘
                 │
                 │ HTTP (Port 8080)
                 │
         ┌───────▼────────────────────────────┐
         │      ConfigService Microservice     │
         │   (WildFly/Tomcat - Port 8080)      │
         ├────────────────────────────────────┤
         │                                    │
         │  ConfigResource (REST API)         │
         │  - GET  /api/config/board          │
         │  - PUT  /api/config/board          │
         │  - GET  /api/config/pieces         │
         │  - PUT  /api/config/pieces/{name}  │
         │  - GET  /api/config/health         │
         │                                    │
         ├────────────────────────────────────┤
         │  ConfigServiceBean (EJB)           │
         │  - Logique métier                  │
         │  - Gestion des configurations      │
         │                                    │
         ├────────────────────────────────────┤
         │  JPA Entities                      │
         │  - BoardConfigEntity               │
         │  - PieceHealthConfigEntity         │
         │                                    │
         ├────────────────────────────────────┤
         │  Database (H2)                     │
         │  - board_config                    │
         │  - piece_health_config             │
         └────────────────────────────────────┘
```

## Composants

### Client (Pong Game)

#### 1. **ConfigServiceClient.java**
- **Rôle**: Client HTTP pour communiquer avec le service
- **Responsabilités**:
  - Envoyer les requêtes HTTP au service
  - Parser les réponses JSON
  - Gérer les erreurs de connexion
  - Vérifier la santé du service

**Méthodes clés**:
```java
checkServiceHealth()              // Vérifie la disponibilité
getBoardConfig()                  // GET /config/board
saveBoardConfig(int)              // PUT /config/board
getAllPieceHealthConfig()         // GET /config/pieces
getPieceHealthConfig(String)      // GET /config/pieces/{name}
savePieceHealthConfig(String, int) // PUT /config/pieces/{name}
isServiceAvailable()              // Vérifie l'état du service
```

#### 2. **ConfigServiceConfig.java**
- **Rôle**: Centralisé la configuration
- **Contient**:
  - URL du service
  - Timeouts et retries
  - Valeurs par défaut
  - Paramètres de log

#### 3. **ConfigLoader.java** (Modifié)
- **Rôle**: Charge les configurations au démarrage
- **Stratégie**:
  1. Essaie le service REST
  2. Bascule sur CSV si indisponible
  3. Utilise les valeurs par défaut en dernier recours

### Microservice (ConfigService)

#### 1. **ConfigResource.java** (REST API)
- **Rôle**: Endpoints HTTP pour accéder aux configurations
- **Endpoints**:
```
GET  /config/board           → Récupère la config du plateau
PUT  /config/board           → Sauvegarde la config du plateau
GET  /config/pieces          → Récupère toutes les configs des pièces
GET  /config/pieces/{name}   → Récupère une config de pièce
PUT  /config/pieces/{name}   → Sauvegarde une config de pièce
GET  /config/health          → Santé du service
```

#### 2. **ConfigServiceBean.java** (EJB Stateless)
- **Rôle**: Logique métier
- **Responsabilités**:
  - Requêtes JPA
  - Gestion des configurations
  - Valeurs par défaut

#### 3. **Entities**
- **BoardConfigEntity**: Configuration du plateau
  - `numberOfPawns`: Nombre de pions
  - `configVersion`: Version de la configuration

- **PieceHealthConfigEntity**: Configuration des pièces
  - `pieceName`: Nom de la pièce (KING, QUEEN, etc.)
  - `healthPoints`: Points de vie
  - `configVersion`: Version de la configuration

#### 4. **ConfigApplication.java** (JAX-RS)
- **Rôle**: Point d'entrée de l'application
- **Chemin**: `/api`

## Flux de données

### Chargement au démarrage

```
Game Start
    ↓
ConfigLoader.loadAllConfigurations()
    ↓
├─→ loadBoardConfig()
│   ├─→ ConfigServiceClient.isServiceAvailable()
│   │   └─→ Service UP? 
│   │       ├─ YES: getBoardConfig() → REST API
│   │       └─ NO: loadCSV() → Fichier CSV
│   └─→ BoardConfig.setNumberOfPawns()
│
└─→ loadPieceHealthConfig()
    ├─→ ConfigServiceClient.isServiceAvailable()
    │   └─→ Service UP?
    │       ├─ YES: getAllPieceHealthConfig() → REST API
    │       └─ NO: loadCSV() → Fichier CSV
    └─→ PieceHealthConfig.set*Health()
```

### Sauvegarde (Exemple: BoardConfig)

```
BoardConfigScreen → Save Button
    ↓
ConfigLoader.saveBoardConfig()
    ↓
├─→ Service disponible?
│   ├─ YES: ConfigServiceClient.saveBoardConfig() → REST PUT
│   └─ NO: Sauvegarde CSV + fallback
└─→ Afficher message de succès
```

## Résilience et Fallback

### Stratégie de Fallback à 3 niveaux

```
┌─────────────────────┐
│   Niveau 1: REST    │  ← Service ConfigService (HTTP)
└──────────┬──────────┘
           │ Erreur de connexion
           ▼
┌─────────────────────┐
│   Niveau 2: CSV     │  ← Fichiers src/main/resources/config/
└──────────┬──────────┘
           │ Fichier absent
           ▼
┌─────────────────────┐
│   Niveau 3: Default │  ← Valeurs codées en dur
└─────────────────────┘
```

### Avantages

✅ **Haute disponibilité**: Fonctionne même sans le service  
✅ **Transparent**: L'application choisit automatiquement  
✅ **Évolutif**: Peut passer à d'autres sources (LDAP, HTTPS, etc.)  
✅ **Testable**: Peut simuler l'indisponibilité du service  

## Sécurité

### État Actuel
- Pas d'authentification (local/trusted network)
- Pas de chiffrement (localhost)
- Pas de validation avancée

### Recommandations futures
- [ ] Ajouter JWT/OAuth2
- [ ] Utiliser HTTPS/TLS
- [ ] Valider les entrées strictement
- [ ] Logger les accès
- [ ] Rate limiting
- [ ] CORS si accès multi-domaine

## Performance

### Optimisations actuelles
- Singleton HttpClient (réutilisé)
- Vérification de santé au démarrage
- Pas de polling (requêtes à la demande)

### Améliorations possibles
- [ ] Cache les réponses (TTL)
- [ ] Compression GZIP
- [ ] Batch les requêtes
- [ ] WebSocket pour sync temps réel

## Déploiement

### Architecture actuelle
```
Local Development
├─ Pong Game (Port 8080+ - JavaFX)
└─ ConfigService (Port 8080 - WildFly)
```

### Production potentielle
```
Load Balancer (Port 80/443)
├─ API Gateway
├─ ConfigService Node 1 (Port 8080)
├─ ConfigService Node 2 (Port 8080)
├─ ConfigService Node N (Port 8080)
└─ Shared Database (PostgreSQL)
```

## Évolution future

### Phase 1 (Actuelle) ✅
- [x] REST API simple
- [x] H2 In-Memory
- [x] Fallback CSV
- [x] Déploiement local

### Phase 2 (À venir)
- [ ] Base de données persistante
- [ ] Authentification
- [ ] Versioning des configs
- [ ] Historique des modifications

### Phase 3 (Futur)
- [ ] Réplication
- [ ] Caching distribué
- [ ] Multi-tenancy
- [ ] WebSocket real-time sync

## Documentation complémentaire

- [README_CONFIGSERVICE.md](README_CONFIGSERVICE.md) - API Reference
- [QUICKSTART.md](QUICKSTART.md) - Installation rapide
- [Javadoc](ConfigService/src/main/java/com/echecpong/config/) - Code source
