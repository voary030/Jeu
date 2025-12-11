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
public class PieceHealthConfigScreen extends VBox {
    
    private Spinner<Integer> kingSpinner;
    private Spinner<Integer> queenSpinner;
    private Spinner<Integer> rookSpinner;
    private Spinner<Integer> bishopSpinner;
    private Spinner<Integer> knightSpinner;
    private Spinner<Integer> pawnSpinner;
    
    private Button validateButton;
    private Button cancelButton;
    private Button resetButton;
    
    // Palette moderne
    private static final String BG_DARK = "#0a0a0f";
    private static final String BG_CARD = "#14141f";
    private static final String BG_SECTION = "#1a1a2e";
    private static final String ACCENT_ROSE = "#f43f5e";
    private static final String ACCENT_GREEN = "#10b981";
    private static final String ACCENT_AMBER = "#f59e0b";
    private static final String TEXT_LIGHT = "#e2e8f0";
    private static final String TEXT_DIM = "#94a3b8";
    
    public PieceHealthConfigScreen(double width, double height) {
        super(0);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);
        setStyle("-fx-background-color: " + BG_DARK + ";");
        setPadding(new Insets(30));
        
        // Carte principale
        VBox mainCard = new VBox(20);
        mainCard.setAlignment(Pos.CENTER);
        mainCard.setPadding(new Insets(35, 50, 35, 50));
        mainCard.setMaxWidth(500);
        mainCard.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 20;");
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(ACCENT_ROSE, 0.25));
        shadow.setRadius(35);
        mainCard.setEffect(shadow);
        
        // En-tête
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        
        Text title = new Text("Points de Vie");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_LIGHT));
        
        Text subtitle = new Text("Ajustez la résistance de chaque pièce");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 13));
        subtitle.setFill(Color.web(TEXT_DIM));
        
        header.getChildren().addAll(title, subtitle);
        
        // Grille de spinners - 2 colonnes
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(35);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 25, 20, 25));
        grid.setStyle("-fx-background-color: " + BG_SECTION + "; -fx-background-radius: 14;");
        
        // Colonne gauche
        kingSpinner = addPieceRow(grid, 0, 0, "Roi", "K", PieceHealthConfig.getKingHealth(), 1, 50);
        queenSpinner = addPieceRow(grid, 1, 0, "Reine", "Q", PieceHealthConfig.getQueenHealth(), 1, 30);
        rookSpinner = addPieceRow(grid, 2, 0, "Tour", "R", PieceHealthConfig.getRookHealth(), 1, 20);
        
        // Colonne droite
        bishopSpinner = addPieceRow(grid, 0, 2, "Fou", "B", PieceHealthConfig.getBishopHealth(), 1, 15);
        knightSpinner = addPieceRow(grid, 1, 2, "Cavalier", "N", PieceHealthConfig.getKnightHealth(), 1, 15);
        pawnSpinner = addPieceRow(grid, 2, 2, "Pion", "P", PieceHealthConfig.getPawnHealth(), 1, 10);
        
        // Boutons
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        validateButton = createStyledButton("Valider", ACCENT_GREEN, true);
        resetButton = createStyledButton("Réinitialiser", ACCENT_AMBER, true);
        cancelButton = createStyledButton("Annuler", "#64748b", false);
        
        // Réinitialiser aux valeurs par défaut
        resetButton.setOnAction(e -> resetToDefaults());
        
        buttonBox.getChildren().addAll(validateButton, resetButton, cancelButton);
        
        mainCard.getChildren().addAll(header, grid, buttonBox);
        getChildren().add(mainCard);
    }
    
    private Spinner<Integer> addPieceRow(GridPane grid, int row, int colOffset, String name, String symbol, int value, int min, int max) {
        // Badge avec symbole
        Label badge = new Label(symbol);
        badge.setFont(Font.font("System", FontWeight.BOLD, 13));
        badge.setTextFill(Color.web(ACCENT_ROSE));
        badge.setMinWidth(26);
        badge.setMinHeight(26);
        badge.setAlignment(Pos.CENTER);
        badge.setStyle("-fx-background-color: " + ACCENT_ROSE + "22; -fx-background-radius: 6;");
        
        // Conteneur badge + nom
        HBox labelBox = new HBox(8);
        labelBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        nameLabel.setTextFill(Color.web(TEXT_LIGHT));
        nameLabel.setMinWidth(65);
        
        labelBox.getChildren().addAll(badge, nameLabel);
        
        // Spinner
        Spinner<Integer> spinner = new Spinner<>();
        SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, value);
        spinner.setValueFactory(factory);
        spinner.setPrefWidth(75);
        spinner.setEditable(true);
        spinner.setStyle("-fx-background-color: #252538; -fx-font-size: 12px;");
        
        grid.add(labelBox, colOffset, row);
        grid.add(spinner, colOffset + 1, row);
        
        return spinner;
    }
    
    private void resetToDefaults() {
        kingSpinner.getValueFactory().setValue(10);
        queenSpinner.getValueFactory().setValue(8);
        rookSpinner.getValueFactory().setValue(5);
        bishopSpinner.getValueFactory().setValue(3);
        knightSpinner.getValueFactory().setValue(3);
        pawnSpinner.getValueFactory().setValue(2);
    }
    
    private Button createStyledButton(String text, String color, boolean filled) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 13));
        button.setPrefWidth(115);
        button.setPrefHeight(40);
        
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
    
    public void applyConfiguration() {
        PieceHealthConfig.setKingHealth(kingSpinner.getValue());
        PieceHealthConfig.setQueenHealth(queenSpinner.getValue());
        PieceHealthConfig.setRookHealth(rookSpinner.getValue());
        PieceHealthConfig.setBishopHealth(bishopSpinner.getValue());
        PieceHealthConfig.setKnightHealth(knightSpinner.getValue());
        PieceHealthConfig.setPawnHealth(pawnSpinner.getValue());
        
        com.pong.config.ConfigLoader.savePieceHealthConfig();
    }
    
    public Button getValidateButton() {
        return validateButton;
    }
    
    public Button getCancelButton() {
        return cancelButton;
    }
    
    public Button getResetButton() {
        return resetButton;
    }
}
