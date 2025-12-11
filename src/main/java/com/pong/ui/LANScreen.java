package com.pong.ui;

import com.pong.network.GameServer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Écran de sélection du mode multijoueur LAN
 */
public class LANScreen extends VBox {
    
    private Button hostButton;
    private Button joinButton;
    private Button backButton;
    private Button startServerButton;
    private Button connectButton;
    private Button boardConfigButton;
    private Button pieceHealthConfigButton;
    
    private TextField ipField;
    private TextField portField;
    
    private Label statusLabel;
    private Label ipInfoLabel;
    
    private VBox hostPanel;
    private VBox joinPanel;
    
    private static final int DEFAULT_PORT = 5555;
    
    // Palette moderne
    private static final String BG_DARK = "#0a0a0f";
    private static final String BG_CARD = "#14141f";
    private static final String BG_PANEL = "#1a1a2e";
    private static final String ACCENT_TEAL = "#14b8a6";
    private static final String ACCENT_VIOLET = "#8b5cf6";
    private static final String ACCENT_AMBER = "#f59e0b";
    private static final String ACCENT_ROSE = "#f43f5e";
    private static final String TEXT_LIGHT = "#e2e8f0";
    private static final String TEXT_DIM = "#94a3b8";
    
    public LANScreen(double width, double height) {
        super(0);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);
        setStyle("-fx-background-color: " + BG_DARK + ";");
        
        // Conteneur principal
        VBox mainCard = new VBox(20);
        mainCard.setAlignment(Pos.CENTER);
        mainCard.setPadding(new Insets(35, 45, 35, 45));
        mainCard.setMaxWidth(520);
        mainCard.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 20;");
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(ACCENT_TEAL, 0.25));
        shadow.setRadius(35);
        mainCard.setEffect(shadow);
        
        // En-tête
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        
        Text title = new Text("Partie en Réseau");
        title.setFont(Font.font("System", FontWeight.BOLD, 30));
        title.setFill(Color.web(TEXT_LIGHT));
        
        Text subtitle = new Text("Jouez avec un ami sur le réseau local");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 13));
        subtitle.setFill(Color.web(TEXT_DIM));
        
        header.getChildren().addAll(title, subtitle);
        
        // Boutons principaux en ligne
        HBox mainButtons = new HBox(20);
        mainButtons.setAlignment(Pos.CENTER);
        mainButtons.setPadding(new Insets(15, 0, 15, 0));
        
        hostButton = createModeButton("Héberger", "Créer une partie", ACCENT_TEAL);
        joinButton = createModeButton("Rejoindre", "Se connecter", ACCENT_VIOLET);
        
        mainButtons.getChildren().addAll(hostButton, joinButton);
        
        // Panel pour héberger
        hostPanel = createHostPanel();
        hostPanel.setVisible(false);
        hostPanel.setManaged(false);
        
        // Panel pour rejoindre
        joinPanel = createJoinPanel();
        joinPanel.setVisible(false);
        joinPanel.setManaged(false);
        
        // Status label
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        statusLabel.setTextFill(Color.web(ACCENT_AMBER));
        statusLabel.setPadding(new Insets(8));
        
        // Bouton Retour
        backButton = createBackButton();
        
        // Actions des boutons
        hostButton.setOnAction(e -> showHostPanel());
        joinButton.setOnAction(e -> showJoinPanel());
        
        mainCard.getChildren().addAll(header, mainButtons, hostPanel, joinPanel, statusLabel, backButton);
        
        getChildren().add(mainCard);
        setPadding(new Insets(30));
    }
    
    private Button createModeButton(String title, String subtitle, String color) {
        Button btn = new Button(title + "\n" + subtitle);
        btn.setFont(Font.font("System", FontWeight.BOLD, 14));
        btn.setPrefWidth(180);
        btn.setPrefHeight(70);
        btn.setWrapText(true);
        btn.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        String baseStyle = "-fx-background-color: " + BG_PANEL + "; -fx-text-fill: " + TEXT_LIGHT + "; "
                + "-fx-background-radius: 14; -fx-cursor: hand; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 12;";
        String hoverStyle = "-fx-background-color: " + color + "22; -fx-text-fill: " + color + "; "
                + "-fx-background-radius: 14; -fx-cursor: hand; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 12;";
        
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        
        return btn;
    }
    
    /**
     * Crée le panel pour héberger une partie
     */
    private VBox createHostPanel() {
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: " + BG_PANEL + "; -fx-background-radius: 14;");
        panel.setMaxWidth(420);
        
        Label titleLabel = new Label("Héberger une partie");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web(TEXT_LIGHT));
        
        // Afficher l'IP locale
        String localIP = GameServer.getLocalIPAddress();
        ipInfoLabel = new Label("Votre IP : " + localIP);
        ipInfoLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        ipInfoLabel.setTextFill(Color.web(ACCENT_TEAL));
        ipInfoLabel.setPadding(new Insets(8, 15, 8, 15));
        ipInfoLabel.setStyle("-fx-background-color: " + ACCENT_TEAL + "22; -fx-background-radius: 8;");
        
        // Port
        HBox portBox = new HBox(12);
        portBox.setAlignment(Pos.CENTER);
        Label portLabel = new Label("Port :");
        portLabel.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        portLabel.setTextFill(Color.web(TEXT_LIGHT));
        portField = new TextField(String.valueOf(DEFAULT_PORT));
        portField.setPrefWidth(90);
        portField.setStyle("-fx-background-color: #252538; -fx-text-fill: white; -fx-border-color: #3d3d5c; -fx-border-radius: 6; -fx-background-radius: 6;");
        portBox.getChildren().addAll(portLabel, portField);
        
        // Boutons de configuration
        HBox configButtons = new HBox(12);
        configButtons.setAlignment(Pos.CENTER);
        
        boardConfigButton = createSmallButton("Terrain", ACCENT_VIOLET);
        pieceHealthConfigButton = createSmallButton("Pièces", ACCENT_ROSE);
        
        configButtons.getChildren().addAll(boardConfigButton, pieceHealthConfigButton);
        
        Label infoLabel = new Label("Communiquez votre IP et port à l'autre joueur");
        infoLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        infoLabel.setTextFill(Color.web(TEXT_DIM));
        
        startServerButton = createActionButton("Démarrer le serveur", ACCENT_AMBER);
        
        panel.getChildren().addAll(titleLabel, ipInfoLabel, portBox, configButtons, infoLabel, startServerButton);
        return panel;
    }
    
    /**
     * Crée le panel pour rejoindre une partie
     */
    private VBox createJoinPanel() {
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: " + BG_PANEL + "; -fx-background-radius: 14;");
        panel.setMaxWidth(420);
        
        Label titleLabel = new Label("Rejoindre une partie");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web(TEXT_LIGHT));
        
        // IP du serveur
        HBox ipBox = new HBox(12);
        ipBox.setAlignment(Pos.CENTER);
        Label ipLabel = new Label("IP Hôte :");
        ipLabel.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        ipLabel.setTextFill(Color.web(TEXT_LIGHT));
        ipField = new TextField("192.168.");
        ipField.setPrefWidth(150);
        ipField.setPromptText("ex: 192.168.1.10");
        ipField.setStyle("-fx-background-color: #252538; -fx-text-fill: white; -fx-border-color: #3d3d5c; -fx-border-radius: 6; -fx-background-radius: 6;");
        ipBox.getChildren().addAll(ipLabel, ipField);
        
        // Port
        HBox portBox = new HBox(12);
        portBox.setAlignment(Pos.CENTER);
        Label portLabel = new Label("Port :");
        portLabel.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        portLabel.setTextFill(Color.web(TEXT_LIGHT));
        TextField joinPortField = new TextField(String.valueOf(DEFAULT_PORT));
        joinPortField.setPrefWidth(90);
        joinPortField.setStyle("-fx-background-color: #252538; -fx-text-fill: white; -fx-border-color: #3d3d5c; -fx-border-radius: 6; -fx-background-radius: 6;");
        portBox.getChildren().addAll(portLabel, joinPortField);
        
        // Stocker le port field pour le rejoindre
        portField = joinPortField;
        
        Label infoLabel = new Label("Entrez l'IP et le port de l'hôte");
        infoLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        infoLabel.setTextFill(Color.web(TEXT_DIM));
        
        connectButton = createActionButton("Se connecter", ACCENT_VIOLET);
        
        panel.getChildren().addAll(titleLabel, ipBox, portBox, infoLabel, connectButton);
        return panel;
    }
    
    private Button createSmallButton(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("System", FontWeight.MEDIUM, 12));
        btn.setPrefWidth(100);
        btn.setPrefHeight(32);
        
        String baseStyle = "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: derive(" + color + ", 15%); -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;";
        
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        
        return btn;
    }
    
    private Button createActionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("System", FontWeight.BOLD, 14));
        btn.setPrefWidth(200);
        btn.setPrefHeight(45);
        
        String baseStyle = "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: derive(" + color + ", 15%); -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;";
        
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        
        return btn;
    }
    
    private Button createBackButton() {
        Button btn = new Button("Retour");
        btn.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        btn.setPrefWidth(140);
        btn.setPrefHeight(38);
        
        String baseStyle = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_DIM + "; "
                + "-fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: " + TEXT_DIM + "; -fx-border-width: 1; -fx-border-radius: 8;";
        String hoverStyle = "-fx-background-color: #1e293b; -fx-text-fill: " + TEXT_LIGHT + "; "
                + "-fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: " + TEXT_LIGHT + "; -fx-border-width: 1; -fx-border-radius: 8;";
        
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        
        return btn;
    }
    
    /**
     * Affiche le panel pour héberger
     */
    public void showHostPanel() {
        hostPanel.setVisible(true);
        hostPanel.setManaged(true);
        joinPanel.setVisible(false);
        joinPanel.setManaged(false);
        hostButton.setDisable(true);
        joinButton.setDisable(false);
    }
    
    /**
     * Affiche le panel pour rejoindre
     */
    public void showJoinPanel() {
        joinPanel.setVisible(true);
        joinPanel.setManaged(true);
        hostPanel.setVisible(false);
        hostPanel.setManaged(false);
        joinButton.setDisable(true);
        hostButton.setDisable(false);
    }
    
    /**
     * Met à jour le label de status
     */
    public void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    /**
     * Met à jour le label de status avec une couleur
     */
    public void setStatus(String status, String color) {
        statusLabel.setText(status);
        statusLabel.setTextFill(Color.web(color));
    }
    
    // Getters
    public Button getHostButton() { return hostButton; }
    public Button getJoinButton() { return joinButton; }
    public Button getBackButton() { return backButton; }
    public Button getStartServerButton() { return startServerButton; }
    public Button getConnectButton() { return connectButton; }
    public Button getBoardConfigButton() { return boardConfigButton; }
    public Button getPieceHealthConfigButton() { return pieceHealthConfigButton; }
    
    public String getServerIP() { return ipField.getText().trim(); }
    public int getPort() {
        try {
            return Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            return DEFAULT_PORT;
        }
    }
}
