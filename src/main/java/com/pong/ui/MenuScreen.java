package com.pong.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Écran d'accueil principal du jeu
 */
public class MenuScreen extends VBox {
    
    private Button startButton;
    private Button boardConfigButton;
    private Button configButton;
    
    // Palette de couleurs moderne
    private static final String BG_DARK = "#0a0a0f";
    private static final String BG_CARD = "#14141f";
    private static final String ACCENT_PRIMARY = "#7c3aed";
    private static final String ACCENT_SECONDARY = "#06b6d4";
    private static final String ACCENT_TERTIARY = "#ec4899";
    private static final String TEXT_LIGHT = "#e2e8f0";
    private static final String TEXT_DIM = "#94a3b8";
    
    public MenuScreen(double width, double height) {
        super(20);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);
        setStyle("-fx-background-color: " + BG_DARK + ";");
        
        // Conteneur principal avec effet carte
        VBox mainCard = new VBox(25);
        mainCard.setAlignment(Pos.CENTER);
        mainCard.setPadding(new Insets(50, 60, 50, 60));
        mainCard.setMaxWidth(450);
        mainCard.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 20;");
        
        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.web(ACCENT_PRIMARY, 0.3));
        cardShadow.setRadius(30);
        cardShadow.setSpread(0.1);
        mainCard.setEffect(cardShadow);
        
        // Section titre
        VBox titleSection = new VBox(8);
        titleSection.setAlignment(Pos.CENTER);
        
        Text title = new Text("CHESS STRIKER");
        title.setFont(Font.font("System", FontWeight.BLACK, 38));
        title.setFill(Color.web(TEXT_LIGHT));
        
        // Ligne décorative
        HBox decorLine = new HBox(8);
        decorLine.setAlignment(Pos.CENTER);
        Line line1 = createDecorLine(60, ACCENT_TERTIARY);
        Line line2 = createDecorLine(30, ACCENT_PRIMARY);
        Line line3 = createDecorLine(60, ACCENT_SECONDARY);
        decorLine.getChildren().addAll(line1, line2, line3);
        
        Text subtitle = new Text("Stratégie • Action • Victoire");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setFill(Color.web(TEXT_DIM));
        
        titleSection.getChildren().addAll(title, decorLine, subtitle);
        
        // Section boutons
        VBox buttonSection = new VBox(15);
        buttonSection.setAlignment(Pos.CENTER);
        buttonSection.setPadding(new Insets(20, 0, 0, 0));
        
        startButton = createMenuButton("NOUVELLE PARTIE", ACCENT_PRIMARY, true);
        boardConfigButton = createMenuButton("Paramètres Terrain", ACCENT_SECONDARY, false);
        configButton = createMenuButton("Paramètres Pièces", ACCENT_TERTIARY, false);
        
        buttonSection.getChildren().addAll(startButton, boardConfigButton, configButton);
        
        mainCard.getChildren().addAll(titleSection, buttonSection);
        
        getChildren().add(mainCard);
        setPadding(new Insets(40));
    }
    
    private Line createDecorLine(double width, String color) {
        Line line = new Line(0, 0, width, 0);
        line.setStroke(Color.web(color));
        line.setStrokeWidth(3);
        return line;
    }
    
    private Button createMenuButton(String text, String accentColor, boolean isPrimary) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, isPrimary ? 16 : 14));
        button.setPrefWidth(280);
        button.setPrefHeight(isPrimary ? 55 : 45);
        
        String baseStyle;
        String hoverStyle;
        
        if (isPrimary) {
            baseStyle = "-fx-background-color: " + accentColor + "; -fx-text-fill: white; "
                    + "-fx-background-radius: 12; -fx-cursor: hand; -fx-border-color: transparent;";
            hoverStyle = "-fx-background-color: derive(" + accentColor + ", 20%); -fx-text-fill: white; "
                    + "-fx-background-radius: 12; -fx-cursor: hand; -fx-border-color: transparent;";
        } else {
            baseStyle = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_LIGHT + "; "
                    + "-fx-background-radius: 12; -fx-cursor: hand; -fx-border-color: " + accentColor + "; -fx-border-width: 2; -fx-border-radius: 10;";
            hoverStyle = "-fx-background-color: " + accentColor + "22; -fx-text-fill: " + accentColor + "; "
                    + "-fx-background-radius: 12; -fx-cursor: hand; -fx-border-color: " + accentColor + "; -fx-border-width: 2; -fx-border-radius: 10;";
        }
        
        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        
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
