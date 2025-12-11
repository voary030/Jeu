package com.pong.ui;

import com.pong.config.BoardConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
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
    
    // Palette moderne
    private static final String BG_DARK = "#0a0a0f";
    private static final String BG_CARD = "#14141f";
    private static final String ACCENT_CYAN = "#06b6d4";
    private static final String ACCENT_GREEN = "#10b981";
    private static final String TEXT_LIGHT = "#e2e8f0";
    private static final String TEXT_DIM = "#94a3b8";
    
    public BoardConfigScreen(double width, double height) {
        super(0);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);
        setStyle("-fx-background-color: " + BG_DARK + ";");
        setPadding(new Insets(30));
        
        // Carte principale
        VBox mainCard = new VBox(25);
        mainCard.setAlignment(Pos.CENTER);
        mainCard.setPadding(new Insets(35, 50, 35, 50));
        mainCard.setMaxWidth(500);
        mainCard.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 20;");
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(ACCENT_CYAN, 0.25));
        shadow.setRadius(35);
        mainCard.setEffect(shadow);
        
        // En-tête
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        
        Text title = new Text("Configuration du Terrain");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_LIGHT));
        
        Text subtitle = new Text("Sélectionnez le nombre de pièces par camp");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 13));
        subtitle.setFill(Color.web(TEXT_DIM));
        
        header.getChildren().addAll(title, subtitle);
        
        // Groupe de toggles en ligne
        sizeGroup = new ToggleGroup();
        HBox togglesRow = new HBox(12);
        togglesRow.setAlignment(Pos.CENTER);
        togglesRow.setPadding(new Insets(15, 0, 15, 0));
        
        ToggleButton size2 = createToggleOption("2", 2);
        ToggleButton size4 = createToggleOption("4", 4);
        ToggleButton size6 = createToggleOption("6", 6);
        ToggleButton size8 = createToggleOption("8", 8);
        
        size2.setToggleGroup(sizeGroup);
        size4.setToggleGroup(sizeGroup);
        size6.setToggleGroup(sizeGroup);
        size8.setToggleGroup(sizeGroup);
        
        size8.setSelected(true);
        
        togglesRow.getChildren().addAll(size2, size4, size6, size8);
        
        // Descriptions
        VBox descriptions = new VBox(8);
        descriptions.setAlignment(Pos.CENTER);
        descriptions.setPadding(new Insets(10));
        descriptions.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 12;");
        
        Label desc2 = createDescLabel("2 pièces : Roi + Reine uniquement");
        Label desc4 = createDescLabel("4 pièces : Roi + Reine + Fous");
        Label desc6 = createDescLabel("6 pièces : Roi + Reine + Fous + Cavaliers");
        Label desc8 = createDescLabel("8 pièces : Configuration complète avec Tours");
        
        descriptions.getChildren().addAll(desc2, desc4, desc6, desc8);
        
        // Aperçu
        previewLabel = new Label();
        previewLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        previewLabel.setTextFill(Color.web(ACCENT_CYAN));
        previewLabel.setPadding(new Insets(12));
        previewLabel.setStyle("-fx-background-color: " + ACCENT_CYAN + "15; -fx-background-radius: 8;");
        updatePreview(8);
        
        // Écoute les changements
        sizeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                int pawns = (int) newVal.getUserData();
                updatePreview(pawns);
            }
        });
        
        // Boutons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        validateButton = createStyledButton("Valider", ACCENT_GREEN, true);
        cancelButton = createStyledButton("Annuler", "#64748b", false);
        
        buttonBox.getChildren().addAll(validateButton, cancelButton);
        
        mainCard.getChildren().addAll(header, togglesRow, descriptions, previewLabel, buttonBox);
        getChildren().add(mainCard);
    }
    
    private ToggleButton createToggleOption(String text, int pawns) {
        ToggleButton toggle = new ToggleButton(text);
        toggle.setUserData(pawns);
        toggle.setFont(Font.font("System", FontWeight.BOLD, 16));
        toggle.setPrefWidth(70);
        toggle.setPrefHeight(50);
        
        String baseStyle = "-fx-background-color: #1e1e2e; -fx-text-fill: " + TEXT_LIGHT + "; "
                + "-fx-background-radius: 12; -fx-cursor: hand; -fx-border-color: #2d2d42; -fx-border-width: 2; -fx-border-radius: 10;";
        String selectedStyle = "-fx-background-color: " + ACCENT_CYAN + "; -fx-text-fill: white; "
                + "-fx-background-radius: 12; -fx-cursor: hand; -fx-border-color: " + ACCENT_CYAN + "; -fx-border-width: 2; -fx-border-radius: 10;";
        
        toggle.setStyle(baseStyle);
        
        toggle.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            toggle.setStyle(isSelected ? selectedStyle : baseStyle);
        });
        
        toggle.setOnMouseEntered(e -> {
            if (!toggle.isSelected()) {
                toggle.setStyle("-fx-background-color: #252538; -fx-text-fill: " + ACCENT_CYAN + "; "
                        + "-fx-background-radius: 12; -fx-cursor: hand; -fx-border-color: " + ACCENT_CYAN + "; -fx-border-width: 2; -fx-border-radius: 10;");
            }
        });
        toggle.setOnMouseExited(e -> {
            if (!toggle.isSelected()) {
                toggle.setStyle(baseStyle);
            }
        });
        
        return toggle;
    }
    
    private Label createDescLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.NORMAL, 12));
        label.setTextFill(Color.web(TEXT_DIM));
        return label;
    }
    
    private void updatePreview(int pawns) {
        int cols = pawns;
        int width = cols * 80;
        int height = 8 * 80;
        previewLabel.setText(String.format("Terrain : %d colonnes × 8 lignes  |  %d × %d pixels", 
            cols, width, height));
    }
    
    private Button createStyledButton(String text, String color, boolean filled) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 14));
        button.setPrefWidth(130);
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
    
    public void applyConfiguration() {
        ToggleButton selected = (ToggleButton) sizeGroup.getSelectedToggle();
        if (selected != null) {
            int pawns = (int) selected.getUserData();
            BoardConfig.setNumberOfPawns(pawns);
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
