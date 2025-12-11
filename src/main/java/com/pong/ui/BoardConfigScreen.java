package com.pong.ui;

import com.pong.config.BoardConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Écran de configuration de la taille du terrain et du nombre de pièces
 */
public class BoardConfigScreen extends VBox {
    
    private ToggleGroup sizeGroup;
    private Button validateButton;
    private Button cancelButton;
    private Label previewLabel;
    
    public BoardConfigScreen(double width, double height) {
        super(20);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);
        setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");
        setPadding(new Insets(30));
        
        // Titre
        Text title = new Text("🏁 Configuration du Terrain");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setFill(Color.web("#f39c12"));
        
        Text subtitle = new Text("Choisissez le nombre de pions par camp");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitle.setFill(Color.web("#ecf0f1"));
        
        // Groupe de boutons radio
        sizeGroup = new ToggleGroup();
        VBox radioBox = new VBox(15);
        radioBox.setAlignment(Pos.CENTER_LEFT);
        radioBox.setPadding(new Insets(20));
        
        RadioButton size2 = createRadioOption("2 pions", 2, "Roi + Reine uniquement");
        RadioButton size4 = createRadioOption("4 pions", 4, "Roi + Reine + Fous");
        RadioButton size6 = createRadioOption("6 pions", 6, "Roi + Reine + Fous + Cavaliers");
        RadioButton size8 = createRadioOption("8 pions", 8, "Configuration complète avec Tours");
        
        sizeGroup.getToggles().addAll(size2, size4, size6, size8);
        
        // Sélectionne 8 par défaut
        size8.setSelected(true);
        
        radioBox.getChildren().addAll(size2, size4, size6, size8);
        
        // Label d'aperçu
        previewLabel = new Label();
        previewLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        previewLabel.setTextFill(Color.web("#3498db"));
        updatePreview(8);
        
        // Écoute les changements
        sizeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                int pawns = (int) newVal.getUserData();
                updatePreview(pawns);
            }
        });
        
        // Boutons
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        validateButton = createButton("Valider", "#27ae60", "#2ecc71");
        cancelButton = createButton("Annuler", "#e74c3c", "#c0392b");
        
        buttonBox.getChildren().addAll(validateButton, cancelButton);
        
        getChildren().addAll(title, subtitle, radioBox, previewLabel, buttonBox);
    }
    
    private RadioButton createRadioOption(String text, int pawns, String description) {
        RadioButton radio = new RadioButton(text + " - " + description);
        radio.setUserData(pawns);
        radio.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        radio.setTextFill(Color.web("#ecf0f1"));
        radio.setStyle("-fx-cursor: hand;");
        return radio;
    }
    
    private void updatePreview(int pawns) {
        int cols = pawns;
        int width = cols * 80;
        int height = 8 * 80;
        previewLabel.setText(String.format("📐 Terrain: %d colonnes × 8 lignes (%d × %d pixels)", 
            cols, width, height));
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
        RadioButton selected = (RadioButton) sizeGroup.getSelectedToggle();
        if (selected != null) {
            int pawns = (int) selected.getUserData();
            BoardConfig.setNumberOfPawns(pawns);
            
            // Sauvegarder dans le fichier CSV
            com.pong.config.ConfigLoader.saveBoardConfig();
        }
    }
    
    public Button getValidateButton() {
        return validateButton;
    }
    
    public Button getCancelButton() {
        return cancelButton;
    }
}
