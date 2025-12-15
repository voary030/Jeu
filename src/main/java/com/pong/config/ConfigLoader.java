package com.pong.config;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe pour charger les configurations depuis la microservice ConfigService
 * Fallback sur les fichiers CSV si le service n'est pas disponible
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
     * Charge la configuration du terrain depuis ConfigService ou CSV
     */
    public static void loadBoardConfig() {
        // Essayer d'abord le service
        if (ConfigServiceClient.isServiceAvailable()) {
            try {
                System.out.println("  📡 Chargement du terrain via ConfigService...");
                Map<String, String> config = ConfigServiceClient.getBoardConfig();
                
                if (config.containsKey("numberOfPawns")) {
                    int numberOfPawns = Integer.parseInt(config.get("numberOfPawns"));
                    BoardConfig.setNumberOfPawns(numberOfPawns);
                    System.out.println("  ✓ Terrain (service): " + numberOfPawns + " pions");
                    return;
                }
            } catch (Exception e) {
                System.err.println("⚠️ Erreur chargement via service: " + e.getMessage());
                System.out.println("  ⬇️ Basculement sur fichier CSV...");
            }
        }
        
        // Fallback sur CSV
        try {
            Map<String, String> config = loadCSV(BOARD_CONFIG_FILE);
            
            if (config.containsKey("numberOfPawns")) {
                int numberOfPawns = Integer.parseInt(config.get("numberOfPawns"));
                BoardConfig.setNumberOfPawns(numberOfPawns);
                System.out.println("  ✓ Terrain (fichier): " + numberOfPawns + " pions");
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ Erreur chargement config terrain: " + e.getMessage());
            System.err.println("   Utilisation des valeurs par défaut");
        }
    }
    
    /**
     * Charge la configuration des HP des pièces depuis ConfigService ou CSV
     */
    public static void loadPieceHealthConfig() {
        // Essayer d'abord le service
        if (ConfigServiceClient.isServiceAvailable()) {
            try {
                System.out.println("  📡 Chargement des HP via ConfigService...");
                Map<String, String> config = ConfigServiceClient.getAllPieceHealthConfig();
                
                if (!config.isEmpty()) {
                    setPieceHealthFromMap(config);
                    System.out.println("  ✓ HP (service): Roi=" + PieceHealthConfig.getKingHealth() + 
                                     ", Reine=" + PieceHealthConfig.getQueenHealth() +
                                     ", Tour=" + PieceHealthConfig.getRookHealth() +
                                     ", Fou=" + PieceHealthConfig.getBishopHealth() +
                                     ", Cavalier=" + PieceHealthConfig.getKnightHealth() +
                                     ", Pion=" + PieceHealthConfig.getPawnHealth());
                    return;
                }
            } catch (Exception e) {
                System.err.println("⚠️ Erreur chargement via service: " + e.getMessage());
                System.out.println("  ⬇️ Basculement sur fichier CSV...");
            }
        }
        
        // Fallback sur CSV
        try {
            Map<String, String> config = loadCSV(PIECE_HEALTH_CONFIG_FILE);
            setPieceHealthFromMap(config);
            
            System.out.println("  ✓ HP (fichier): Roi=" + PieceHealthConfig.getKingHealth() + 
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
     * Configure les HP des pièces depuis une Map
     */
    private static void setPieceHealthFromMap(Map<String, String> config) {
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
    }
    
    /**
     * Sauvegarde la configuration du terrain dans ConfigService et/ou CSV
     */
    public static void saveBoardConfig() {
        try {
            if (ConfigServiceClient.isServiceAvailable()) {
                try {
                    System.out.println("  📡 Sauvegarde du terrain via ConfigService...");
                    ConfigServiceClient.saveBoardConfig(BoardConfig.getNumberOfPawns());
                    System.out.println("💾 Configuration terrain sauvegardée (service)");
                    return;
                } catch (Exception e) {
                    System.err.println("⚠️ Erreur sauvegarde service: " + e.getMessage());
                    System.out.println("  ⬇️ Sauvegarde sur fichier CSV...");
                }
            }
            
            // Fallback sur CSV
            Map<String, String> config = new HashMap<>();
            config.put("numberOfPawns", String.valueOf(BoardConfig.getNumberOfPawns()));
            
            saveCSV(BOARD_CONFIG_FILE, config, 
                   "# Configuration du terrain de jeu\n# Format: parametre,valeur");
            System.out.println("💾 Configuration terrain sauvegardée (fichier)");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur sauvegarde config terrain: " + e.getMessage());
        }
    }
    
    /**
     * Sauvegarde la configuration des HP dans ConfigService et/ou CSV
     */
    public static void savePieceHealthConfig() {
        try {
            if (ConfigServiceClient.isServiceAvailable()) {
                try {
                    System.out.println("  📡 Sauvegarde des HP via ConfigService...");
                    ConfigServiceClient.savePieceHealthConfig("KING", PieceHealthConfig.getKingHealth());
                    ConfigServiceClient.savePieceHealthConfig("QUEEN", PieceHealthConfig.getQueenHealth());
                    ConfigServiceClient.savePieceHealthConfig("ROOK", PieceHealthConfig.getRookHealth());
                    ConfigServiceClient.savePieceHealthConfig("BISHOP", PieceHealthConfig.getBishopHealth());
                    ConfigServiceClient.savePieceHealthConfig("KNIGHT", PieceHealthConfig.getKnightHealth());
                    ConfigServiceClient.savePieceHealthConfig("PAWN", PieceHealthConfig.getPawnHealth());
                    System.out.println("💾 Configuration HP sauvegardée (service)");
                    return;
                } catch (Exception e) {
                    System.err.println("⚠️ Erreur sauvegarde service: " + e.getMessage());
                    System.out.println("  ⬇️ Sauvegarde sur fichier CSV...");
                }
            }
            
            // Fallback sur CSV
            Map<String, String> config = new HashMap<>();
            config.put("KING", String.valueOf(PieceHealthConfig.getKingHealth()));
            config.put("QUEEN", String.valueOf(PieceHealthConfig.getQueenHealth()));
            config.put("ROOK", String.valueOf(PieceHealthConfig.getRookHealth()));
            config.put("BISHOP", String.valueOf(PieceHealthConfig.getBishopHealth()));
            config.put("KNIGHT", String.valueOf(PieceHealthConfig.getKnightHealth()));
            config.put("PAWN", String.valueOf(PieceHealthConfig.getPawnHealth()));
            
            saveCSV(PIECE_HEALTH_CONFIG_FILE, config,
                   "# Configuration des points de vie des pièces\n# Format: piece,health");
            System.out.println("💾 Configuration HP sauvegardée (fichier)");
            
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
