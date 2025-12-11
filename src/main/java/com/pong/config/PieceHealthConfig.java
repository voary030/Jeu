package com.pong.config;

/**
 * Configuration des points de vie des pièces d'échecs
 */
public class PieceHealthConfig {
    private static int kingHealth = 10;
    private static int queenHealth = 8;
    private static int rookHealth = 5;
    private static int bishopHealth = 3;
    private static int knightHealth = 3;
    private static int pawnHealth = 2;
    
    public static int getKingHealth() {
        return kingHealth;
    }
    
    public static void setKingHealth(int health) {
        kingHealth = health;
    }
    
    public static int getQueenHealth() {
        return queenHealth;
    }
    
    public static void setQueenHealth(int health) {
        queenHealth = health;
    }
    
    public static int getRookHealth() {
        return rookHealth;
    }
    
    public static void setRookHealth(int health) {
        rookHealth = health;
    }
    
    public static int getBishopHealth() {
        return bishopHealth;
    }
    
    public static void setBishopHealth(int health) {
        bishopHealth = health;
    }
    
    public static int getKnightHealth() {
        return knightHealth;
    }
    
    public static void setKnightHealth(int health) {
        knightHealth = health;
    }
    
    public static int getPawnHealth() {
        return pawnHealth;
    }
    
    public static void setPawnHealth(int health) {
        pawnHealth = health;
    }
}
