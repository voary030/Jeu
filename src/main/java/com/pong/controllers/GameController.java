package com.pong.controllers;

import com.pong.config.GameConfig;
import com.pong.models.*;
import com.pong.ui.Arrow;
import com.pong.ui.ChessBoard;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Classe contrôlant la logique du jeu avec échiquier et système de joueurs
 */
public class GameController {
    private Puck puck;
    private Paddle topPaddle;
    private Paddle bottomPaddle;
    private ChessBoard chessBoard;
    
    // Système de joueurs
    private Joueur joueurBlanc;
    private Joueur joueurNoir;
    private Joueur joueurActuel;
    
    // Flèche directionnelle
    private Arrow arrow;
    private boolean ballLaunched = false;
    private boolean arrowAnimating = false;
    private double arrowAngleDirection = 1; // 1 pour augmenter, -1 pour diminuer
    
    private final double WIDTH;
    private final double HEIGHT;
    private AnimationTimer gameLoop;
    private boolean gameOver = false;
    private boolean isPaused = false;
    private String winner = "";
    private GameScore gameScore;
    private Runnable onVictoryCallback;
    
    // Mode de jeu et IA
    private boolean vsComputerMode = false;
    private ComputerAI computerAI;
    
    public GameController(ChessBoard chessBoard) {
        this(chessBoard, false);
    }
    
    public GameController(ChessBoard chessBoard, boolean vsComputer) {
        this.vsComputerMode = vsComputer;
        this.gameScore = new GameScore(GameConfig.getPlayer1Name(), GameConfig.getPlayer2Name());
        this.chessBoard = chessBoard;
        this.WIDTH = chessBoard.getBoardWidth();
        this.HEIGHT = chessBoard.getBoardHeight();
        
        // Crée la balle au centre
        puck = new Puck(WIDTH, HEIGHT);
        
        // Raquette du haut (dans la zone grise, ligne 2-3)
        topPaddle = new Paddle(
            WIDTH / 2 - 35,  // x: centré horizontalement
            190,             // y: ligne 2-3 (zone grise haute)
            70,              // largeur
            12,              // hauteur
            WIDTH, HEIGHT,
            true             // raquette du HAUT
        );
        
        // Raquette du bas (dans la zone grise, ligne 4-5)
        bottomPaddle = new Paddle(
            WIDTH / 2 - 35,  // x: centré horizontalement
            380,             // y: ligne 4-5 (zone grise basse)
            70,              // largeur
            12,              // hauteur
            WIDTH, HEIGHT,
            false            // raquette du BAS
        );
        
        // Crée les joueurs avec les noms de la configuration
        String player2Name = vsComputer ? "Computer" : GameConfig.getPlayer2Name();
        joueurBlanc = new Joueur(GameConfig.getPlayer1Name(), Joueur.Cote.HAUT);
        joueurNoir = new Joueur(player2Name, Joueur.Cote.BAS);
        joueurActuel = null;
        
        // Crée la flèche de visée (sans paramètres de position)
        arrow = new Arrow();
        
        // Initialise l'IA si en mode VS Computer
        if (vsComputerMode) {
            computerAI = new ComputerAI(topPaddle, puck, WIDTH, 0.7); // Difficulté 0.7
        }
    }
    
    /**
     * Démarre la boucle de jeu
     */
    public void start() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();
    }
    
    /**
     * Arrête la boucle de jeu
     */
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
    
    /**
     * Met en pause le jeu
     */
    public void pause() {
        isPaused = true;
    }
    
    /**
     * Reprend le jeu
     */
    public void resume() {
        isPaused = false;
    }
    
    /**
     * Met à jour l'état du jeu à chaque frame
     */
    private void update() {
        if (gameOver || isPaused) {
            return;
        }
        
        // Mise à jour des positions des raquettes
        topPaddle.update();
        bottomPaddle.update();
        
        // Mise à jour de l'IA si en mode VS Computer
        if (vsComputerMode && computerAI != null && ballLaunched) {
            computerAI.update();
        }
        
        // Mise à jour de la balle si lancée
        if (ballLaunched) {
            puck.update();
            
            // Détection de collision avec la raquette du HAUT (toute la surface)
            if (topPaddle.collidesWith(puck)) {
                String side = topPaddle.getCollisionSide(puck);
                
                if ("bottom".equals(side)) {
                    // Collision sur le dessous de la raquette du haut - la balle va vers le bas
                    double relativePos = topPaddle.getRelativePosition(puck);
                    puck.bounceWithSpin(relativePos, true); // true = raquette du haut
                    puck.setCenterY(topPaddle.getY() + topPaddle.getHeight() + puck.getRadius() + 1);
                } else if ("top".equals(side)) {
                    // Collision sur le dessus de la raquette du haut - la balle va vers le haut
                    double relativePos = topPaddle.getRelativePosition(puck);
                    // Quand la balle frappe le DESSUS de la raquette du haut, elle doit aller vers le haut
                    puck.bounceWithSpin(relativePos, false); // false pour aller vers le haut
                    puck.setCenterY(topPaddle.getY() - puck.getRadius() - 1);
                } else if ("left".equals(side)) {
                    // Collision sur le côté gauche
                    puck.setCenterX(topPaddle.getX() - puck.getRadius() - 1);
                    puck.setVelocity(-Math.abs(puck.getVelocityX()), puck.getVelocityY());
                } else if ("right".equals(side)) {
                    // Collision sur le côté droit
                    puck.setCenterX(topPaddle.getX() + topPaddle.getWidth() + puck.getRadius() + 1);
                    puck.setVelocity(Math.abs(puck.getVelocityX()), puck.getVelocityY());
                }
            }
            
            // Détection de collision avec la raquette du BAS (toute la surface)
            if (bottomPaddle.collidesWith(puck)) {
                String side = bottomPaddle.getCollisionSide(puck);
                
                if ("top".equals(side)) {
                    // Collision sur le dessus de la raquette du bas - la balle va vers le haut
                    double relativePos = bottomPaddle.getRelativePosition(puck);
                    puck.bounceWithSpin(relativePos, false); // false = raquette du bas
                    puck.setCenterY(bottomPaddle.getY() - puck.getRadius() - 1);
                } else if ("bottom".equals(side)) {
                    // Collision sur le dessous de la raquette du bas - la balle va vers le bas
                    double relativePos = bottomPaddle.getRelativePosition(puck);
                    // Quand la balle frappe le DESSOUS de la raquette du bas, elle doit aller vers le bas
                    puck.bounceWithSpin(relativePos, true); // true pour aller vers le bas
                    puck.setCenterY(bottomPaddle.getY() + bottomPaddle.getHeight() + puck.getRadius() + 1);
                } else if ("left".equals(side)) {
                    // Collision sur le côté gauche
                    puck.setCenterX(bottomPaddle.getX() - puck.getRadius() - 1);
                    puck.setVelocity(-Math.abs(puck.getVelocityX()), puck.getVelocityY());
                } else if ("right".equals(side)) {
                    // Collision sur le côté droit
                    puck.setCenterX(bottomPaddle.getX() + bottomPaddle.getWidth() + puck.getRadius() + 1);
                    puck.setVelocity(Math.abs(puck.getVelocityX()), puck.getVelocityY());
                }
            }
            
            // Détection de collision avec les pièces d'échecs
            checkPieceCollision();
        }
        
        // Animation automatique de la flèche si un joueur est actif
        if (joueurActuel != null && joueurActuel.estActif() && arrowAnimating) {
            int currentAngle = joueurActuel.getAngle();
            currentAngle += (int)(arrowAngleDirection * 2); // Vitesse d'animation
            
            // Inversion de direction aux limites
            if (currentAngle >= 60) {
                currentAngle = 60;
                arrowAngleDirection = -1;
            } else if (currentAngle <= -60) {
                currentAngle = -60;
                arrowAngleDirection = 1;
            }
            
            joueurActuel.setAngle(currentAngle);
            arrow.setAngle(currentAngle);
        }
        
        // Vérifier si un roi est mort
        checkGameOver();
    }
    
    /**
     * Vérifie les collisions entre la balle et les pièces
     */
    private void checkPieceCollision() {
        double puckX = puck.getCenterX();
        double puckY = puck.getCenterY();
        
        ChessPiece hitPiece = chessBoard.getPieceAt(puckX, puckY);
        
        if (hitPiece != null && hitPiece.isAlive()) {
            // La pièce perd 1 PV
            boolean stillAlive = hitPiece.takeDamage();
            
            // La balle rebondit dans la direction opposée verticale
            puck.bounce();
            
            // Ajoute un petit effet de rebond horizontal aléatoire
            double currentVx = puck.getVelocityX();
            double randomSpin = (Math.random() - 0.5) * 2; // Entre -1 et 1
            puck.setVelocity(currentVx + randomSpin, puck.getVelocityY());
            
            if (!stillAlive) {
                System.out.println(hitPiece.getType().getName() + " détruit!");
            }
        }
    }
    
    /**
     * Vérifie si un roi est mort pour terminer la partie
     */
    private void checkGameOver() {
        ChessPiece whiteKing = chessBoard.getKing(ChessPiece.PieceColor.WHITE);
        ChessPiece blackKing = chessBoard.getKing(ChessPiece.PieceColor.BLACK);
        
        if (whiteKing == null || !whiteKing.isAlive()) {
            gameOver = true;
            winner = gameScore.getPlayer2Name();
            gameScore.addWinForPlayer2();
            stop();
            if (onVictoryCallback != null) {
                onVictoryCallback.run();
            }
        } else if (blackKing == null || !blackKing.isAlive()) {
            gameOver = true;
            winner = gameScore.getPlayer1Name();
            gameScore.addWinForPlayer1();
            stop();
            if (onVictoryCallback != null) {
                onVictoryCallback.run();
            }
        }
    }
    
    /**
     * Réinitialise le jeu pour une nouvelle partie
     */
    public void resetGame() {
        gameOver = false;
        ballLaunched = false;
        joueurActuel = null;
        arrow.setVisible(false);
        
        // Réinitialise la balle
        puck.reset();
        
        // Réinitialise l'échiquier
        chessBoard.resetBoard();
        
        // Redémarre la boucle
        start();
    }
    
    /**
     * Gère les événements clavier
     */
    public void handleKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();
        
        // En mode VS Computer, désactiver les contrôles de la raquette du haut
        if (vsComputerMode) {
            // Seulement les contrôles du joueur du bas (humain)
            switch (code) {
                case LEFT:
                    bottomPaddle.startMovingLeft();
                    break;
                case RIGHT:
                    bottomPaddle.startMovingRight();
                    break;
                case UP:
                    bottomPaddle.startMovingUp();
                    break;
                case DOWN:
                    bottomPaddle.startMovingDown();
                    break;
                
                // Contrôles d'angle de tir
                case Q:
                    if (joueurActuel != null && joueurActuel.estActif()) {
                        joueurActuel.tournerGauche();
                    }
                    break;
                case E:
                    if (joueurActuel != null && joueurActuel.estActif()) {
                        joueurActuel.tournerDroite();
                    }
                    break;
                
                // ENTER = Lance la balle
                case ENTER:
                    if (joueurActuel != null && joueurActuel.estActif()) {
                        lancerBalle();
                    }
                    break;
                
                default:
                    break;
            }
            return;
        }
        
        // Mode multijoueur normal
        // Activation des joueurs au clic sur leur raquette
        // (Cela sera géré par la souris dans Main, mais on laisse les contrôles de déplacement)
        
        switch (code) {
            // Joueur BLANC (haut): Pavé numérique 8246
            case NUMPAD4:
                topPaddle.startMovingLeft();
                break;
            case NUMPAD6:
                topPaddle.startMovingRight();
                break;
            case NUMPAD8:
                topPaddle.startMovingUp();
                break;
            case NUMPAD2:
                topPaddle.startMovingDown();
                break;
            
            // Joueur NOIR (bas): Flèches
            case LEFT:
                bottomPaddle.startMovingLeft();
                break;
            case RIGHT:
                bottomPaddle.startMovingRight();
                break;
            case UP:
                bottomPaddle.startMovingUp();
                break;
            case DOWN:
                bottomPaddle.startMovingDown();
                break;
            
            // Contrôles d'angle de tir
            case Q: // Tourne la flèche à gauche
                if (joueurActuel != null && joueurActuel.estActif()) {
                    joueurActuel.tournerGauche();
                }
                break;
            case E: // Tourne la flèche à droite
                if (joueurActuel != null && joueurActuel.estActif()) {
                    joueurActuel.tournerDroite();
                }
                break;
            
            // ENTER = Lance la balle
            case ENTER:
                if (joueurActuel != null && joueurActuel.estActif()) {
                    lancerBalle();
                }
                break;
            
            default:
                break;
        }
    }
    
    /**
     * Lance la balle selon l'angle du joueur actuel
     */
    private void lancerBalle() {
        if (joueurActuel != null) {
            double[] velocite = joueurActuel.getVelociteLancement();
            puck.setVelocity(velocite[0], velocite[1]);
            ballLaunched = true;
            arrowAnimating = false; // Arrêter l'animation
            joueurActuel.desactiver();
            joueurActuel = null;
            arrow.setVisible(false);
        }
    }
    
    /**
     * Active le joueur pour la prochaine action
     * Positionne la balle devant la raquette et démarre l'animation de la flèche
     */
    public void activerJoueur(Joueur joueur) {
        if (joueurActuel != null) {
            joueurActuel.desactiver();
        }
        joueurActuel = joueur;
        joueurActuel.activer();
        
        // Arrêter la balle et la positionner devant la raquette
        ballLaunched = false;
        puck.setVelocity(0, 0);
        
        if (joueur.getCote() == Joueur.Cote.HAUT) {
            // Positionner devant la raquette du haut
            puck.setCenterX(topPaddle.getX() + topPaddle.getWidth() / 2);
            puck.setCenterY(topPaddle.getY() + topPaddle.getHeight() + puck.getRadius() + 5);
            arrow.setLayoutX(topPaddle.getX() + topPaddle.getWidth() / 2);
            arrow.setLayoutY(topPaddle.getY() + topPaddle.getHeight() + 20);
        } else {
            // Positionner devant la raquette du bas
            puck.setCenterX(bottomPaddle.getX() + bottomPaddle.getWidth() / 2);
            puck.setCenterY(bottomPaddle.getY() - puck.getRadius() - 5);
            arrow.setLayoutX(bottomPaddle.getX() + bottomPaddle.getWidth() / 2);
            arrow.setLayoutY(bottomPaddle.getY() - 20);
        }
        
        // Démarrer l'animation de la flèche
        arrowAnimating = true;
        arrowAngleDirection = 1;
        joueurActuel.setAngle(0); // Commencer à l'angle 0
        arrow.setAngle(0);
        arrow.setVisible(true);
    }
    
    /**
     * Gère la libération des touches
     */
    public void handleKeyReleased(KeyEvent event) {
        KeyCode code = event.getCode();
        
        switch (code) {
            // Joueur BLANC (haut): Pavé numérique 8246
            case NUMPAD4:
                topPaddle.stopMovingLeft();
                break;
            case NUMPAD6:
                topPaddle.stopMovingRight();
                break;
            case NUMPAD8:
                topPaddle.stopMovingUp();
                break;
            case NUMPAD2:
                topPaddle.stopMovingDown();
                break;
            
            // Joueur NOIR (bas): Flèches
            case LEFT:
                bottomPaddle.stopMovingLeft();
                break;
            case RIGHT:
                bottomPaddle.stopMovingRight();
                break;
            case UP:
                bottomPaddle.stopMovingUp();
                break;
            case DOWN:
                bottomPaddle.stopMovingDown();
                break;
            
            default:
                break;
        }
    }
    
    // Getters
    public Puck getPuck() {
        return puck;
    }
    
    public Paddle getTopPaddle() {
        return topPaddle;
    }
    
    public Paddle getBottomPaddle() {
        return bottomPaddle;
    }
    
    public ChessBoard getChessBoard() {
        return chessBoard;
    }
    
    public Arrow getArrow() {
        return arrow;
    }
    
    public Joueur getJoueurBlanc() {
        return joueurBlanc;
    }
    
    public Joueur getJoueurNoir() {
        return joueurNoir;
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public String getWinner() {
        return winner;
    }
    
    public GameScore getGameScore() {
        return gameScore;
    }
    
    public void setOnVictoryCallback(Runnable callback) {
        this.onVictoryCallback = callback;
    }
}
