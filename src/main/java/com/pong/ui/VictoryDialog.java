package com.pong.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    
    // Palette moderne
    private static final String BG_DARK = "#0a0a0f";
    private static final String BG_CARD = "#14141f";
    private static final String ACCENT_GOLD = "#fbbf24";
    private static final String ACCENT_GREEN = "#10b981";
    private static final String ACCENT_ROSE = "#f43f5e";
    private static final String TEXT_LIGHT = "#e2e8f0";
    private static final String TEXT_DIM = "#94a3b8";
    
    public Action show(String winnerName, String scoreText, Stage owner) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Victoire!");
        
        // Layout principal
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40, 50, 40, 50));
        layout.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 20;");
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(ACCENT_GOLD, 0.4));
        shadow.setRadius(40);
        shadow.setSpread(0.1);
        layout.setEffect(shadow);
        
        // Icône trophée stylisée
        Circle trophy = new Circle(35);
        trophy.setFill(Color.web(ACCENT_GOLD, 0.15));
        trophy.setStroke(Color.web(ACCENT_GOLD));
        trophy.setStrokeWidth(3);
        
        Label trophyIcon = new Label("★");
        trophyIcon.setFont(Font.font("System", FontWeight.BOLD, 36));
        trophyIcon.setTextFill(Color.web(ACCENT_GOLD));
        
        javafx.scene.layout.StackPane trophyContainer = new javafx.scene.layout.StackPane(trophy, trophyIcon);
        
        // Titre de victoire
        Label titleLabel = new Label("VICTOIRE");
        titleLabel.setFont(Font.font("System", FontWeight.BLACK, 28));
        titleLabel.setTextFill(Color.web(ACCENT_GOLD));
        
        // Nom du gagnant
        Label winnerLabel = new Label(winnerName + " remporte la manche !");
        winnerLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        winnerLabel.setTextFill(Color.web(TEXT_LIGHT));
        
        // Score dans un badge
        Label scoreLabel = new Label("Score : " + scoreText);
        scoreLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        scoreLabel.setTextFill(Color.web(TEXT_DIM));
        scoreLabel.setPadding(new Insets(8, 16, 8, 16));
        scoreLabel.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8;");
        
        // Boutons côte à côte
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));
        
        Button continueButton = createDialogButton("Continuer", ACCENT_GREEN, true);
        continueButton.setOnAction(e -> {
            selectedAction = Action.CONTINUE;
            dialog.close();
        });
        
        Button stopButton = createDialogButton("Arrêter", ACCENT_ROSE, false);
        stopButton.setOnAction(e -> {
            selectedAction = Action.STOP;
            dialog.close();
        });
        
        buttonBox.getChildren().addAll(continueButton, stopButton);
        
        layout.getChildren().addAll(trophyContainer, titleLabel, winnerLabel, scoreLabel, buttonBox);
        
        // Conteneur externe pour le fond sombre
        VBox outerContainer = new VBox(layout);
        outerContainer.setAlignment(Pos.CENTER);
        outerContainer.setStyle("-fx-background-color: " + BG_DARK + "ee;");
        outerContainer.setPadding(new Insets(30));
        
        Scene scene = new Scene(outerContainer, 420, 380);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.showAndWait();
        
        return selectedAction;
    }
    
    private Button createDialogButton(String text, String color, boolean filled) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 14));
        button.setPrefWidth(140);
        button.setPrefHeight(45);
        
        String baseStyle, hoverStyle;
        
        if (filled) {
            baseStyle = "-fx-background-color: " + color + "; -fx-text-fill: white; "
                    + "-fx-background-radius: 10; -fx-cursor: hand;";
            hoverStyle = "-fx-background-color: derive(" + color + ", 15%); -fx-text-fill: white; "
                    + "-fx-background-radius: 10; -fx-cursor: hand;";
        } else {
            baseStyle = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_LIGHT + "; "
                    + "-fx-background-radius: 10; -fx-cursor: hand; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 8;";
            hoverStyle = "-fx-background-color: " + color + "33; -fx-text-fill: " + color + "; "
                    + "-fx-background-radius: 10; -fx-cursor: hand; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 8;";
        }
        
        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        
        return button;
    }
}
