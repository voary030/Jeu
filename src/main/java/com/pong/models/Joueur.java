package com.pong.models;

/**
 * Classe représentant un joueur du jeu
 */
public class Joueur {
    
    public enum Cote {
        HAUT, BAS
    }
    
    private final String nom;
    private final Cote cote;
    private boolean estActif; // Le joueur qui contrôle actuellement
    private int angle; // Angle de tir (0-360)
    private final int angleMin = -60;
    private final int angleMax = 60;
    
    public Joueur(String nom, Cote cote) {
        this.nom = nom;
        this.cote = cote;
        this.estActif = false;
        this.angle = 0; // Angle droit (horizontal)
    }
    
    /**
     * Active ce joueur pour lancer la balle
     */
    public void activer() {
        this.estActif = true;
        this.angle = 0; // Réinitialise l'angle
    }
    
    /**
     * Désactive ce joueur
     */
    public void desactiver() {
        this.estActif = false;
    }
    
    /**
     * Tourne la flèche vers la gauche (augmente l'angle)
     */
    public void tournerGauche() {
        if (estActif) {
            angle = Math.min(angle + 5, angleMax);
        }
    }
    
    /**
     * Tourne la flèche vers la droite (diminue l'angle)
     */
    public void tournerDroite() {
        if (estActif) {
            angle = Math.max(angle - 5, angleMin);
        }
    }
    
    /**
     * Retourne la vélocité de lancement selon l'angle
     */
    public double[] getVelociteLancement() {
        double angleRad = Math.toRadians(angle);
        double vitesse = 6.0;
        
        // Pour le joueur du haut, la balle va vers le bas
        // Pour le joueur du bas, la balle va vers le haut
        double vx = Math.sin(angleRad) * vitesse;
        double vy = (cote == Cote.HAUT ? 1 : -1) * Math.cos(angleRad) * vitesse;
        
        return new double[]{vx, vy};
    }
    
    // Getters
    public String getNom() {
        return nom;
    }
    
    public Cote getCote() {
        return cote;
    }
    
    public boolean estActif() {
        return estActif;
    }
    
    public int getAngle() {
        return angle;
    }
    
    public void setAngle(int angle) {
        this.angle = Math.max(angleMin, Math.min(angleMax, angle));
    }
}
