package com.pong.controllers;

import com.pong.models.Paddle;
import com.pong.models.Puck;

/**
 * Intelligence artificielle pour contrôler la raquette du joueur ordinateur
 */
public class ComputerAI {
    private Paddle paddle;
    private Puck puck;
    private double boardWidth;
    private double difficulty; // 0.0 à 1.0 (plus élevé = plus difficile)
    
    // Paramètres de l'IA
    private static final double REACTION_SPEED = 0.6; // Vitesse de réaction
    private static final double PREDICTION_FACTOR = 0.3; // Anticipation de la balle
    private static final double ERROR_MARGIN = 15.0; // Marge d'erreur pour rendre l'IA battable
    
    public ComputerAI(Paddle paddle, Puck puck, double boardWidth, double difficulty) {
        this.paddle = paddle;
        this.puck = puck;
        this.boardWidth = boardWidth;
        this.difficulty = Math.max(0.0, Math.min(1.0, difficulty)); // Clamp entre 0 et 1
    }
    
    /**
     * Met à jour la position de la raquette de l'IA
     */
    public void update() {
        // Calculer la position cible en fonction de la balle
        double targetX = calculateTargetPosition();
        
        // Déplacer la raquette vers la cible avec une certaine vitesse
        double paddleCenter = paddle.getX() + paddle.getWidth() / 2;
        double distance = targetX - paddleCenter;
        
        // Appliquer la vitesse de réaction ajustée par la difficulté
        double moveSpeed = REACTION_SPEED * 5 * (0.5 + difficulty * 0.5);
        
        if (Math.abs(distance) > ERROR_MARGIN * (1.5 - difficulty)) {
            if (distance > 0) {
                moveRight(moveSpeed);
            } else {
                moveLeft(moveSpeed);
            }
        }
    }
    
    /**
     * Calcule la position cible où la raquette devrait aller
     */
    private double calculateTargetPosition() {
        double ballX = puck.getCenterX();
        double ballY = puck.getCenterY();
        double ballVX = puck.getVelocityX();
        double ballVY = puck.getVelocityY();
        
        // Prédiction simple : ajouter un facteur prédictif basé sur la vélocité
        double predictedX = ballX + (ballVX * PREDICTION_FACTOR * difficulty * 10);
        
        // Ajouter une petite erreur aléatoire pour rendre l'IA moins parfaite
        double error = (Math.random() - 0.5) * ERROR_MARGIN * (1.5 - difficulty);
        
        // S'assurer que la position reste dans les limites du plateau
        predictedX = Math.max(paddle.getWidth() / 2, 
                             Math.min(boardWidth - paddle.getWidth() / 2, predictedX + error));
        
        return predictedX;
    }
    
    /**
     * Déplace la raquette vers la gauche
     */
    private void moveLeft(double speed) {
        double newX = paddle.getX() - speed;
        if (newX >= 0) {
            paddle.setX(newX);
        } else {
            paddle.setX(0);
        }
    }
    
    /**
     * Déplace la raquette vers la droite
     */
    private void moveRight(double speed) {
        double newX = paddle.getX() + speed;
        if (newX + paddle.getWidth() <= boardWidth) {
            paddle.setX(newX);
        } else {
            paddle.setX(boardWidth - paddle.getWidth());
        }
    }
    
    /**
     * Définit le niveau de difficulté de l'IA
     */
    public void setDifficulty(double difficulty) {
        this.difficulty = Math.max(0.0, Math.min(1.0, difficulty));
    }
    
    /**
     * Obtient le niveau de difficulté actuel
     */
    public double getDifficulty() {
        return difficulty;
    }
}
