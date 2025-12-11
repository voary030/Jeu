package com.pong.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Écran de menu principal du jeu
 */
public class MenuScreen extends VBox {
    
    private Button startButton;
    private Button boardConfigButton;
    private Button configButton;
    
    public MenuScreen(double width, double height) {
        super(30);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);
        setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");
        
        // Titre du jeu
        Text title = new Text("⚔️ ÉCHEC PONG ⚔️");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setFill(Color.web("#f39c12"));
        
        Text subtitle = new Text("Détruisez le Roi adverse!");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        subtitle.setFill(Color.web("#ecf0f1"));
        
        // Bouton Commencer
        startButton = createButton("Commencer", "#27ae60", "#2ecc71");
        
        // Bouton Configuration Terrain
        boardConfigButton = createButton("Configuration Terrain", "#9b59b6", "#8e44ad");
        
        // Bouton Configuration Pièces
        configButton = createButton("Configuration Pièces", "#3498db", "#5dade2");
        
        // Ajouter tous les éléments
        getChildren().addAll(title, subtitle, startButton, boardConfigButton, configButton);
        setPadding(new Insets(50));
    }
    
    private Button createButton(String text, String color, String hoverColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        button.setPrefWidth(300);
        button.setPrefHeight(60);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
                + "-fx-background-radius: 10; -fx-cursor: hand;");
        
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; "
                    + "-fx-background-radius: 10; -fx-cursor: hand;"));
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
                    + "-fx-background-radius: 10; -fx-cursor: hand;"));
        
        return button;
    }
    
    public Button getStartButton() {
        return startButton;
    }
    
    public Button getBoardConfigButton() {
        return boardConfigButton;
    }
    
    public Button getConfigButton() {
        return configButton;
    }
}
