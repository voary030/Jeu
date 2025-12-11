package com.pong.ui;

import com.pong.config.BoardConfig;
import com.pong.config.PieceHealthConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Panneau latéral avec bouton pause et informations de configuration
 */
public class SidePanel extends VBox {
    
    private Button pauseButton;
    private Button resumeButton;
    private Button boardConfigButton;
    private Button piecesConfigButton;
    private Button returnMenuButton;
    private Label configLabel;
    private Label piecesConfigLabel;
    
    // Palette moderne
    private static final String BG_PANEL = "#0f0f17";
    private static final String BG_SECTION = "#161622";
    private static final String ACCENT_ROSE = "#f43f5e";
    private static final String ACCENT_GREEN = "#10b981";
    private static final String ACCENT_VIOLET = "#8b5cf6";
    private static final String ACCENT_CYAN = "#06b6d4";
    private static final String TEXT_LIGHT = "#e2e8f0";
    private static final String TEXT_DIM = "#64748b";
    private static final String BORDER_COLOR = "#1e1e2e";
    
    public SidePanel() {
        super(12);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(20, 15, 20, 15));
        setStyle("-fx-background-color: " + BG_PANEL + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 0 2;");
        setPrefWidth(210);
        
        // Titre
        Label title = new Label("CONTRÔLES");
        title.setFont(Font.font("System", FontWeight.BLACK, 14));
        title.setTextFill(Color.web(TEXT_LIGHT));
        title.setPadding(new Insets(0, 0, 5, 0));
        
        // Bouton Pause
        pauseButton = createControlButton("Pause", ACCENT_ROSE, true);
        
        // Bouton Reprendre (initialement caché)
        resumeButton = createControlButton("Reprendre", ACCENT_GREEN, true);
        resumeButton.setVisible(false);
        resumeButton.setManaged(false);
        
        Separator sep1 = createStyledSeparator();
        
        // Section Configuration du terrain
        VBox configSection = createInfoSection("TERRAIN", configLabel = new Label());
        updateConfigInfo();
        
        Separator sep2 = createStyledSeparator();
        
        // Section Points de Vie
        VBox piecesSection = createInfoSection("POINTS DE VIE", piecesConfigLabel = new Label());
        updatePiecesConfigInfo();
        
        Separator sep3 = createStyledSeparator();
        
        // Section Réglages
        Label settingsTitle = new Label("RÉGLAGES");
        settingsTitle.setFont(Font.font("System", FontWeight.BOLD, 11));
        settingsTitle.setTextFill(Color.web(TEXT_DIM));
        
        boardConfigButton = createControlButton("Terrain", ACCENT_VIOLET, false);
        piecesConfigButton = createControlButton("Pièces", ACCENT_CYAN, false);
        
        Separator sep4 = createStyledSeparator();
        
        returnMenuButton = createControlButton("Menu Principal", "#64748b", false);
        
        getChildren().addAll(
            title,
            pauseButton,
            resumeButton,
            sep1,
            configSection,
            sep2,
            piecesSection,
            sep3,
            settingsTitle,
            boardConfigButton,
            piecesConfigButton,
            sep4,
            returnMenuButton
        );
    }
    
    private Separator createStyledSeparator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + BORDER_COLOR + ";");
        sep.setPadding(new Insets(5, 0, 5, 0));
        return sep;
    }
    
    private VBox createInfoSection(String title, Label contentLabel) {
        VBox section = new VBox(6);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-background-color: " + BG_SECTION + "; -fx-background-radius: 10;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        titleLabel.setTextFill(Color.web(TEXT_DIM));
        
        contentLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        contentLabel.setTextFill(Color.web(TEXT_LIGHT));
        contentLabel.setWrapText(true);
        contentLabel.setLineSpacing(2);
        
        section.getChildren().addAll(titleLabel, contentLabel);
        return section;
    }
    
    private Button createControlButton(String text, String color, boolean filled) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 12));
        button.setPrefWidth(175);
        button.setPrefHeight(36);
        
        String baseStyle, hoverStyle;
        
        if (filled) {
            baseStyle = "-fx-background-color: " + color + "; -fx-text-fill: white; "
                    + "-fx-background-radius: 8; -fx-cursor: hand;";
            hoverStyle = "-fx-background-color: derive(" + color + ", 15%); -fx-text-fill: white; "
                    + "-fx-background-radius: 8; -fx-cursor: hand;";
        } else {
            baseStyle = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_LIGHT + "; "
                    + "-fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: " + color + "; -fx-border-width: 1.5; -fx-border-radius: 6;";
            hoverStyle = "-fx-background-color: " + color + "22; -fx-text-fill: " + color + "; "
                    + "-fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: " + color + "; -fx-border-width: 1.5; -fx-border-radius: 6;";
        }
        
        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        
        return button;
    }
    
    private void updateConfigInfo() {
        int cols = BoardConfig.getNumberOfColumns();
        int width = BoardConfig.getBoardWidth();
        int height = BoardConfig.getBoardHeight();
        
        String pieces = "";
        if (BoardConfig.hasRooks()) pieces = "Complète";
        else if (BoardConfig.hasKnights()) pieces = "Sans Tours";
        else if (BoardConfig.hasBishops()) pieces = "Basique +";
        else pieces = "Basique";
        
        configLabel.setText(String.format(
            "Colonnes: %d\nTaille: %dx%d\nConfig: %s",
            cols, width, height, pieces
        ));
    }
    
    private void updatePiecesConfigInfo() {
        piecesConfigLabel.setText(String.format(
            "K: %d  |  Q: %d  |  R: %d\nB: %d  |  N: %d  |  P: %d",
            PieceHealthConfig.getKingHealth(),
            PieceHealthConfig.getQueenHealth(),
            PieceHealthConfig.getRookHealth(),
            PieceHealthConfig.getBishopHealth(),
            PieceHealthConfig.getKnightHealth(),
            PieceHealthConfig.getPawnHealth()
        ));
    }
    
    public Button getPauseButton() {
        return pauseButton;
    }
    
    public Button getResumeButton() {
        return resumeButton;
    }
    
    public void showPauseButton() {
        pauseButton.setVisible(true);
        pauseButton.setManaged(true);
        resumeButton.setVisible(false);
        resumeButton.setManaged(false);
    }
    
    public void showResumeButton() {
        pauseButton.setVisible(false);
        pauseButton.setManaged(false);
        resumeButton.setVisible(true);
        resumeButton.setManaged(true);
    }
    
    public void refreshConfig() {
        updateConfigInfo();
        updatePiecesConfigInfo();
    }
    
    public Button getBoardConfigButton() {
        return boardConfigButton;
    }
    
    public Button getPiecesConfigButton() {
        return piecesConfigButton;
    }
    
    public Button getReturnMenuButton() {
        return returnMenuButton;
    }
}
