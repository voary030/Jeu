# Échec Pong - JavaFX

Un jeu innovant mêlant échecs et Pong! Chaque pièce d'échecs a des points de vie. Détruisez le Roi adverse pour gagner!

## Concept du Jeu

🎯 **Objectif**: Toucher les pièces adverses avec la balle pour réduire leurs PV. Détruisez le Roi (10 PV) pour remporter la victoire!

## Fonctionnalités

✅ **Échiquier 8x8** avec zone de jeu bleue au centre (comme l'image)
✅ **Pièces d'échecs avec PV** - Chaque pièce a des points de vie spécifiques
✅ **Raquettes horizontales** dans la zone bleue (haut et bas)
✅ **Système de dégâts** - La balle réduit les PV des pièces qu'elle touche
✅ **Fin de partie** - Quand un Roi atteint 0 PV, l'adversaire gagne!
✅ **Interface intuitive** avec affichage des PV sur chaque pièce

## Points de Vie des Pièces

| Pièce | Symbole | PV Max |
|-------|---------|--------|
| **Roi** | ♚/♔ | 10 |
| **Reine** | ♛/♕ | 8 |
| **Tour** | ♜/♖ | 5 |
| **Cavalier** | ♞/♘ | 3 |
| **Fou** | ♝/♗ | 3 |
| **Pion** | ♟/♙ | 2 |

## Contrôles

### Joueur BLANC (Haut - Pièces blanches)
- **A** : Déplacer la raquette vers la gauche
- **D** : Déplacer la raquette vers la droite

### Joueur NOIR (Bas - Pièces noires)
- **← (Flèche Gauche)** : Déplacer la raquette vers la gauche
- **→ (Flèche Droite)** : Déplacer la raquette vers la droite

## Architecture du Projet

```
src/main/java/com/pong/
├── Main.java           # Classe principale et interface
├── GameController.java # Logique du jeu et collisions
├── ChessBoard.java     # Échiquier 8x8 avec pièces
├── ChessPiece.java     # Pièce d'échecs avec PV
├── Paddle.java         # Classe pour les raquettes
└── Puck.java          # Classe pour la balle

pom.xml                # Configuration Maven
```

## Classes Principales

### `ChessPiece`
Représente une pièce d'échecs avec points de vie.
- **Types** : Roi, Reine, Tour, Cavalier, Fou, Pion
- **PV** : Chaque type a des PV spécifiques
- **Dégâts** : Perd 1 PV quand touchée par la balle
- **Animation** : Effet visuel rouge lors des dégâts
- **Destruction** : Disparaît quand PV = 0

### `ChessBoard`
Gère l'échiquier et toutes les pièces.
- **Grille 8x8** : Cases beige/marron alternées
- **Zone bleue** : Centre (lignes 3-6) pour le jeu de Pong
- **Positionnement** : Blancs en haut (lignes 0-1), Noirs en bas (lignes 6-7)
- **Collision** : Détecte quelle pièce est touchée par la balle

### `Puck`
Représente la balle du jeu.
- **Mouvement** : Se déplace dans toutes les directions
- **Rebond** : Rebondit sur les murs, raquettes et pièces
- **Dégâts** : Inflige 1 PV de dégât aux pièces touchées

### `Paddle`
Représente une raquette.
- **Positionnement** : Dans la zone bleue (haut et bas)
- **Mouvement horizontal** : Gauche/droite
- **Collision** : Renvoie la balle

### `GameController`
Gère la logique du jeu.
- **Collisions raquettes** : Détecte et gère les rebonds
- **Collisions pièces** : Réduit les PV des pièces touchées
- **Fin de partie** : Vérifie si un Roi est détruit
- **Animation** : Boucle de jeu à 60 FPS

### `Main`
Interface de l'application JavaFX.
- **Affichage** : Échiquier, pièces, raquettes, balle
- **Événements** : Gère les entrées clavier
- **État du jeu** : Affiche le statut et le gagnant

## Installation et Exécution

### Prérequis
- Java 11 ou plus
- Maven 3.6 ou plus

### Étapes d'installation


### localhost:127.0.0.1

1. **Naviguer vers le dossier du projet**
```bash
cd "c:\Users\ranto\Documents\S5\ArchitectureLogiciel\EchecPong\Jeu"
```

2. **Compiler le projet**
```bash
mvn clean compile
```

3. **Exécuter le jeu**
```bash
mvn javafx:run
```

Ou, si vous préférez créer un JAR exécutable :
```bash
mvn clean package
java -jar target/pong-javafx-1.0.jar
```

## Design et Inspiration

Le projet s'inspire de l'image fournie :
- **Échiquier classique** : Cases beige (#f0d9b5) et marron (#b58863)
- **Zone de jeu bleue** : Centre bleu (#1e3a8a) pour le Pong
- **Pièces Unicode** : Symboles d'échecs avec couleurs blanches/noires
- **Raquettes marron** : Positionnées dans la zone bleue
- **Balle orange** : Visible sur fond bleu

## Mécanique du Jeu

1. **Début** : La balle commence au centre et se déplace aléatoirement
2. **Contrôle** : Les joueurs contrôlent les raquettes pour renvoyer la balle
3. **Collision pièces** : Quand la balle touche une pièce, celle-ci perd 1 PV
4. **Destruction** : Une pièce à 0 PV disparaît de l'échiquier
5. **Victoire** : Le jeu se termine quand un Roi est détruit
6. **Rebonds** : La balle rebondit sur les raquettes, murs et pièces

## Stratégie

- **Protégez votre Roi** : Le Roi a 10 PV, c'est votre pièce la plus importante
- **Ciblez le Roi adverse** : Concentrez vos tirs sur le Roi ennemi
- **Sacrifices tactiques** : Parfois, laisser une pièce faible se faire toucher peut sauver le Roi
- **Positionnement** : Placez votre raquette pour diriger la balle vers les pièces ennemies

## Améliorations Possibles

- Ajouter des power-ups (vie bonus, balle plus rapide, etc.)
- Son et effets sonores pour les impacts
- Animation d'explosion pour les pièces détruites
- Mode IA pour jouer seul
- Statistiques de partie (pièces détruées, coups joués, etc.)
- Différents modes de jeu (Time Attack, Survival, etc.)
- Sauvegarder et reprendre des parties

---

**Projet** : Échec Pong - Architecture Logicielle S5
**Technologie** : JavaFX
**Date** : Décembre 2025
