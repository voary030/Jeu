package com.pong.config;

import java.util.Map;

/**
 * Exemple d'utilisation de ConfigServiceClient
 * Montre comment utiliser la microservice de configuration
 */
public class ConfigServiceExample {
    
    public static void main(String[] args) {
        System.out.println("=== Exemple d'utilisation ConfigService ===\n");
        
        // Vérifier la santé du service
        checkServiceHealth();
        
        // Récupérer la configuration du plateau
        getBoardConfigExample();
        
        // Récupérer les configurations des pièces
        getPieceConfigExample();
        
        // Sauvegarder une configuration
        saveBoardConfigExample();
    }
    
    /**
     * Vérifie la disponibilité du service
     */
    private static void checkServiceHealth() {
        System.out.println("1️⃣ Vérification de la santé du service...");
        if (ConfigServiceClient.isServiceAvailable()) {
            System.out.println("✅ Service disponible\n");
        } else {
            System.out.println("❌ Service indisponible\n");
            System.out.println("⚠️ Les configurations seront chargées depuis les fichiers CSV\n");
        }
    }
    
    /**
     * Récupère la configuration du plateau
     */
    private static void getBoardConfigExample() {
        System.out.println("2️⃣ Récupération de la configuration du plateau...");
        try {
            Map<String, String> boardConfig = ConfigServiceClient.getBoardConfig();
            System.out.println("Configuration du plateau:");
            boardConfig.forEach((key, value) -> System.out.println("  - " + key + ": " + value));
            System.out.println();
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Récupère les configurations des pièces
     */
    private static void getPieceConfigExample() {
        System.out.println("3️⃣ Récupération des configurations des pièces...");
        try {
            Map<String, String> piecesConfig = ConfigServiceClient.getAllPieceHealthConfig();
            System.out.println("Points de vie des pièces:");
            piecesConfig.forEach((piece, hp) -> System.out.println("  - " + piece + ": " + hp + " PV"));
            System.out.println();
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Récupère une pièce spécifique
     */
    private static void getPieceName(String pieceName) {
        System.out.println("Récupération de la santé du " + pieceName + "...");
        try {
            int health = ConfigServiceClient.getPieceHealthConfig(pieceName);
            System.out.println("  - " + pieceName + ": " + health + " PV\n");
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Sauvegarde une configuration
     */
    private static void saveBoardConfigExample() {
        System.out.println("4️⃣ Sauvegarde d'une configuration...");
        try {
            System.out.println("Changement du nombre de pions: 8 → 10");
            ConfigServiceClient.saveBoardConfig(10);
            System.out.println("✅ Configuration sauvegardée\n");
            
            // Vérifier la modification
            Map<String, String> updatedConfig = ConfigServiceClient.getBoardConfig();
            System.out.println("Vérification: numberOfPawns = " + updatedConfig.get("numberOfPawns"));
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Sauvegarde la configuration d'une pièce
     */
    private static void savePieceConfigExample(String pieceName, int newHealth) {
        System.out.println("Sauvegarde de la santé du " + pieceName + ": " + newHealth + " PV");
        try {
            ConfigServiceClient.savePieceHealthConfig(pieceName, newHealth);
            System.out.println("✅ Configuration sauvegardée\n");
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage() + "\n");
        }
    }
}
