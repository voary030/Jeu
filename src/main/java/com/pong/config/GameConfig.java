package com.pong.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Classe pour gérer la configuration du jeu
 */
public class GameConfig {
    private static final Properties properties = new Properties();
    
    static {
        try (InputStream input = GameConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String getPlayer1Name() {
        return properties.getProperty("player1.name", "Joueur Blanc");
    }
    
    public static String getPlayer2Name() {
        return properties.getProperty("player2.name", "Joueur Noir");
    }
}
