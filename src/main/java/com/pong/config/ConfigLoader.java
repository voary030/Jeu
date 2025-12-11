package com.pong.config;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe pour charger les configurations depuis des fichiers CSV
 */
public class ConfigLoader {
    
    private static final String BOARD_CONFIG_FILE = "/config/board_config.csv";
    private static final String PIECE_HEALTH_CONFIG_FILE = "/config/piece_health_config.csv";
    
    /**
     * Charge toutes les configurations au démarrage de l'application
     */
    public static void loadAllConfigurations() {
        System.out.println("📂 Chargement des configurations...");
        loadBoardConfig();
        loadPieceHealthConfig();
        System.out.println("✅ Configurations chargées avec succès!");
    }
    
    /**
     * Charge la configuration du terrain depuis le fichier CSV
     */
    public static void loadBoardConfig() {
        try {
            Map<String, String> config = loadCSV(BOARD_CONFIG_FILE);
            
            if (config.containsKey("numberOfPawns")) {
                int numberOfPawns = Integer.parseInt(config.get("numberOfPawns"));
                BoardConfig.setNumberOfPawns(numberOfPawns);
                System.out.println("  ✓ Terrain: " + numberOfPawns + " pions");
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ Erreur chargement config terrain: " + e.getMessage());
            System.err.println("   Utilisation des valeurs par défaut");
        }
    }
    
    /**
     * Charge la configuration des HP des pièces depuis le fichier CSV
     */
    public static void loadPieceHealthConfig() {
        try {
            Map<String, String> config = loadCSV(PIECE_HEALTH_CONFIG_FILE);
            
            if (config.containsKey("KING")) {
                PieceHealthConfig.setKingHealth(Integer.parseInt(config.get("KING")));
            }
            if (config.containsKey("QUEEN")) {
                PieceHealthConfig.setQueenHealth(Integer.parseInt(config.get("QUEEN")));
            }
            if (config.containsKey("ROOK")) {
                PieceHealthConfig.setRookHealth(Integer.parseInt(config.get("ROOK")));
            }
            if (config.containsKey("BISHOP")) {
                PieceHealthConfig.setBishopHealth(Integer.parseInt(config.get("BISHOP")));
            }
            if (config.containsKey("KNIGHT")) {
                PieceHealthConfig.setKnightHealth(Integer.parseInt(config.get("KNIGHT")));
            }
            if (config.containsKey("PAWN")) {
                PieceHealthConfig.setPawnHealth(Integer.parseInt(config.get("PAWN")));
            }
            
            System.out.println("  ✓ HP: Roi=" + PieceHealthConfig.getKingHealth() + 
                             ", Reine=" + PieceHealthConfig.getQueenHealth() +
                             ", Tour=" + PieceHealthConfig.getRookHealth() +
                             ", Fou=" + PieceHealthConfig.getBishopHealth() +
                             ", Cavalier=" + PieceHealthConfig.getKnightHealth() +
                             ", Pion=" + PieceHealthConfig.getPawnHealth());
            
        } catch (Exception e) {
            System.err.println("⚠️ Erreur chargement config HP: " + e.getMessage());
            System.err.println("   Utilisation des valeurs par défaut");
        }
    }
    
    /**
     * Sauvegarde la configuration du terrain dans le fichier CSV
     */
    public static void saveBoardConfig() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("numberOfPawns", String.valueOf(BoardConfig.getNumberOfPawns()));
            
            saveCSV(BOARD_CONFIG_FILE, config, 
                   "# Configuration du terrain de jeu\n# Format: parametre,valeur");
            System.out.println("💾 Configuration terrain sauvegardée");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur sauvegarde config terrain: " + e.getMessage());
        }
    }
    
    /**
     * Sauvegarde la configuration des HP dans le fichier CSV
     */
    public static void savePieceHealthConfig() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("KING", String.valueOf(PieceHealthConfig.getKingHealth()));
            config.put("QUEEN", String.valueOf(PieceHealthConfig.getQueenHealth()));
            config.put("ROOK", String.valueOf(PieceHealthConfig.getRookHealth()));
            config.put("BISHOP", String.valueOf(PieceHealthConfig.getBishopHealth()));
            config.put("KNIGHT", String.valueOf(PieceHealthConfig.getKnightHealth()));
            config.put("PAWN", String.valueOf(PieceHealthConfig.getPawnHealth()));
            
            saveCSV(PIECE_HEALTH_CONFIG_FILE, config,
                   "# Configuration des points de vie des pièces\n# Format: piece,health");
            System.out.println("💾 Configuration HP sauvegardée");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur sauvegarde config HP: " + e.getMessage());
        }
    }
    
    /**
     * Charge un fichier CSV et retourne une Map clé-valeur
     */
    private static Map<String, String> loadCSV(String resourcePath) throws IOException {
        Map<String, String> config = new HashMap<>();
        
        try (InputStream is = ConfigLoader.class.getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new IOException("Fichier non trouvé: " + resourcePath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                // Ignorer les commentaires et lignes vides
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Parser la ligne: clé,valeur
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    config.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
    }

        return config;
    }
    
    /**
     * Sauvegarde une Map dans un fichier CSV
     */
    private static void saveCSV(String resourcePath, Map<String, String> config, String header) throws IOException {
        // Convertir le chemin de ressource en chemin de fichier
        String filePath = "src/main/resources" + resourcePath;
        File file = new File(filePath);
        
        // Créer les dossiers si nécessaire
        file.getParentFile().mkdirs();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Écrire l'en-tête
            writer.write(header);
            writer.newLine();
            
            // Écrire les données
            for (Map.Entry<String, String> entry : config.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        }
    }
}
