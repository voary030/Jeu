package com.pong.config;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Client HTTP pour communiquer avec le service de configuration ConfigService
 */
public class ConfigServiceClient {
    
    private static final String CONFIG_SERVICE_URL = "http://localhost:8080/config-service/api/config";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    
    private static boolean serviceAvailable = false;
    
    static {
        checkServiceHealth();
    }
    
    /**
     * Vérifie la disponibilité du service de configuration
     */
    public static void checkServiceHealth() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CONFIG_SERVICE_URL + "/health"))
                    .GET()
                    .header("Accept", "application/json")
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            serviceAvailable = response.statusCode() == 200;
            
            if (serviceAvailable) {
                System.out.println("✅ Service ConfigService disponible");
            } else {
                System.out.println("⚠️ Service ConfigService indisponible (code: " + response.statusCode() + ")");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Service ConfigService indisponible: " + e.getMessage());
            serviceAvailable = false;
        }
    }
    
    /**
     * Récupère la configuration du plateau depuis le service
     */
    public static Map<String, String> getBoardConfig() throws IOException, InterruptedException {
        if (!serviceAvailable) {
            throw new IOException("Service ConfigService indisponible");
        }
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CONFIG_SERVICE_URL + "/board"))
                .GET()
                .header("Accept", "application/json")
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Erreur: " + response.body());
        }
        
        return parseJsonResponse(response.body());
    }
    
    /**
     * Sauvegarde la configuration du plateau
     */
    public static void saveBoardConfig(int numberOfPawns) throws IOException, InterruptedException {
        if (!serviceAvailable) {
            throw new IOException("Service ConfigService indisponible");
        }
        
        String json = "{\"numberOfPawns\": " + numberOfPawns + "}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CONFIG_SERVICE_URL + "/board"))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Erreur sauvegarde: " + response.body());
        }
    }
    
    /**
     * Récupère toutes les configurations des pièces
     */
    public static Map<String, String> getAllPieceHealthConfig() throws IOException, InterruptedException {
        if (!serviceAvailable) {
            throw new IOException("Service ConfigService indisponible");
        }
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CONFIG_SERVICE_URL + "/pieces"))
                .GET()
                .header("Accept", "application/json")
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Erreur: " + response.body());
        }
        
        return parseJsonResponse(response.body());
    }
    
    /**
     * Récupère la configuration de santé pour une pièce spécifique
     */
    public static int getPieceHealthConfig(String pieceName) throws IOException, InterruptedException {
        if (!serviceAvailable) {
            throw new IOException("Service ConfigService indisponible");
        }
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CONFIG_SERVICE_URL + "/pieces/" + pieceName))
                .GET()
                .header("Accept", "application/json")
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Erreur: " + response.body());
        }
        
        Map<String, String> config = parseJsonResponse(response.body());
        return Integer.parseInt(config.getOrDefault("healthPoints", "10"));
    }
    
    /**
     * Sauvegarde la configuration de santé d'une pièce
     */
    public static void savePieceHealthConfig(String pieceName, int healthPoints) throws IOException, InterruptedException {
        if (!serviceAvailable) {
            throw new IOException("Service ConfigService indisponible");
        }
        
        String json = "{\"healthPoints\": " + healthPoints + "}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CONFIG_SERVICE_URL + "/pieces/" + pieceName))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Erreur sauvegarde: " + response.body());
        }
    }
    
    /**
     * Vérifie si le service est disponible
     */
    public static boolean isServiceAvailable() {
        return serviceAvailable;
    }
    
    /**
     * Parser simple pour JSON
     */
    private static Map<String, String> parseJsonResponse(String json) {
        Map<String, String> result = new HashMap<>();
        
        // Parser basique pour "key": "value"
        String[] parts = json.split(",");
        for (String part : parts) {
            String[] kv = part.split(":");
            if (kv.length == 2) {
                String key = kv[0].replaceAll("[\"\\{\\s]", "").trim();
                String value = kv[1].replaceAll("[\"\\}\\s]", "").trim();
                if (!key.isEmpty() && !value.isEmpty()) {
                    result.put(key, value);
                }
            }
        }
        
        return result;
    }
}
