package com.pong.ui;

import com.pong.network.GameServer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    
    public LANScreen(double width, double height) {
        super(20);
        setAlignment(Pos.CENTER);
        setPrefSize(width, height);
        setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");
        
        // Titre
        Text title = new Text("🌐 MULTIJOUEUR LAN 🌐");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setFill(Color.web("#1abc9c"));
        
        Text subtitle = new Text("Jouez sur le réseau local");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        subtitle.setFill(Color.web("#ecf0f1"));
        
        // Boutons principaux
        HBox mainButtons = new HBox(30);
        mainButtons.setAlignment(Pos.CENTER);
        
        hostButton = createButton("🖥️ Héberger", "#27ae60", "#2ecc71");
        joinButton = createButton("🔗 Rejoindre", "#3498db", "#5dade2");
        
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
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        statusLabel.setTextFill(Color.web("#f39c12"));
        
        // Bouton Retour
        backButton = createButton("← Retour", "#95a5a6", "#7f8c8d");
        backButton.setPrefWidth(200);
        
        // Actions des boutons
        hostButton.setOnAction(e -> showHostPanel());
        joinButton.setOnAction(e -> showJoinPanel());
        
        getChildren().addAll(title, subtitle, mainButtons, hostPanel, joinPanel, statusLabel, backButton);
        setPadding(new Insets(30));
    }
    
    /**
     * Crée le panel pour héberger une partie
     */
    private VBox createHostPanel() {
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 10;");
        panel.setMaxWidth(400);
        
        Label titleLabel = new Label("🖥️ Héberger une partie");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);
        
        // Afficher l'IP locale
        String localIP = GameServer.getLocalIPAddress();
        ipInfoLabel = new Label("📡 Votre IP: " + localIP);
        ipInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        ipInfoLabel.setTextFill(Color.web("#2ecc71"));
        
        // Port
        HBox portBox = new HBox(10);
        portBox.setAlignment(Pos.CENTER);
        Label portLabel = new Label("Port:");
        portLabel.setTextFill(Color.WHITE);
        portField = new TextField(String.valueOf(DEFAULT_PORT));
        portField.setPrefWidth(100);
        portField.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-border-color: #3498db;");
        portBox.getChildren().addAll(portLabel, portField);
        
        // Boutons de configuration
        HBox configButtons = new HBox(10);
        configButtons.setAlignment(Pos.CENTER);
        
        boardConfigButton = new Button("⚙️ Terrain");
        boardConfigButton.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        boardConfigButton.setPrefWidth(120);
        boardConfigButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        
        pieceHealthConfigButton = new Button("❤️ Vie des pièces");
        pieceHealthConfigButton.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        pieceHealthConfigButton.setPrefWidth(120);
        pieceHealthConfigButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-cursor: hand;");
        
        configButtons.getChildren().addAll(boardConfigButton, pieceHealthConfigButton);
        
        Label infoLabel = new Label("Communiquez votre IP et port à l'autre joueur");
        infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        infoLabel.setTextFill(Color.web("#bdc3c7"));
        
        startServerButton = createButton("🚀 Démarrer le serveur", "#e67e22", "#d35400");
        
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
        panel.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 10;");
        panel.setMaxWidth(400);
        
        Label titleLabel = new Label("🔗 Rejoindre une partie");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);
        
        // IP du serveur
        HBox ipBox = new HBox(10);
        ipBox.setAlignment(Pos.CENTER);
        Label ipLabel = new Label("IP:");
        ipLabel.setTextFill(Color.WHITE);
        ipField = new TextField("192.168.");
        ipField.setPrefWidth(150);
        ipField.setPromptText("ex: 192.168.1.10");
        ipField.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-border-color: #3498db;");
        ipBox.getChildren().addAll(ipLabel, ipField);
        
        // Port
        HBox portBox = new HBox(10);
        portBox.setAlignment(Pos.CENTER);
        Label portLabel = new Label("Port:");
        portLabel.setTextFill(Color.WHITE);
        TextField joinPortField = new TextField(String.valueOf(DEFAULT_PORT));
        joinPortField.setPrefWidth(100);
        joinPortField.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-border-color: #3498db;");
        portBox.getChildren().addAll(portLabel, joinPortField);
        
        // Stocker le port field pour le rejoindre
        portField = joinPortField;
        
        Label infoLabel = new Label("Entrez l'IP et le port de l'hôte");
        infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        infoLabel.setTextFill(Color.web("#bdc3c7"));
        
        connectButton = createButton("🔌 Se connecter", "#9b59b6", "#8e44ad");
        
        panel.getChildren().addAll(titleLabel, ipBox, portBox, infoLabel, connectButton);
        return panel;
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
     * Crée un bouton stylisé
     */
    private Button createButton(String text, String color, String hoverColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setPrefWidth(250);
        button.setPrefHeight(50);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
                + "-fx-background-radius: 10; -fx-cursor: hand;");
        
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; "
                    + "-fx-background-radius: 10; -fx-cursor: hand;"));
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
                    + "-fx-background-radius: 10; -fx-cursor: hand;"));
        
        return button;
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
