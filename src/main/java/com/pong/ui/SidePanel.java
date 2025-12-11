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
    
    public SidePanel() {
        super(15);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #34495e; -fx-border-color: #2c3e50; -fx-border-width: 0 2 0 0;");
        setPrefWidth(200);
        
        // Titre
        Label title = new Label("⚙️ CONTRÔLES");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#f39c12"));
        
        // Bouton Pause
        pauseButton = createButton("⏸ Pause", "#e74c3c");
        pauseButton.setPrefWidth(160);
        
        // Bouton Reprendre (initialement caché)
        resumeButton = createButton("▶ Reprendre", "#27ae60");
        resumeButton.setPrefWidth(160);
        resumeButton.setVisible(false);
        resumeButton.setManaged(false);
        
        Separator sep1 = new Separator();
        
        // Configuration du terrain
        Label configTitle = new Label("📐 Configuration");
        configTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        configTitle.setTextFill(Color.web("#ecf0f1"));
        
        configLabel = new Label();
        configLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        configLabel.setTextFill(Color.web("#bdc3c7"));
        configLabel.setWrapText(true);
        updateConfigInfo();
        
        Separator sep2 = new Separator();
        
        // Configuration des pièces
        Label piecesTitle = new Label("♟️ Points de Vie");
        piecesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        piecesTitle.setTextFill(Color.web("#ecf0f1"));
        
        piecesConfigLabel = new Label();
        piecesConfigLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        piecesConfigLabel.setTextFill(Color.web("#bdc3c7"));
        piecesConfigLabel.setWrapText(true);
        updatePiecesConfigInfo();
        
        Separator sep3 = new Separator();
        
        // Boutons de configuration
        Label configButtonsTitle = new Label("⚙️ Réglages");
        configButtonsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        configButtonsTitle.setTextFill(Color.web("#ecf0f1"));
        
        boardConfigButton = createButton("📐 Terrain", "#9b59b6");
        boardConfigButton.setPrefWidth(160);
        
        piecesConfigButton = createButton("♟️ Pièces", "#3498db");
        piecesConfigButton.setPrefWidth(160);
        
        Separator sep4 = new Separator();
        
        returnMenuButton = createButton("🏠 Menu Principal", "#e74c3c");
        returnMenuButton.setPrefWidth(160);
        
        getChildren().addAll(
            title,
            pauseButton,
            resumeButton,
            sep1,
            configTitle,
            configLabel,
            sep2,
            piecesTitle,
            piecesConfigLabel,
            sep3,
            configButtonsTitle,
            boardConfigButton,
            piecesConfigButton,
            sep4,
            returnMenuButton
        );
    }
    
    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
                + "-fx-background-radius: 5; -fx-cursor: hand;");
        
        // Couleurs de hover
        String hoverColor;
        if (color.equals("#e74c3c")) hoverColor = "#c0392b";
        else if (color.equals("#27ae60")) hoverColor = "#2ecc71";
        else if (color.equals("#9b59b6")) hoverColor = "#8e44ad";
        else if (color.equals("#3498db")) hoverColor = "#2980b9";
        else hoverColor = "#2c3e50";
        
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; "
                    + "-fx-background-radius: 5; -fx-cursor: hand;"));
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
                    + "-fx-background-radius: 5; -fx-cursor: hand;"));
        
        return button;
    }
    
    private void updateConfigInfo() {
        int cols = BoardConfig.getNumberOfColumns();
        int width = BoardConfig.getBoardWidth();
        int height = BoardConfig.getBoardHeight();
        
        String pieces = "";
        if (BoardConfig.hasRooks()) pieces = "Complète";
        else if (BoardConfig.hasKnights()) pieces = "Roi + Reine + Fous + Cavaliers";
        else if (BoardConfig.hasBishops()) pieces = "Roi + Reine + Fous";
        else pieces = "Roi + Reine";
        
        configLabel.setText(String.format(
            "Colonnes: %d\n" +
            "Dimension: %dx%d px\n" +
            "Pièces: %s",
            cols, width, height, pieces
        ));
    }
    
    private void updatePiecesConfigInfo() {
        piecesConfigLabel.setText(String.format(
            "♚ Roi: %d PV\n" +
            "♛ Reine: %d PV\n" +
            "♜ Tour: %d PV\n" +
            "♝ Fou: %d PV\n" +
            "♞ Cavalier: %d PV\n" +
            "♟ Pion: %d PV",
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
