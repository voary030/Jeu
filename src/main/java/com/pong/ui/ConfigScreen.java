package com.pong.ui;

import com.pong.config.PieceHealthConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
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
    
    public ConfigScreen(double width, double height) {
        super(20);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);
        setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");
        setPadding(new Insets(30));
        
        // Titre
        Text title = new Text("⚙️ Configuration des Pièces");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setFill(Color.web("#f39c12"));
        
        // Grille de configuration
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        // Configuration pour chaque pièce
        kingHpSpinner = addPieceConfig(grid, 0, "♔ Roi", PieceHealthConfig.getKingHealth(), 1, 20);
        queenHpSpinner = addPieceConfig(grid, 1, "♕ Reine", PieceHealthConfig.getQueenHealth(), 1, 15);
        rookHpSpinner = addPieceConfig(grid, 2, "♖ Tour", PieceHealthConfig.getRookHealth(), 1, 10);
        bishopHpSpinner = addPieceConfig(grid, 3, "♗ Fou", PieceHealthConfig.getBishopHealth(), 1, 10);
        knightHpSpinner = addPieceConfig(grid, 4, "♘ Cavalier", PieceHealthConfig.getKnightHealth(), 1, 10);
        pawnHpSpinner = addPieceConfig(grid, 5, "♙ Pion", PieceHealthConfig.getPawnHealth(), 1, 5);
        
        // Boutons
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        saveButton = createButton("Enregistrer", "#27ae60", "#2ecc71");
        cancelButton = createButton("Annuler", "#e74c3c", "#c0392b");
        
        buttonBox.getChildren().addAll(saveButton, cancelButton);
        
        getChildren().addAll(title, grid, buttonBox);
    }
    
    private Spinner<Integer> addPieceConfig(GridPane grid, int row, String pieceName, int currentValue, int min, int max) {
        Label label = new Label(pieceName + " - Points de vie:");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.web("#ecf0f1"));
        
        Spinner<Integer> spinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, currentValue);
        spinner.setValueFactory(valueFactory);
        spinner.setPrefWidth(100);
        spinner.setStyle("-fx-font-size: 14px;");
        
        grid.add(label, 0, row);
        grid.add(spinner, 1, row);
        
        return spinner;
    }
    
    private Button createButton(String text, String color, String hoverColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
                + "-fx-background-radius: 8; -fx-cursor: hand;");
        
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; "
                    + "-fx-background-radius: 8; -fx-cursor: hand;"));
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
                    + "-fx-background-radius: 8; -fx-cursor: hand;"));
        
        return button;
    }
    
    public void saveConfiguration() {
        PieceHealthConfig.setKingHealth(kingHpSpinner.getValue());
        PieceHealthConfig.setQueenHealth(queenHpSpinner.getValue());
        PieceHealthConfig.setRookHealth(rookHpSpinner.getValue());
        PieceHealthConfig.setBishopHealth(bishopHpSpinner.getValue());
        PieceHealthConfig.setKnightHealth(knightHpSpinner.getValue());
        PieceHealthConfig.setPawnHealth(pawnHpSpinner.getValue());
        
        // Sauvegarder dans le fichier CSV
        com.pong.config.ConfigLoader.savePieceHealthConfig();
    }
    
    public Button getSaveButton() {
        return saveButton;
    }
    
    public Button getCancelButton() {
        return cancelButton;
    }
}
