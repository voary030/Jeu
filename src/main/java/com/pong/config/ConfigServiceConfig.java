package com.pong.config;

/**
 * Configuration centralisée pour le ConfigServiceClient
 * Permet de modifier facilement les paramètres de connexion au service
 */
public class ConfigServiceConfig {
    
    // ==================== Configuration de base ====================
    
    /**
     * URL de base du service ConfigService
     * Format: http://host:port/context-path/api/config
     */
    public static final String SERVICE_URL = "http://localhost:8080/config-service/api/config";
    
    /**
     * Activer/désactiver l'utilisation du service
     */
    public static final boolean SERVICE_ENABLED = true;
    
    /**
     * Activer le fallback automatique sur fichiers CSV
     */
    public static final boolean AUTO_FALLBACK_ENABLED = true;
    
    // ==================== Timeouts et Retries ====================
    
    /**
     * Timeout de connexion au service (en millisecondes)
     */
    public static final long CONNECTION_TIMEOUT = 5000;
    
    /**
     * Timeout de lecture (en millisecondes)
     */
    public static final long READ_TIMEOUT = 10000;
    
    /**
     * Nombre de tentatives avant de basculer sur fallback
     */
    public static final int MAX_RETRIES = 1;
    
    /**
     * Délai avant retry (en millisecondes)
     */
    public static final long RETRY_DELAY = 500;
    
    // ==================== Chemins des fichiers CSV ====================
    
    /**
     * Chemin du fichier CSV de configuration du plateau
     */
    public static final String BOARD_CONFIG_FILE = "/config/board_config.csv";
    
    /**
     * Chemin du fichier CSV de configuration des pièces
     */
    public static final String PIECE_HEALTH_CONFIG_FILE = "/config/piece_health_config.csv";
    
    // ==================== Valeurs par défaut ====================
    
    /**
     * Nombre de pions par défaut
     */
    public static final int DEFAULT_NUMBER_OF_PAWNS = 8;
    
    /**
     * Points de vie par défaut des pièces
     */
    public static final int DEFAULT_KING_HEALTH = 100;
    public static final int DEFAULT_QUEEN_HEALTH = 90;
    public static final int DEFAULT_ROOK_HEALTH = 50;
    public static final int DEFAULT_BISHOP_HEALTH = 40;
    public static final int DEFAULT_KNIGHT_HEALTH = 30;
    public static final int DEFAULT_PAWN_HEALTH = 10;
    
    // ==================== Paramètres de log ====================
    
    /**
     * Activer les logs détaillés
     */
    public static final boolean DEBUG_LOGGING = false;
    
    /**
     * Afficher les temps de réponse
     */
    public static final boolean LOG_RESPONSE_TIME = false;
    
    // ==================== Paramètres HTTP ====================
    
    /**
     * User-Agent pour les requêtes HTTP
     */
    public static final String USER_AGENT = "Pong-JavaFX/1.0";
    
    /**
     * Header Content-Type par défaut
     */
    public static final String CONTENT_TYPE = "application/json";
    
    // ==================== Méthodes utilitaires ====================
    
    /**
     * Obtient l'URL complète pour un endpoint
     */
    public static String getEndpointUrl(String endpoint) {
        return SERVICE_URL + endpoint;
    }
    
    /**
     * Obtient la santé par défaut pour une pièce
     */
    public static int getDefaultHealthForPiece(String pieceName) {
        switch(pieceName.toUpperCase()) {
            case "KING": return DEFAULT_KING_HEALTH;
            case "QUEEN": return DEFAULT_QUEEN_HEALTH;
            case "ROOK": return DEFAULT_ROOK_HEALTH;
            case "BISHOP": return DEFAULT_BISHOP_HEALTH;
            case "KNIGHT": return DEFAULT_KNIGHT_HEALTH;
            case "PAWN": return DEFAULT_PAWN_HEALTH;
            default: return 10;
        }
    }
    
    /**
     * Valide si le service URL est correctement configurée
     */
    public static boolean validateServiceUrl() {
        try {
            return SERVICE_URL != null 
                && !SERVICE_URL.isEmpty() 
                && SERVICE_URL.startsWith("http");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Affiche la configuration actuelle
     */
    public static void displayConfig() {
        System.out.println("=== Configuration ConfigService ===");
        System.out.println("Service URL: " + SERVICE_URL);
        System.out.println("Service activé: " + SERVICE_ENABLED);
        System.out.println("Fallback automatique: " + AUTO_FALLBACK_ENABLED);
        System.out.println("Connection timeout: " + CONNECTION_TIMEOUT + "ms");
        System.out.println("Read timeout: " + READ_TIMEOUT + "ms");
        System.out.println("Max retries: " + MAX_RETRIES);
        System.out.println("Debug logging: " + DEBUG_LOGGING);
        System.out.println("===================================");
    }
}
