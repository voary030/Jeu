package com.pong.config;

/**
 * Configuration dynamique du nombre de pions et de la disposition du terrain
 */
public class BoardConfig {
    private static int numberOfPawns = 8; // Par défaut: configuration complète
    
    /**
     * Définit le nombre de pions (2, 4, 6, 8...)
     * Ce nombre détermine également le nombre de colonnes
     */
    public static void setNumberOfPawns(int pawns) {
        if (pawns % 2 != 0 || pawns < 2) {
            throw new IllegalArgumentException("Le nombre de pions doit être pair et >= 2");
        }
        numberOfPawns = pawns;
    }
    
    public static int getNumberOfPawns() {
        return numberOfPawns;
    }
    
    /**
     * Retourne le nombre de colonnes = nombre de pions
     */
    public static int getNumberOfColumns() {
        return numberOfPawns;
    }
    
    /**
     * Retourne la largeur d'une case en pixels
     */
    public static int getCellSize() {
        return 80; // Taille fixe pour chaque case
    }
    
    /**
     * Calcule la largeur totale du plateau
     */
    public static int getBoardWidth() {
        return getNumberOfColumns() * getCellSize();
    }
    
    /**
     * Le nombre de lignes reste toujours 8 (2 pour chaque camp + 4 pour la zone de jeu)
     */
    public static int getNumberOfRows() {
        return 8;
    }
    
    /**
     * Calcule la hauteur totale du plateau
     */
    public static int getBoardHeight() {
        return getNumberOfRows() * getCellSize();
    }
    
    /**
     * Détermine quelles pièces doivent être présentes selon le nombre de pions
     */
    public static boolean hasRooks() {
        return numberOfPawns >= 8;
    }
    
    public static boolean hasKnights() {
        return numberOfPawns >= 6;
    }
    
    public static boolean hasBishops() {
        return numberOfPawns >= 4;
    }
    
    /**
     * Roi et Reine sont toujours présents
     */
    public static boolean hasKingAndQueen() {
        return true;
    }
}
