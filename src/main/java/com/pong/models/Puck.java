package com.pong.models;

import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

/**
 * Classe représentant la balle (puck) du jeu de Pong
 */
public class Puck extends Circle {
    private double velocityX;
    private double velocityY;
    private final double speed = 5.0; // Augmenté de 4.0 à 5.0
    
    private final double WIDTH;
    private final double HEIGHT;
    
    public Puck(double width, double height) {
        super(10); // Rayon du cercle réduit
        this.WIDTH = width;
        this.HEIGHT = height;
        
        setFill(Color.web("#f39c12")); // Couleur orange comme dans le HTML
        setStroke(Color.web("#e67e22"));
        setStrokeWidth(3);
        
        // Position initiale au centre absolu
        setCenterX(width / 4);
        setCenterY(height / 4);
        
        // Vitesse initiale à 0 - la balle sera lancée par le joueur
        velocityX = 0;
        velocityY = 0;
    }
    
    /**
     * Définit la vélocité de la balle
     */
    public void setVelocity(double vx, double vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }
    
    /**
     * Réinitialise la balle au centre et arrête le mouvement
     */
    public void reset() {
        setCenterX(WIDTH / 2);
        setCenterY(HEIGHT / 2);
        velocityX = 0;
        velocityY = 0;
    }
    public void update() {
        // Ne bouge pas si la vitesse est nulle
        if (Math.abs(velocityX) < 0.01 && Math.abs(velocityY) < 0.01) {
            return;
        }
        
        // Déplacement
        setCenterX(getCenterX() + velocityX);
        setCenterY(getCenterY() + velocityY);
        
        // Rebond sur les murs haut et bas
        if (getCenterY() - getRadius() <= 0 || getCenterY() + getRadius() >= HEIGHT) {
            velocityY = -velocityY;
            // Garde la balle dans les limites
            if (getCenterY() - getRadius() < 0) {
                setCenterY(getRadius() + 2);
            } else {
                setCenterY(HEIGHT - getRadius() - 2);
            }
        }
        
        // Rebond sur les murs gauche et droite
        if (getCenterX() - getRadius() <= 0 || getCenterX() + getRadius() >= WIDTH) {
            velocityX = -velocityX;
            if (getCenterX() - getRadius() < 0) {
                setCenterX(getRadius() + 2);
            } else {
                setCenterX(WIDTH - getRadius() - 2);
            }
        }
    }
    
    /**
     * Fait rebondir la balle
     */
    public void bounce() {
        velocityY = -velocityY;
    }
    
    /**
     * Fait rebondir la balle avec variation (plus réaliste)
     * @param paddleRelativePosition Position relative sur la raquette: -1 (gauche) à 1 (droite)
     * @param isTopPaddle true si c'est la raquette du haut, false si c'est celle du bas
     */
    public void bounceWithSpin(double paddleRelativePosition, boolean isTopPaddle) {
        // Physique naturelle: on inverse simplement la direction Y (comme une vraie balle)
        velocityY = -velocityY;
        
        // Ajoute un effet de spin léger selon où la balle frappe la raquette
        // Si la balle frappe à gauche (-1), elle part vers la gauche
        // Si elle frappe à droite (+1), elle part vers la droite
        double spinEffect = paddleRelativePosition * speed * 0.3;
        velocityX += spinEffect;
        
        // Limite la vitesse horizontale pour garder un jeu jouable
        double maxVx = speed * 1.2;
        if (Math.abs(velocityX) > maxVx) {
            velocityX = Math.signum(velocityX) * maxVx;
        }
        
        // S'assure que la balle ne va pas trop lentement verticalement
        // (évite les trajectoires presque horizontales)
        double minVy = speed * 0.4;
        if (Math.abs(velocityY) < minVy) {
            velocityY = Math.signum(velocityY) * minVy;
        }
    }
    
    public double getVelocityX() {
        return velocityX;
    }
    
    public double getVelocityY() {
        return velocityY;
    }
}
