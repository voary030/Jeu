package com.pong.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Boîte de dialogue affichée quand un joueur gagne
 */
public class VictoryDialog {
    
    public enum Action {
        CONTINUE,
        STOP
    }
    
    private Action selectedAction = null;
    
    public Action show(String winnerName, String scoreText, Stage owner) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Victoire!");
        
        // Layout principal
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e); "
                + "-fx-border-color: #f39c12; -fx-border-width: 3; -fx-border-radius: 10; "
                + "-fx-background-radius: 10;");
        
        // Titre de victoire
        Label titleLabel = new Label("🏆 VICTOIRE ! 🏆");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#f39c12"));
        
        // Nom du gagnant
        Label winnerLabel = new Label(winnerName + " a gagné!");
        winnerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        winnerLabel.setTextFill(Color.WHITE);
        
        // Score
        Label scoreLabel = new Label("Score: " + scoreText);
        scoreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        scoreLabel.setTextFill(Color.web("#ecf0f1"));
        
        // Bouton Continuer
        Button continueButton = new Button("Continuer la partie");
        continueButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        continueButton.setPrefWidth(200);
        continueButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; "
                + "-fx-background-radius: 5; -fx-cursor: hand;");
        continueButton.setOnMouseEntered(e -> 
            continueButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; "
                    + "-fx-background-radius: 5; -fx-cursor: hand;"));
        continueButton.setOnMouseExited(e -> 
            continueButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; "
                    + "-fx-background-radius: 5; -fx-cursor: hand;"));
        continueButton.setOnAction(e -> {
            selectedAction = Action.CONTINUE;
            dialog.close();
        });
        
        // Bouton Arrêter
        Button stopButton = new Button("Arrêter la partie");
        stopButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        stopButton.setPrefWidth(200);
        stopButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; "
                + "-fx-background-radius: 5; -fx-cursor: hand;");
        stopButton.setOnMouseEntered(e -> 
            stopButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; "
                    + "-fx-background-radius: 5; -fx-cursor: hand;"));
        stopButton.setOnMouseExited(e -> 
            stopButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; "
                    + "-fx-background-radius: 5; -fx-cursor: hand;"));
        stopButton.setOnAction(e -> {
            selectedAction = Action.STOP;
            dialog.close();
        });
        
        layout.getChildren().addAll(titleLabel, winnerLabel, scoreLabel, continueButton, stopButton);
        
        Scene scene = new Scene(layout, 400, 350);
        dialog.setScene(scene);
        dialog.showAndWait();
        
        return selectedAction;
    }
}
