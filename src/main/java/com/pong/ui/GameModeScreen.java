package com.pong.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    
    // Palette cohérente
    private static final String BG_DARK = "#0a0a0f";
    private static final String BG_CARD = "#14141f";
    private static final String ACCENT_GREEN = "#10b981";
    private static final String ACCENT_BLUE = "#3b82f6";
    private static final String ACCENT_ORANGE = "#f59e0b";
    private static final String TEXT_LIGHT = "#e2e8f0";
    private static final String TEXT_DIM = "#64748b";
    
    public GameModeScreen(double width, double height) {
        super(0);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);
        setStyle("-fx-background-color: " + BG_DARK + ";");
        
        // Conteneur principal
        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setMaxWidth(550);
        mainContainer.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 24;");
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#3b82f6", 0.25));
        shadow.setRadius(40);
        mainContainer.setEffect(shadow);
        
        // En-tête
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        
        Text title = new Text("Sélection du Mode");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setFill(Color.web(TEXT_LIGHT));
        
        Text subtitle = new Text("Comment souhaitez-vous jouer ?");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setFill(Color.web(TEXT_DIM));
        
        header.getChildren().addAll(title, subtitle);
        
        // Cartes de mode - disposition horizontale
        HBox modeCards = new HBox(20);
        modeCards.setAlignment(Pos.CENTER);
        modeCards.setPadding(new Insets(20, 0, 20, 0));
        
        VBox localCard = createModeCard("Local", "2 joueurs\nmême écran", ACCENT_GREEN, "1");
        multiplayerButton = (Button) localCard.lookup(".mode-btn");
        
        VBox lanCard = createModeCard("Réseau", "Multijoueur\nLAN", ACCENT_BLUE, "2");
        lanButton = (Button) lanCard.lookup(".mode-btn");
        
        VBox aiCard = createModeCard("Solo", "Contre\nl'ordinateur", ACCENT_ORANGE, "3");
        vsComputerButton = (Button) aiCard.lookup(".mode-btn");
        
        modeCards.getChildren().addAll(localCard, lanCard, aiCard);
        
        // Bouton retour
        backButton = new Button("Retour au menu");
        backButton.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        backButton.setPrefWidth(160);
        backButton.setPrefHeight(38);
        String backBaseStyle = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_DIM + "; "
                + "-fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: " + TEXT_DIM + "; -fx-border-width: 1; -fx-border-radius: 8;";
        String backHoverStyle = "-fx-background-color: #1e293b; -fx-text-fill: " + TEXT_LIGHT + "; "
                + "-fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: " + TEXT_LIGHT + "; -fx-border-width: 1; -fx-border-radius: 8;";
        backButton.setStyle(backBaseStyle);
        backButton.setOnMouseEntered(e -> backButton.setStyle(backHoverStyle));
        backButton.setOnMouseExited(e -> backButton.setStyle(backBaseStyle));
        
        mainContainer.getChildren().addAll(header, modeCards, backButton);
        getChildren().add(mainContainer);
        setPadding(new Insets(30));
    }
    
    private VBox createModeCard(String title, String description, String accentColor, String number) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25, 20, 25, 20));
        card.setPrefWidth(140);
        card.setStyle("-fx-background-color: #1e1e2e; -fx-background-radius: 16;");
        
        // Numéro dans un cercle
        Circle numCircle = new Circle(18);
        numCircle.setFill(Color.web(accentColor, 0.15));
        numCircle.setStroke(Color.web(accentColor));
        numCircle.setStrokeWidth(2);
        
        Text numText = new Text(number);
        numText.setFont(Font.font("System", FontWeight.BOLD, 16));
        numText.setFill(Color.web(accentColor));
        
        javafx.scene.layout.StackPane numContainer = new javafx.scene.layout.StackPane(numCircle, numText);
        
        // Titre
        Text titleText = new Text(title);
        titleText.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleText.setFill(Color.web(TEXT_LIGHT));
        
        // Description
        Text descText = new Text(description);
        descText.setFont(Font.font("System", FontWeight.NORMAL, 11));
        descText.setFill(Color.web(TEXT_DIM));
        descText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        // Bouton
        Button btn = new Button("Jouer");
        btn.getStyleClass().add("mode-btn");
        btn.setFont(Font.font("System", FontWeight.BOLD, 12));
        btn.setPrefWidth(100);
        btn.setPrefHeight(35);
        
        String btnBase = "-fx-background-color: " + accentColor + "; -fx-text-fill: white; "
                + "-fx-background-radius: 8; -fx-cursor: hand;";
        String btnHover = "-fx-background-color: derive(" + accentColor + ", 15%); -fx-text-fill: white; "
                + "-fx-background-radius: 8; -fx-cursor: hand;";
        
        btn.setStyle(btnBase);
        btn.setOnMouseEntered(e -> btn.setStyle(btnHover));
        btn.setOnMouseExited(e -> btn.setStyle(btnBase));
        
        card.getChildren().addAll(numContainer, titleText, descText, btn);
        
        // Hover effet sur la carte
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #252538; -fx-background-radius: 16;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #1e1e2e; -fx-background-radius: 16;"));
        
        return card;
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
