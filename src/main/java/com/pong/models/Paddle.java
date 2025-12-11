package com.pong.models;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

/**
 * Classe représentant une raquette (paddle) du jeu de Pong
 */
public class Paddle extends Rectangle {
    private double speed = 6.0;
    private final double WIDTH;
    private final double HEIGHT;
    private final boolean isTopPaddle;
    
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean movingUp = false;
    private boolean movingDown = false;
    
    public Paddle(double x, double y, double width, double height, double gameWidth, double gameHeight, boolean isTop) {
        super(width, height);
        this.WIDTH = gameWidth;
        this.HEIGHT = gameHeight;
        this.isTopPaddle = isTop;
        
        setX(x);
        setY(y);
        
        // Design inspiré du HTML
        setFill(Color.web("#8b4513")); // Couleur marron
        setStroke(Color.web("#5d4037"));
        setStrokeWidth(2);
    }
    
    /**
     * Met à jour la position de la raquette
     */
    public void update() {
        // Mouvement horizontal (gauche/droite)
        if (movingLeft && getX() > 0) {
            setX(getX() - speed);
        }
        if (movingRight && getX() + getWidth() < WIDTH) {
            setX(getX() + speed);
        }
        
        // Surface de jeu = 4 carreaux (lignes 2-5 = 160px à 400px)
        // 2 carreaux pour le HAUT (160-320) et 2 carreaux pour le BAS (320-400)
        
        if (isTopPaddle) {
            // Raquette du HAUT: zone lignes 2-3 (160px à 320px)
            double minY = 160; // Ligne 2
            double maxY = 320 - getHeight(); // Ligne 4 (milieu) - hauteur raquette
            
            if (movingDown && getY() < maxY) {
                setY(getY() + speed * 0.7);
            }
            if (movingUp && getY() > minY) {
                setY(getY() - speed * 0.7);
            }
        } else {
            // Raquette du BAS: zone lignes 4-5 (320px à 480px)
            double minY = 320; // Ligne 4 (milieu)
            double maxY = 480 - getHeight(); // Ligne 6 - hauteur raquette
            
            if (movingUp && getY() > minY) {
                setY(getY() - speed * 0.7);
            }
            if (movingDown && getY() < maxY) {
                setY(getY() + speed * 0.7);
            }
        }
    }
    
    /**
     * Démarre le mouvement vers la gauche
     */
    public void startMovingLeft() {
        movingLeft = true;
    }
    
    /**
     * Arrête le mouvement vers la gauche
     */
    public void stopMovingLeft() {
        movingLeft = false;
    }
    
    /**
     * Démarre le mouvement vers la droite
     */
    public void startMovingRight() {
        movingRight = true;
    }
    
    /**
     * Arrête le mouvement vers la droite
     */
    public void stopMovingRight() {
        movingRight = false;
    }
    
    /**
     * Démarre le mouvement vers le haut (avancer)
     */
    public void startMovingUp() {
        movingUp = true;
    }
    
    /**
     * Arrête le mouvement vers le haut
     */
    public void stopMovingUp() {
        movingUp = false;
    }
    
    /**
     * Démarre le mouvement vers le bas (reculer)
     */
    public void startMovingDown() {
        movingDown = true;
    }
    
    /**
     * Arrête le mouvement vers le bas
     */
    public void stopMovingDown() {
        movingDown = false;
    }
    
    /**
     * Détecte la collision avec la balle
     */
    public boolean collidesWith(Puck puck) {
        return this.getBoundsInParent().intersects(puck.getBoundsInParent());
    }
    
    /**
     * Détecte quel côté de la raquette a été touché
     * Retourne: 'top', 'bottom', 'left', 'right', ou 'none'
     */
    public String getCollisionSide(Puck puck) {
        double puckX = puck.getCenterX();
        double puckY = puck.getCenterY();
        
        double paddleLeft = getX();
        double paddleRight = getX() + getWidth();
        double paddleTop = getY();
        double paddleBottom = getY() + getHeight();
        
        // Calcule les distances du centre du puck aux bords de la raquette
        double distToLeft = Math.abs(puckX - paddleLeft);
        double distToRight = Math.abs(puckX - paddleRight);
        double distToTop = Math.abs(puckY - paddleTop);
        double distToBottom = Math.abs(puckY - paddleBottom);
        
        // Trouve le côté le plus proche
        double minDist = Math.min(Math.min(distToLeft, distToRight), Math.min(distToTop, distToBottom));
        
        if (minDist == distToTop && puckY > paddleTop) {
            return "top";
        } else if (minDist == distToBottom && puckY < paddleBottom) {
            return "bottom";
        } else if (minDist == distToLeft && puckX > paddleLeft) {
            return "left";
        } else if (minDist == distToRight && puckX < paddleRight) {
            return "right";
        }
        return "none";
    }
    
    /**
     * Retourne la position relative de la balle sur la raquette (-1 à 1)
     */
    public double getRelativePosition(Puck puck) {
        double puckCenterX = puck.getCenterX();
        double paddleLeft = getX();
        double paddleRight = getX() + getWidth();
        double paddleCenter = (paddleLeft + paddleRight) / 2;
        
        // Position relative de -1 (gauche) à 1 (droite)
        return (puckCenterX - paddleCenter) / (getWidth() / 2);
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
