package com.pong;

import com.pong.config.BoardConfig;
import com.pong.controllers.GameController;
import com.pong.controllers.NetworkGameController;
import com.pong.ui.BoardConfigScreen;
import com.pong.ui.ChessBoard;
import com.pong.ui.ConfigScreen;
import com.pong.ui.GameModeScreen;
import com.pong.ui.LANScreen;
import com.pong.ui.MenuScreen;
import com.pong.ui.PieceHealthConfigScreen;
import com.pong.ui.SidePanel;
import com.pong.ui.VictoryDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Jeu Pong avec échiquier - Les pièces ont des points de vie
 * Objectif: Détruire le roi adverse
 */
public class Main extends Application {
    private GameController gameController;
    private Label statusLabel;
    private Label instructionsLabel;
    private Label scoreLabel;
    private Stage primaryStage;
    private ChessBoard chessBoard;
    private BorderPane gameLayout;
    private SidePanel sidePanel;
    private boolean vsComputerMode = false;
    
    // État du LANScreen pour retourner au bon panneau après configuration
    private LANScreen currentLANScreen;
    private boolean isHostingMode = false;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Charger les configurations depuis les fichiers CSV
        com.pong.config.ConfigLoader.loadAllConfigurations();
        
        // Afficher le menu principal au démarrage
        showMenu();
    }
    
    /**
     * Crée le panneau d'informations du haut
     */
    private VBox createTopInfoPane() {
        VBox topPane = new VBox(5);
        topPane.setPadding(new Insets(15));
        topPane.setAlignment(Pos.CENTER);
        topPane.setStyle("-fx-background-color: #0f0f17;");
        
        Label titleLabel = new Label("CHESS STRIKER");
        titleLabel.setFont(Font.font("System", FontWeight.BLACK, 22));
        titleLabel.setTextFill(Color.web("#e2e8f0"));
        
        statusLabel = new Label("Partie en cours - Éliminez le Roi adverse");
        statusLabel.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        statusLabel.setTextFill(Color.web("#94a3b8"));
        
        scoreLabel = new Label("Score: 0 - 0");
        scoreLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        scoreLabel.setTextFill(Color.web("#06b6d4"));
        
        topPane.getChildren().addAll(titleLabel, scoreLabel, statusLabel);
        return topPane;
    }
    
    /**
     * Crée le panneau d'instructions du bas
     */
    private VBox createBottomInfoPane() {
        VBox bottomPane = new VBox(5);
        bottomPane.setPadding(new Insets(12));
        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.setStyle("-fx-background-color: #161622;");
        
        instructionsLabel = new Label("CLIC sur raquette = activer | Q/E = orienter | ENTRÉE = lancer");
        instructionsLabel.setFont(Font.font("System", FontWeight.MEDIUM, 12));
        instructionsLabel.setTextFill(Color.web("#e2e8f0"));
        
        Label infoLabel = new Label("Flèche verte = direction du tir  |  A/D et flèches = déplacer raquettes");
        infoLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        infoLabel.setTextFill(Color.web("#f59e0b"));
        
        bottomPane.getChildren().addAll(instructionsLabel, infoLabel);
        return bottomPane;
    }
    
    /**
     * Affiche le menu principal
     */
    private void showMenu() {
        MenuScreen menuScreen = new MenuScreen(640, 630);
        
        // Bouton Commencer - affiche la sélection du mode de jeu
        menuScreen.getStartButton().setOnAction(e -> showGameModeScreen());
        
        // Bouton Configuration Terrain
        menuScreen.getBoardConfigButton().setOnAction(e -> showBoardConfigScreen());
        
        // Bouton Configuration Pièces
        menuScreen.getConfigButton().setOnAction(e -> showConfigScreen());
        
        Scene scene = new Scene(menuScreen);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess Striker");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    /**
     * Affiche l'écran de configuration du terrain
     */
    private void showBoardConfigScreen() {
        BoardConfigScreen boardConfigScreen = new BoardConfigScreen(640, 630);
        
        // Bouton Valider
        boardConfigScreen.getValidateButton().setOnAction(e -> {
            boardConfigScreen.applyConfiguration();
            showMenu();
        });
        
        // Bouton Annuler
        boardConfigScreen.getCancelButton().setOnAction(e -> showMenu());
        
        Scene scene = new Scene(boardConfigScreen);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Échec Pong - Configuration Terrain");
    }
    
    /**
     * Affiche l'écran de configuration
     */
    private void showConfigScreen() {
        ConfigScreen configScreen = new ConfigScreen(640, 630);
        
        // Bouton Enregistrer
        configScreen.getSaveButton().setOnAction(e -> {
            configScreen.saveConfiguration();
            showMenu();
        });
        
        // Bouton Annuler
        configScreen.getCancelButton().setOnAction(e -> showMenu());
        
        Scene scene = new Scene(configScreen);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Échec Pong - Configuration");
    }
    
    /**
     * Affiche l'écran de sélection du mode de jeu
     */
    private void showGameModeScreen() {
        GameModeScreen gameModeScreen = new GameModeScreen(640, 630);
        
        // Bouton Multijoueur Local
        gameModeScreen.getMultiplayerButton().setOnAction(e -> {
            vsComputerMode = false;
            initializeAndStartGame();
        });
        
        // Bouton Multijoueur LAN
        gameModeScreen.getLanButton().setOnAction(e -> showLANScreen());
        
        // Bouton VS Computer
        gameModeScreen.getVsComputerButton().setOnAction(e -> {
            vsComputerMode = true;
            initializeAndStartGame();
        });
        
        // Bouton Retour
        gameModeScreen.getBackButton().setOnAction(e -> showMenu());
        
        Scene scene = new Scene(gameModeScreen);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Échec Pong - Mode de Jeu");
    }
    
    /**
     * Initialise et démarre une nouvelle partie
     */
    private void initializeAndStartGame() {
        // Recrée l'échiquier avec les nouvelles valeurs de santé
        chessBoard = new ChessBoard();
        
        // Création du contrôleur de jeu avec le mode sélectionné
        gameController = new GameController(chessBoard, vsComputerMode);
        
        // Configure le callback de victoire
        gameController.setOnVictoryCallback(this::onVictory);
        
        // Panneau de jeu avec l'échiquier - Utiliser Pane pour positionnement absolu
        javafx.scene.layout.Pane gamePane = new javafx.scene.layout.Pane();
        gamePane.setPrefSize(chessBoard.getBoardWidth(), chessBoard.getBoardHeight());
        
        // Ajoute d'abord l'échiquier (fond)
        gamePane.getChildren().add(chessBoard);
        
        // Puis ajoute les éléments de jeu par-dessus
        gamePane.getChildren().addAll(
            gameController.getTopPaddle(),
            gameController.getBottomPaddle(),
            gameController.getPuck(),
            gameController.getArrow()
        );
        
        // Centrer le terrain dans un conteneur
        StackPane centerPane = new StackPane(gamePane);
        centerPane.setStyle("-fx-background-color: #2c3e50;");
        centerPane.setAlignment(Pos.CENTER);
        
        // Créer le panneau latéral avec pause et configurations
        sidePanel = new SidePanel();
        sidePanel.getPauseButton().setOnAction(e -> {
            gameController.pause();
            sidePanel.showResumeButton();
        });
        sidePanel.getResumeButton().setOnAction(e -> {
            gameController.resume();
            sidePanel.showPauseButton();
            gameLayout.requestFocus();
        });
        
        // Boutons de configuration dans la sidebar
        sidePanel.getBoardConfigButton().setOnAction(e -> {
            gameController.pause();
            showBoardConfigScreenFromGame();
        });
        sidePanel.getPiecesConfigButton().setOnAction(e -> {
            gameController.pause();
            showConfigScreenFromGame();
        });
        
        // Bouton retour au menu principal
        sidePanel.getReturnMenuButton().setOnAction(e -> {
            gameController.stop();
            showMenu();
        });
        
        // Panel central avec terrain + panneau latéral
        HBox centerContent = new HBox();
        centerContent.getChildren().addAll(sidePanel, centerPane);
        HBox.setHgrow(centerPane, javafx.scene.layout.Priority.ALWAYS);
        
        // La flèche au centre
        gameController.getArrow().setLayoutX(chessBoard.getBoardWidth() / 2);
        gameController.getArrow().setLayoutY(chessBoard.getBoardHeight() / 2);
        gameController.getArrow().setVisible(false);
        
        // Gestion des clics sur les raquettes pour activer les joueurs
        gameController.getTopPaddle().setOnMouseClicked(e -> {
            gameController.activerJoueur(gameController.getJoueurBlanc());
        });
        
        gameController.getBottomPaddle().setOnMouseClicked(e -> {
            gameController.activerJoueur(gameController.getJoueurNoir());
        });
        
        // Panneau d'informations en haut
        VBox topPane = createTopInfoPane();
        
        // Panneau d'instructions en bas
        VBox bottomPane = createBottomInfoPane();
        
        // Layout principal
        gameLayout = new BorderPane();
        gameLayout.setStyle("-fx-background-color: #34495e;");
        gameLayout.setTop(topPane);
        gameLayout.setCenter(centerContent);
        gameLayout.setBottom(bottomPane);
        
        // Crée la scène avec largeur fixe de 840px (200 panneau + 640 max terrain)
        Scene scene = new Scene(gameLayout, 840, 790);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Échec Pong - Détruisez le Roi!");
        primaryStage.setResizable(false);
        
        // Gestion des événements clavier
        scene.setOnKeyPressed(event -> gameController.handleKeyPressed(event));
        scene.setOnKeyReleased(event -> gameController.handleKeyReleased(event));
        
        // Demande le focus à la scène
        gameLayout.requestFocus();
        gameLayout.setFocusTraversable(true);
        gamePane.setFocusTraversable(true);
        
        primaryStage.show();
        
        // Démarre le jeu
        gameController.start();
    }
    
    /**
     * Appelé quand un joueur gagne
     */
    private void onVictory() {
        javafx.application.Platform.runLater(() -> {
            // Met à jour le label de score
            scoreLabel.setText(gameController.getGameScore().getScoreText());
            
            // Affiche le dialog de victoire
            VictoryDialog dialog = new VictoryDialog();
            VictoryDialog.Action action = dialog.show(
                gameController.getWinner(), 
                gameController.getGameScore().getScoreText(),
                primaryStage
            );
            
            if (action == VictoryDialog.Action.CONTINUE) {
                // Continue la partie
                statusLabel.setText("Nouvelle partie - Bonne chance!");
                statusLabel.setTextFill(Color.web("#ecf0f1"));
                gameController.resetGame();
            } else {
                // Arrête la partie
                statusLabel.setText("🏆 PARTIE TERMINÉE - " + gameController.getWinner() + " est le grand gagnant! 🏆");
                statusLabel.setTextFill(Color.web("#e74c3c"));
                gameController.stop();
            }
        });
    }
    
    /**
     * Affiche l'écran de configuration du terrain depuis le jeu
     */
    private void showBoardConfigScreenFromGame() {
        BoardConfigScreen boardConfigScreen = new BoardConfigScreen(640, 630);
        
        // Bouton Valider
        boardConfigScreen.getValidateButton().setOnAction(e -> {
            boardConfigScreen.applyConfiguration();
            sidePanel.refreshConfig();
            // Retour au jeu - recréer le jeu avec la nouvelle config
            initializeAndStartGame();
        });
        
        // Bouton Annuler
        boardConfigScreen.getCancelButton().setOnAction(e -> {
            // Retour au jeu sans changement
            Scene scene = new Scene(gameLayout, 840, 790);
            primaryStage.setScene(scene);
            gameController.resume();
            sidePanel.showPauseButton();
            gameLayout.requestFocus();
        });
        
        Scene scene = new Scene(boardConfigScreen);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Échec Pong - Configuration Terrain");
    }
    
    /**
     * Affiche l'écran de configuration des pièces depuis le jeu
     */
    private void showConfigScreenFromGame() {
        ConfigScreen configScreen = new ConfigScreen(640, 630);
        
        // Bouton Enregistrer
        configScreen.getSaveButton().setOnAction(e -> {
            configScreen.saveConfiguration();
            sidePanel.refreshConfig();
            // Retour au jeu - recréer le jeu avec la nouvelle config
            initializeAndStartGame();
        });
        
        // Bouton Annuler
        configScreen.getCancelButton().setOnAction(e -> {
            // Retour au jeu sans changement
            Scene scene = new Scene(gameLayout, 840, 790);
            primaryStage.setScene(scene);
            gameController.resume();
            sidePanel.showPauseButton();
            gameLayout.requestFocus();
        });
        
        Scene scene = new Scene(configScreen);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Échec Pong - Configuration");
    }
    
    /**
     * Affiche l'écran de sélection du mode LAN
     */
    private void showLANScreen() {
        LANScreen lanScreen = new LANScreen(640, 630);
        currentLANScreen = lanScreen;
        
        // Intercepter les clics sur Héberger/Rejoindre pour mémoriser le mode
        lanScreen.getHostButton().setOnAction(e -> {
            isHostingMode = true;
            lanScreen.showHostPanel();
        });
        
        lanScreen.getJoinButton().setOnAction(e -> {
            isHostingMode = false;
            lanScreen.showJoinPanel();
        });
        
        // Bouton pour configurer le terrain
        lanScreen.getBoardConfigButton().setOnAction(e -> {
            showBoardConfigScreenFromLAN();
        });
        
        // Bouton pour configurer la vie des pièces
        lanScreen.getPieceHealthConfigButton().setOnAction(e -> {
            showPieceHealthConfigScreenFromLAN();
        });
        
        // Bouton pour héberger une partie
        lanScreen.getStartServerButton().setOnAction(e -> {
            int port = lanScreen.getPort();
            lanScreen.setStatus("⏳ Démarrage du serveur...", "#f39c12");
            startAsServer(port, lanScreen);
        });
        
        // Bouton pour rejoindre une partie
        lanScreen.getConnectButton().setOnAction(e -> {
            String ip = lanScreen.getServerIP();
            int port = lanScreen.getPort();
            lanScreen.setStatus("⏳ Connexion à " + ip + ":" + port + "...", "#f39c12");
            startAsClient(ip, port, lanScreen);
        });
        
        // Bouton Retour
        lanScreen.getBackButton().setOnAction(e -> showGameModeScreen());
        
        Scene scene = new Scene(lanScreen);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Échec Pong - Multijoueur LAN");
    }
    
    /**
     * Retourne au LANScreen en restaurant le panneau approprié
     */
    private void returnToLANScreen() {
        if (currentLANScreen != null) {
            // Recréer un nouveau LANScreen avec le même état
            showLANScreen();
            
            // Afficher le bon panneau (hébergement ou connexion)
            if (isHostingMode) {
                javafx.application.Platform.runLater(() -> {
                    currentLANScreen.showHostPanel();
                });
            } else {
                javafx.application.Platform.runLater(() -> {
                    currentLANScreen.showJoinPanel();
                });
            }
        } else {
            showLANScreen();
        }
    }
    
    /**
     * Démarre en tant que serveur (héberge la partie)
     */
    private void startAsServer(int port, LANScreen lanScreen) {
        chessBoard = new ChessBoard();
        NetworkGameController networkController = new NetworkGameController(chessBoard, port);
        
        networkController.setOnClientConnectedCallback(() -> {
            lanScreen.setStatus("✅ Joueur connecté! Démarrage...", "#2ecc71");
            // Petit délai puis démarrer le jeu
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {}
                initializeNetworkGame(networkController);
            });
        });
        
        networkController.setOnDisconnectedCallback(() -> {
            javafx.application.Platform.runLater(() -> {
                statusLabel.setText("❌ Connexion perdue!");
                statusLabel.setTextFill(Color.web("#e74c3c"));
            });
        });
        
        // Démarrer le serveur
        networkController.startNetwork();
        lanScreen.setStatus("📡 En attente d'un joueur sur le port " + port + "...", "#3498db");
    }
    
    /**
     * Démarre en tant que client (rejoint une partie)
     */
    private void startAsClient(String ip, int port, LANScreen lanScreen) {
        // Créer un NetworkGameController temporaire avec un échiquier vide
        // Le vrai échiquier sera créé après réception de la configuration
        ChessBoard tempChessBoard = new ChessBoard();
        NetworkGameController networkController = new NetworkGameController(tempChessBoard, ip, port);
        
        networkController.setOnDisconnectedCallback(() -> {
            javafx.application.Platform.runLater(() -> {
                statusLabel.setText("❌ Connexion perdue!");
                statusLabel.setTextFill(Color.web("#e74c3c"));
            });
        });
        
        // Quand la configuration est reçue, recréer l'échiquier et initialiser le jeu
        networkController.setOnConfigReceivedCallback(() -> {
            javafx.application.Platform.runLater(() -> {
                lanScreen.setStatus("✅ Configuration reçue! Démarrage...", "#2ecc71");
                // Recréer l'échiquier avec la bonne configuration
                chessBoard = new ChessBoard();
                networkController.updateChessBoard(chessBoard);
                initializeNetworkGame(networkController);
            });
        });
        
        // Se connecter au serveur
        new Thread(() -> {
            networkController.startNetwork();
            if (networkController.isConnected()) {
                javafx.application.Platform.runLater(() -> {
                    lanScreen.setStatus("⏳ En attente de la configuration...", "#f39c12");
                    // Le jeu sera initialisé quand onConfigReceivedCallback sera appelé
                });
            } else {
                javafx.application.Platform.runLater(() -> {
                    lanScreen.setStatus("❌ Impossible de se connecter", "#e74c3c");
                });
            }
        }).start();
    }
    
    /**
     * Initialise le jeu en mode réseau
     */
    private void initializeNetworkGame(NetworkGameController networkController) {
        // Panneau de jeu avec l'échiquier
        javafx.scene.layout.Pane gamePane = new javafx.scene.layout.Pane();
        gamePane.setPrefSize(chessBoard.getBoardWidth(), chessBoard.getBoardHeight());
        
        gamePane.getChildren().add(chessBoard);
        gamePane.getChildren().addAll(
            networkController.getTopPaddle(),
            networkController.getBottomPaddle(),
            networkController.getPuck(),
            networkController.getArrow()
        );
        
        // Positionner la flèche au centre (initialement invisible)
        networkController.getArrow().setLayoutX(chessBoard.getBoardWidth() / 2);
        networkController.getArrow().setLayoutY(chessBoard.getBoardHeight() / 2);
        networkController.getArrow().setVisible(false);
        
        StackPane centerPane = new StackPane(gamePane);
        centerPane.setStyle("-fx-background-color: #2c3e50;");
        centerPane.setAlignment(Pos.CENTER);
        
        // Panneau latéral simplifié pour le mode réseau
        sidePanel = new SidePanel();
        sidePanel.getPauseButton().setOnAction(e -> {
            networkController.pause();
            sidePanel.showResumeButton();
        });
        sidePanel.getResumeButton().setOnAction(e -> {
            networkController.resume();
            sidePanel.showPauseButton();
        });
        
        // Bouton retour au menu principal
        sidePanel.getReturnMenuButton().setOnAction(e -> {
            networkController.stop();
            showMenu();
        });
        
        // Callbacks pour synchroniser les boutons pause/resume
        networkController.setOnPauseCallback(() -> {
            javafx.application.Platform.runLater(() -> {
                sidePanel.showResumeButton();
            });
        });
        networkController.setOnResumeCallback(() -> {
            javafx.application.Platform.runLater(() -> {
                sidePanel.showPauseButton();
            });
        });
        
        // Callbacks pour synchroniser l'interface avec les pauses réseau
        networkController.setOnPauseCallback(() -> {
            javafx.application.Platform.runLater(() -> {
                sidePanel.showResumeButton();
                statusLabel.setText("⏸️ Jeu en pause (par l'autre joueur)");
            });
        });
        networkController.setOnResumeCallback(() -> {
            javafx.application.Platform.runLater(() -> {
                sidePanel.showPauseButton();
                statusLabel.setText("▶️ Jeu repris");
            });
        });
        
        HBox centerContent = new HBox();
        centerContent.getChildren().addAll(sidePanel, centerPane);
        HBox.setHgrow(centerPane, javafx.scene.layout.Priority.ALWAYS);
        
        // Gestionnaire de clic sur la raquette locale pour lancer
        if (networkController.isServer()) {
            // Serveur = raquette du bas
            networkController.getBottomPaddle().setOnMouseClicked(e -> {
                networkController.activerJoueurLocal();
            });
        } else {
            // Client = raquette du haut
            networkController.getTopPaddle().setOnMouseClicked(e -> {
                networkController.activerJoueurLocal();
            });
        }
        
        gameLayout = new BorderPane();
        gameLayout.setTop(createTopInfoPane());
        gameLayout.setCenter(centerContent);
        gameLayout.setBottom(createBottomInfoPane());
        
        // Afficher le mode de jeu
        String modeText = networkController.isServer() ? "🖥️ HÔTE" : "🔗 CLIENT";
        statusLabel.setText("Mode LAN " + modeText + " - IP: " + networkController.getServerIP());
        
        Scene scene = new Scene(gameLayout, 840, 790);
        scene.setOnKeyPressed(event -> networkController.handleKeyPressed(event));
        scene.setOnKeyReleased(event -> networkController.handleKeyReleased(event));
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Échec Pong - LAN " + modeText);
        
        // S'assurer que le gameLayout a le focus pour recevoir les événements clavier
        Platform.runLater(() -> {
            gameLayout.requestFocus();
        });
        
        primaryStage.show();
    }
    
    /**
     * Affiche l'écran de configuration du terrain depuis le LANScreen
     */
    private void showBoardConfigScreenFromLAN() {
        BoardConfigScreen boardConfigScreen = new BoardConfigScreen(640, 630);
        
        // Bouton Valider
        boardConfigScreen.getValidateButton().setOnAction(e -> {
            boardConfigScreen.applyConfiguration();
            returnToLANScreen();
        });
        
        // Bouton Annuler
        boardConfigScreen.getCancelButton().setOnAction(e -> {
            returnToLANScreen();
        });
        
        Scene scene = new Scene(boardConfigScreen);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Échec Pong - Configuration Terrain");
    }
    
    /**
     * Affiche l'écran de configuration de la vie des pièces depuis le LANScreen
     */
    private void showPieceHealthConfigScreenFromLAN() {
        PieceHealthConfigScreen configScreen = new PieceHealthConfigScreen(640, 630);
        
        // Bouton Valider
        configScreen.getValidateButton().setOnAction(e -> {
            configScreen.applyConfiguration();
            returnToLANScreen();
        });
        
        // Bouton Annuler
        configScreen.getCancelButton().setOnAction(e -> {
            returnToLANScreen();
        });
        
        Scene scene = new Scene(configScreen);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Échec Pong - Configuration Vie des Pièces");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
