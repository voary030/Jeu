package com.pong.ui;

import com.pong.config.PieceHealthConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Écran de configuration des points de vie des pièces
 */
public class ConfigScreen extends VBox {
    
    private Spinner<Integer> kingHpSpinner;
    private Spinner<Integer> queenHpSpinner;
    private Spinner<Integer> rookHpSpinner;
    private Spinner<Integer> bishopHpSpinner;
    private Spinner<Integer> knightHpSpinner;
    private Spinner<Integer> pawnHpSpinner;
    private Button saveButton;
    private Button cancelButton;
    
    // Palette moderne
    private static final String BG_DARK = "#0a0a0f";
    private static final String BG_CARD = "#14141f";
    private static final String ACCENT_PINK = "#ec4899";
    private static final String ACCENT_GREEN = "#10b981";
    private static final String TEXT_LIGHT = "#e2e8f0";
    private static final String TEXT_DIM = "#94a3b8";
    
    public ConfigScreen(double width, double height) {
        super(0);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);
        setStyle("-fx-background-color: " + BG_DARK + ";");
        setPadding(new Insets(30));
        
        // Carte principale
        VBox mainCard = new VBox(25);
        mainCard.setAlignment(Pos.CENTER);
        mainCard.setPadding(new Insets(35, 45, 35, 45));
        mainCard.setMaxWidth(480);
        mainCard.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 20;");
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(ACCENT_PINK, 0.25));
        shadow.setRadius(35);
        mainCard.setEffect(shadow);
        
        // En-tête
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        
        Text title = new Text("Configuration des Pièces");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_LIGHT));
        
        Text subtitle = new Text("Ajustez les points de vie");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 13));
        subtitle.setFill(Color.web(TEXT_DIM));
        
        header.getChildren().addAll(title, subtitle);
        
        // Grille de configuration
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(30);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 12;");
        
        // Colonne gauche
        kingHpSpinner = addConfigRow(grid, 0, "Roi", "K", PieceHealthConfig.getKingHealth(), 1, 20);
        queenHpSpinner = addConfigRow(grid, 1, "Reine", "Q", PieceHealthConfig.getQueenHealth(), 1, 15);
        rookHpSpinner = addConfigRow(grid, 2, "Tour", "R", PieceHealthConfig.getRookHealth(), 1, 10);
        
        // Colonne droite
        bishopHpSpinner = addConfigRow(grid, 0, "Fou", "B", PieceHealthConfig.getBishopHealth(), 1, 10, 3);
        knightHpSpinner = addConfigRow(grid, 1, "Cavalier", "N", PieceHealthConfig.getKnightHealth(), 1, 10, 3);
        pawnHpSpinner = addConfigRow(grid, 2, "Pion", "P", PieceHealthConfig.getPawnHealth(), 1, 5, 3);
        
        // Boutons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));
        
        saveButton = createStyledButton("Enregistrer", ACCENT_GREEN, true);
        cancelButton = createStyledButton("Annuler", "#64748b", false);
        
        buttonBox.getChildren().addAll(saveButton, cancelButton);
        
        mainCard.getChildren().addAll(header, grid, buttonBox);
        getChildren().add(mainCard);
    }
    
    private Spinner<Integer> addConfigRow(GridPane grid, int row, String name, String symbol, int value, int min, int max) {
        return addConfigRow(grid, row, name, symbol, value, min, max, 0);
    }
    
    private Spinner<Integer> addConfigRow(GridPane grid, int row, String name, String symbol, int value, int min, int max, int colOffset) {
        // Badge avec symbole
        Label badge = new Label(symbol);
        badge.setFont(Font.font("System", FontWeight.BOLD, 14));
        badge.setTextFill(Color.web(ACCENT_PINK));
        badge.setMinWidth(28);
        badge.setMinHeight(28);
        badge.setAlignment(Pos.CENTER);
        badge.setStyle("-fx-background-color: " + ACCENT_PINK + "22; -fx-background-radius: 6;");
        
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        nameLabel.setTextFill(Color.web(TEXT_LIGHT));
        nameLabel.setMinWidth(70);
        
        Spinner<Integer> spinner = new Spinner<>();
        SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, value);
        spinner.setValueFactory(factory);
        spinner.setPrefWidth(75);
        spinner.setEditable(true);
        spinner.setStyle("-fx-background-color: #252538; -fx-font-size: 12px;");
        
        grid.add(badge, colOffset, row);
        grid.add(nameLabel, colOffset + 1, row);
        grid.add(spinner, colOffset + 2, row);
        
        return spinner;
    }
    
    private Button createStyledButton(String text, String color, boolean filled) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 14));
        button.setPrefWidth(140);
        button.setPrefHeight(42);
        
        String baseStyle, hoverStyle;
        if (filled) {
            baseStyle = "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;";
            hoverStyle = "-fx-background-color: derive(" + color + ", 15%); -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;";
        } else {
            baseStyle = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_LIGHT + "; -fx-background-radius: 10; -fx-cursor: hand; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 8;";
            hoverStyle = "-fx-background-color: " + color + "33; -fx-text-fill: " + TEXT_LIGHT + "; -fx-background-radius: 10; -fx-cursor: hand; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 8;";
        }
        
        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        
        return button;
    }
    
    public void saveConfiguration() {
        PieceHealthConfig.setKingHealth(kingHpSpinner.getValue());
        PieceHealthConfig.setQueenHealth(queenHpSpinner.getValue());
        PieceHealthConfig.setRookHealth(rookHpSpinner.getValue());
        PieceHealthConfig.setBishopHealth(bishopHpSpinner.getValue());
        PieceHealthConfig.setKnightHealth(knightHpSpinner.getValue());
        PieceHealthConfig.setPawnHealth(pawnHpSpinner.getValue());
        
        com.pong.config.ConfigLoader.savePieceHealthConfig();
    }
    
    public Button getSaveButton() {
        return saveButton;
    }
    
    public Button getCancelButton() {
        return cancelButton;
    }
}
