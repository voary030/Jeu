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
 * Écran de sélection du mode de jeu
 */
public class GameModeScreen extends VBox {
    
    private Button multiplayerButton;
    private Button lanButton;
    private Button vsComputerButton;
    private Button backButton;
    
    public GameModeScreen(double width, double height) {
        super(25);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);
        setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");
        
        // Titre
        Text title = new Text("🎮 MODE DE JEU 🎮");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setFill(Color.web("#e74c3c"));
        
        Text subtitle = new Text("Choisissez votre mode de jeu");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        subtitle.setFill(Color.web("#ecf0f1"));
        
        // Bouton Multijoueur Local (même écran)
        multiplayerButton = createButton("👥 Multijoueur Local", "#27ae60", "#2ecc71");
        
        // Bouton Multijoueur LAN (réseau)
        lanButton = createButton("🌐 Multijoueur LAN", "#3498db", "#5dade2");
        
        // Bouton VS Computer
        vsComputerButton = createButton("🤖 VS Computer", "#e67e22", "#d35400");
        
        // Bouton Retour
        backButton = createButton("← Retour", "#95a5a6", "#7f8c8d");
        backButton.setPrefWidth(200);
        
        // Ajouter tous les éléments
        getChildren().addAll(title, subtitle, multiplayerButton, lanButton, vsComputerButton, backButton);
        setPadding(new Insets(50));
    }
    
    private Button createButton(String text, String color, String hoverColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        button.setPrefWidth(300);
        button.setPrefHeight(55);
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
    
    public Button getMultiplayerButton() {
        return multiplayerButton;
    }
    
    public Button getLanButton() {
        return lanButton;
    }
    
    public Button getVsComputerButton() {
        return vsComputerButton;
    }
    
    public Button getBackButton() {
        return backButton;
    }
}
