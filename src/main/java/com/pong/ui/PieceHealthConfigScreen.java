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
    
    public PieceHealthConfigScreen(double width, double height) {
        super(20);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);
        setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");
        setPadding(new Insets(30));
        
        // Titre
        Text title = new Text("❤️ Configuration des Points de Vie");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setFill(Color.web("#e74c3c"));
        
        Text subtitle = new Text("Définissez les HP de chaque type de pièce");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitle.setFill(Color.web("#ecf0f1"));
        
        // Grille de spinners
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 10;");
        
        // Roi
        addPieceRow(grid, 0, "♔ Roi:", kingSpinner = createSpinner(PieceHealthConfig.getKingHealth(), 1, 50));
        
        // Reine
        addPieceRow(grid, 1, "♕ Reine:", queenSpinner = createSpinner(PieceHealthConfig.getQueenHealth(), 1, 30));
        
        // Tour
        addPieceRow(grid, 2, "♖ Tour:", rookSpinner = createSpinner(PieceHealthConfig.getRookHealth(), 1, 20));
        
        // Fou
        addPieceRow(grid, 3, "♗ Fou:", bishopSpinner = createSpinner(PieceHealthConfig.getBishopHealth(), 1, 15));
        
        // Cavalier
        addPieceRow(grid, 4, "♘ Cavalier:", knightSpinner = createSpinner(PieceHealthConfig.getKnightHealth(), 1, 15));
        
        // Pion
        addPieceRow(grid, 5, "♙ Pion:", pawnSpinner = createSpinner(PieceHealthConfig.getPawnHealth(), 1, 10));
        
        // Boutons
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        validateButton = createButton("Valider", "#27ae60", "#2ecc71");
        resetButton = createButton("Réinitialiser", "#f39c12", "#e67e22");
        cancelButton = createButton("Annuler", "#e74c3c", "#c0392b");
        
        buttonBox.getChildren().addAll(validateButton, resetButton, cancelButton);
        
        // Réinitialiser aux valeurs par défaut
        resetButton.setOnAction(e -> resetToDefaults());
        
        getChildren().addAll(title, subtitle, grid, buttonBox);
    }
    
    private void addPieceRow(GridPane grid, int row, String label, Spinner<Integer> spinner) {
        Label pieceLabel = new Label(label);
        pieceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        pieceLabel.setTextFill(Color.web("#ecf0f1"));
        
        grid.add(pieceLabel, 0, row);
        grid.add(spinner, 1, row);
    }
    
    private Spinner<Integer> createSpinner(int initialValue, int min, int max) {
        Spinner<Integer> spinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue);
        spinner.setValueFactory(valueFactory);
        spinner.setPrefWidth(100);
        spinner.setEditable(true);
        spinner.setStyle("-fx-background-color: #34495e; -fx-text-fill: white;");
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
    
    public void applyConfiguration() {
        PieceHealthConfig.setKingHealth(kingSpinner.getValue());
        PieceHealthConfig.setQueenHealth(queenSpinner.getValue());
        PieceHealthConfig.setRookHealth(rookSpinner.getValue());
        PieceHealthConfig.setBishopHealth(bishopSpinner.getValue());
        PieceHealthConfig.setKnightHealth(knightSpinner.getValue());
        PieceHealthConfig.setPawnHealth(pawnSpinner.getValue());
        
        // Sauvegarder dans le fichier CSV
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
