package com.pong.controllers;

import com.pong.config.GameConfig;
import com.pong.models.*;
import com.pong.network.GameClient;
import com.pong.network.GameServer;
import com.pong.network.NetworkMessageListener;
import com.pong.ui.Arrow;
import com.pong.ui.ChessBoard;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.List;

/**
 * Contrôleur de jeu pour le mode multijoueur en réseau local (LAN)
 */
public class NetworkGameController implements NetworkMessageListener {
    private Puck puck;
    private Paddle topPaddle;
    private Paddle bottomPaddle;
    private ChessBoard chessBoard;
    
    // Système de joueurs
    private Joueur joueurLocal;
    private Joueur joueurDistant;
    
    // Flèche directionnelle
    private Arrow arrow;
    private boolean ballLaunched = false;
    private boolean arrowAnimating = false;
    private double arrowAngleDirection = 1; // 1 pour augmenter, -1 pour diminuer
    private Joueur joueurActif = null; // Le joueur qui prépare le tir
    
    private double WIDTH;
    private double HEIGHT;
    private AnimationTimer gameLoop;
    private boolean gameOver = false;
    private boolean isPaused = false;
    private String winner = "";
    private GameScore gameScore;
    private Runnable onVictoryCallback;
    private Runnable onClientConnectedCallback;
    private Runnable onDisconnectedCallback;
    private Runnable onPauseCallback;
    private Runnable onResumeCallback;
    private Runnable onConfigReceivedCallback;
    
    // Réseau
    private boolean isServer;
    private GameServer server;
    private GameClient client;
    private Paddle localPaddle;
    private Paddle remotePaddle;
    
    // État de connexion
    private boolean isConnected = false;
    
    // Optimisation réseau - limiter le taux d'envoi
    private long lastNetworkUpdate = 0;
    private static final long NETWORK_UPDATE_INTERVAL_MS = 16; // ~60 FPS
    
    /**
     * Constructeur pour le mode serveur (héberge la partie)
     */
    public NetworkGameController(ChessBoard chessBoard, int port) {
        this.isServer = true;
        initializeGame(chessBoard);
        
        // Le serveur contrôle la raquette du bas
        localPaddle = bottomPaddle;
        remotePaddle = topPaddle;
        joueurLocal = new Joueur(GameConfig.getPlayer1Name(), Joueur.Cote.BAS);
        joueurDistant = new Joueur("Adversaire", Joueur.Cote.HAUT);
        
        // Créer le serveur
        server = new GameServer(port);
        server.setMessageListener(this);
    }
    
    /**
     * Constructeur pour le mode client (rejoint une partie)
     */
    public NetworkGameController(ChessBoard chessBoard, String serverIP, int port) {
        this.isServer = false;
        initializeGame(chessBoard);
        
        // Le client contrôle la raquette du haut
        localPaddle = topPaddle;
        remotePaddle = bottomPaddle;
        joueurLocal = new Joueur(GameConfig.getPlayer1Name(), Joueur.Cote.HAUT);
        joueurDistant = new Joueur("Hôte", Joueur.Cote.BAS);
        
        // Créer le client
        client = new GameClient(serverIP, port);
        client.setMessageListener(this);
    }
    
    /**
     * Initialise les éléments communs du jeu
     */
    private void initializeGame(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
        this.WIDTH = chessBoard.getBoardWidth();
        this.HEIGHT = chessBoard.getBoardHeight();
        this.gameScore = new GameScore(GameConfig.getPlayer1Name(), "Adversaire");
        
        // Crée la balle au centre
        puck = new Puck(WIDTH, HEIGHT);
        
        // Raquette du haut
        topPaddle = new Paddle(
            WIDTH / 2 - 35, 190, 70, 12,
            WIDTH, HEIGHT, true
        );
        
        // Raquette du bas
        bottomPaddle = new Paddle(
            WIDTH / 2 - 35, 380, 70, 12,
            WIDTH, HEIGHT, false
        );
        
        arrow = new Arrow();
    }
    
    /**
     * Démarre la connexion réseau
     */
    public void startNetwork() {
        if (isServer) {
            try {
                server.start();
            } catch (Exception e) {
                System.err.println("❌ Erreur démarrage serveur: " + e.getMessage());
            }
        } else {
            if (client.connect()) {
                isConnected = true;
                // Démarrer le jeu côté client
                Platform.runLater(this::start);
            }
        }
    }
    
    /**
     * Réception des messages réseau
     */
    @Override
    public void onMessage(String message) {
        Platform.runLater(() -> {
            if (message.equals("CLIENT_CONNECTED")) {
                isConnected = true;
                System.out.println("✅ Joueur connecté!");
                if (onClientConnectedCallback != null) {
                    onClientConnectedCallback.run();
                }
                // Le serveur envoie la configuration au client
                if (isServer) {
                    sendConfiguration();
                }
                start(); // Démarrer le jeu
            } else if (message.equals("DISCONNECTED")) {
                isConnected = false;
                isPaused = true;
                if (onDisconnectedCallback != null) {
                    onDisconnectedCallback.run();
                }
            } else if (message.startsWith("STATE|")) {
                // Client reçoit l'état du jeu du serveur
                if (!isServer) {
                    parseGameState(message);
                }
            } else if (message.startsWith("PADDLE|")) {
                // Serveur reçoit la position de la raquette du client
                if (isServer) {
                    parsePaddlePosition(message);
                }
            } else if (message.startsWith("INPUT|")) {
                // Traiter les inputs distants
                parseInput(message);
            } else if (message.startsWith("LAUNCH|")) {
                // Lancement de la balle
                parseLaunch(message);
            } else if (message.startsWith("ACTIVATE|")) {
                // L'autre joueur a activé son joueur
                parseActivation(message);
            } else if (message.equals("PAUSE")) {
                // L'autre joueur a mis le jeu en pause
                isPaused = true;
                System.out.println("⏸️ Jeu mis en pause par l'autre joueur");
                if (onPauseCallback != null) {
                    onPauseCallback.run();
                }
            } else if (message.equals("RESUME")) {
                // L'autre joueur a repris le jeu
                isPaused = false;
                System.out.println("▶️ Jeu repris par l'autre joueur");
                if (onResumeCallback != null) {
                    onResumeCallback.run();
                }
            } else if (message.startsWith("CONFIG|")) {
                // Client reçoit la configuration du serveur
                if (!isServer) {
                    parseConfiguration(message);
                }
            }
        });
    }
    
    /**
     * Envoie la configuration du jeu au client (serveur uniquement)
     */
    private void sendConfiguration() {
        if (server != null && server.isClientConnected()) {
            // Format: CONFIG|numberOfPawns|kingHP|queenHP|rookHP|bishopHP|knightHP|pawnHP
            String config = String.format(java.util.Locale.US, "CONFIG|%d|%d|%d|%d|%d|%d|%d",
                com.pong.config.BoardConfig.getNumberOfPawns(),
                com.pong.config.PieceHealthConfig.getKingHealth(),
                com.pong.config.PieceHealthConfig.getQueenHealth(),
                com.pong.config.PieceHealthConfig.getRookHealth(),
                com.pong.config.PieceHealthConfig.getBishopHealth(),
                com.pong.config.PieceHealthConfig.getKnightHealth(),
                com.pong.config.PieceHealthConfig.getPawnHealth());
            server.sendMessage(config);
            System.out.println("📤 Configuration envoyée au client");
        }
    }
    
    /**
     * Parse la configuration reçue du serveur (client uniquement)
     */
    private void parseConfiguration(String message) {
        try {
            String[] parts = message.split("\\|");
            if (parts.length >= 8) {
                int numberOfPawns = Integer.parseInt(parts[1]);
                int kingHP = Integer.parseInt(parts[2]);
                int queenHP = Integer.parseInt(parts[3]);
                int rookHP = Integer.parseInt(parts[4]);
                int bishopHP = Integer.parseInt(parts[5]);
                int knightHP = Integer.parseInt(parts[6]);
                int pawnHP = Integer.parseInt(parts[7]);
                
                // Appliquer la configuration du terrain
                com.pong.config.BoardConfig.setNumberOfPawns(numberOfPawns);
                
                // Appliquer la configuration des HP
                com.pong.config.PieceHealthConfig.setKingHealth(kingHP);
                com.pong.config.PieceHealthConfig.setQueenHealth(queenHP);
                com.pong.config.PieceHealthConfig.setRookHealth(rookHP);
                com.pong.config.PieceHealthConfig.setBishopHealth(bishopHP);
                com.pong.config.PieceHealthConfig.setKnightHealth(knightHP);
                com.pong.config.PieceHealthConfig.setPawnHealth(pawnHP);
                
                System.out.println("📥 Configuration reçue - Pions: " + numberOfPawns);
                System.out.println("   HP: King=" + kingHP + ", Queen=" + queenHP + ", Rook=" + rookHP + 
                                   ", Bishop=" + bishopHP + ", Knight=" + knightHP + ", Pawn=" + pawnHP);
                
                // Notifier que la config a été reçue pour recréer le jeu
                if (onConfigReceivedCallback != null) {
                    onConfigReceivedCallback.run();
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Erreur parsing config: " + e.getMessage());
        }
    }
    
    /**
     * Parse l'état du jeu reçu (côté client)
     */
    private void parseGameState(String message) {
        try {
            String[] parts = message.split("\\|");
            if (parts.length >= 9) {
                // Position de la balle - correction douce pour éviter les saccades
                double serverX = Double.parseDouble(parts[1].replace(',', '.'));
                double serverY = Double.parseDouble(parts[2].replace(',', '.'));
                double serverVx = Double.parseDouble(parts[3].replace(',', '.'));
                double serverVy = Double.parseDouble(parts[4].replace(',', '.'));
                
                // Calculer l'écart entre la position locale et celle du serveur
                double dx = serverX - puck.getCenterX();
                double dy = serverY - puck.getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                // Si l'écart est trop grand (> 20 pixels), corriger immédiatement
                // Sinon, corriger progressivement pour un mouvement fluide
                if (distance > 20) {
                    // Téléportation pour grande divergence
                    puck.setCenterX(serverX);
                    puck.setCenterY(serverY);
                    puck.setVelocity(serverVx, serverVy);
                } else if (distance > 2) {
                    // Correction douce pour petites divergences
                    double correctionFactor = 0.3; // 30% de correction par frame
                    puck.setCenterX(puck.getCenterX() + dx * correctionFactor);
                    puck.setCenterY(puck.getCenterY() + dy * correctionFactor);
                    // Mettre à jour la vélocité pour suivre le serveur
                    puck.setVelocity(serverVx, serverVy);
                }
                // Sinon (distance <= 2), laisser la prédiction locale continuer
                
                // Position de la raquette du serveur (bas)
                remotePaddle.setX(Double.parseDouble(parts[7].replace(',', '.')));
                remotePaddle.setY(Double.parseDouble(parts[8].replace(',', '.')));
                
                // Mise à jour des pièces (si données présentes)
                if (parts.length >= 10 && !parts[9].isEmpty()) {
                    updatePiecesFromServer(parts[9]);
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Erreur parsing état: " + e.getMessage());
        }
    }
    
    /**
     * Met à jour les pièces côté client selon les données du serveur
     * Format: type,x,y,hp;type,x,y,hp;...
     */
    private void updatePiecesFromServer(String piecesData) {
        try {
            String[] piecesArray = piecesData.split(";");
            List<ChessPiece> allPieces = chessBoard.getAllPieces();
            
            // Mettre à jour chaque pièce
            for (String pieceStr : piecesArray) {
                String[] pieceData = pieceStr.split(",");
                if (pieceData.length >= 4) {
                    String type = pieceData[0];
                    double x = Double.parseDouble(pieceData[1].replace(',', '.'));
                    double y = Double.parseDouble(pieceData[2].replace(',', '.'));
                    int hp = Integer.parseInt(pieceData[3]);
                    
                    // Trouver la pièce correspondante
                    for (ChessPiece piece : allPieces) {
                        if (piece.getType().name().equals(type) &&
                            Math.abs(piece.getLayoutX() - x) < 5 &&
                            Math.abs(piece.getLayoutY() - y) < 5) {
                            
                            // Si la pièce est morte côté serveur (hp=0)
                            if (hp == 0 && piece.isAlive()) {
                                // Tuer la pièce jusqu'à ce qu'elle soit morte
                                while (piece.isAlive()) {
                                    piece.takeDamage();
                                }
                                System.out.println("🔴 Pièce supprimée côté client: " + type);
                            }
                            // Sinon, mettre à jour les HP si différents
                            else if (hp > 0 && piece.getHealth() != hp) {
                                // Réduire les HP jusqu'à atteindre la valeur du serveur
                                while (piece.getHealth() > hp && piece.getHealth() > 0) {
                                    piece.takeDamage();
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur mise à jour pièces: " + e.getMessage());
        }
    }
    
    /**
     * Parse la position de la raquette (côté serveur)
     */
    private void parsePaddlePosition(String message) {
        try {
            String[] parts = message.split("\\|");
            if (parts.length >= 3) {
                remotePaddle.setX(Double.parseDouble(parts[1].replace(',', '.')));
                remotePaddle.setY(Double.parseDouble(parts[2].replace(',', '.')));
            }
        } catch (NumberFormatException e) {
            System.err.println("Erreur parsing paddle: " + e.getMessage());
        }
    }
    
    /**
     * Parse les inputs reçus
     */
    private void parseInput(String message) {
        String[] parts = message.split("\\|");
        if (parts.length >= 3) {
            String inputType = parts[1];
            boolean pressed = parts[2].equals("1");
            
            switch (inputType) {
                case "LEFT":
                    if (pressed) remotePaddle.startMovingLeft();
                    else remotePaddle.stopMovingLeft();
                    break;
                case "RIGHT":
                    if (pressed) remotePaddle.startMovingRight();
                    else remotePaddle.stopMovingRight();
                    break;
                case "UP":
                    if (pressed) remotePaddle.startMovingUp();
                    else remotePaddle.stopMovingUp();
                    break;
                case "DOWN":
                    if (pressed) remotePaddle.startMovingDown();
                    else remotePaddle.stopMovingDown();
                    break;
            }
        }
    }
    
    /**
     * Parse le lancement de la balle
     */
    private void parseLaunch(String message) {
        try {
            String[] parts = message.split("\\|");
            if (parts.length >= 2) {
                double angle = Double.parseDouble(parts[1].replace(',', '.'));
                launchBallWithAngle(angle);
            }
        } catch (NumberFormatException e) {
            System.err.println("Erreur parsing launch: " + e.getMessage());
        }
    }
    
    /**
     * Parse l'activation d'un joueur distant
     */
    private void parseActivation(String message) {
        String[] parts = message.split("\\|");
        if (parts.length >= 2) {
            String playerType = parts[1]; // "LOCAL" ou "REMOTE"
            // L'autre joueur a activé son joueur local, qui est notre joueur distant
            if (joueurActif != null) {
                joueurActif.desactiver();
            }
            joueurActif = joueurDistant;
            joueurActif.activer();
            
            // Arrêter la balle et la positionner devant la raquette distante
            ballLaunched = false;
            puck.setVelocity(0, 0);
            
            if (remotePaddle == topPaddle) {
                // Raquette du haut
                puck.setCenterX(topPaddle.getX() + topPaddle.getWidth() / 2);
                puck.setCenterY(topPaddle.getY() + topPaddle.getHeight() + puck.getRadius() + 5);
                arrow.setLayoutX(topPaddle.getX() + topPaddle.getWidth() / 2);
                arrow.setLayoutY(topPaddle.getY() + topPaddle.getHeight() + 20);
            } else {
                // Raquette du bas
                puck.setCenterX(bottomPaddle.getX() + bottomPaddle.getWidth() / 2);
                puck.setCenterY(bottomPaddle.getY() - puck.getRadius() - 5);
                arrow.setLayoutX(bottomPaddle.getX() + bottomPaddle.getWidth() / 2);
                arrow.setLayoutY(bottomPaddle.getY() - 20);
            }
            
            // Démarrer l'animation de la flèche
            arrowAnimating = true;
            arrowAngleDirection = 1;
            joueurActif.setAngle(0);
            arrow.setAngle(0);
            arrow.setVisible(true);
        }
    }
    
    /**
     * Lance la balle avec un angle donné
     */
    private void launchBallWithAngle(double angle) {
        double speed = 5.0;
        double vx = Math.cos(Math.toRadians(angle)) * speed;
        double vy = Math.sin(Math.toRadians(angle)) * speed;
        puck.setVelocity(vx, vy);
        ballLaunched = true;
        arrowAnimating = false;
        arrow.setVisible(false);
    }
    
    /**
     * Démarre la boucle de jeu
     */
    public void start() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();
    }
    
    /**
     * Arrête la boucle de jeu
     */
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        // Fermer les connexions réseau
        if (server != null) server.stop();
        if (client != null) client.disconnect();
    }
    
    /**
     * Met à jour l'état du jeu
     */
    private void update() {
        if (gameOver || isPaused || !isConnected) {
            return;
        }
        
        // Animation automatique de la flèche si un joueur est actif
        if (joueurActif != null && arrowAnimating) {
            int currentAngle = joueurActif.getAngle();
            currentAngle += (int)(arrowAngleDirection * 2); // Vitesse d'animation
            
            // Inversion de direction aux limites
            if (currentAngle >= 60) {
                currentAngle = 60;
                arrowAngleDirection = -1;
            } else if (currentAngle <= -60) {
                currentAngle = -60;
                arrowAngleDirection = 1;
            }
            
            joueurActif.setAngle(currentAngle);
            arrow.setAngle(currentAngle);
        }
        
        // Mise à jour des raquettes
        topPaddle.update();
        bottomPaddle.update();
        
        // Le serveur gère la logique autoritaire du jeu
        if (isServer) {
            if (ballLaunched) {
                puck.update();
                checkCollisions();
                checkPieceCollision();
                checkGameOver();
            }
            
            // Envoyer l'état du jeu au client
            sendGameStateToClient();
        } else {
            // Le client aussi calcule la physique localement pour un mouvement fluide
            // (la synchronisation avec le serveur corrige les éventuelles dérives)
            if (ballLaunched) {
                puck.update();
                // Le client vérifie aussi les collisions localement pour une réactivité immédiate
                checkCollisions();
            }
        }
        
        // Envoyer la position de la raquette locale
        sendPaddlePosition();
    }
    
    /**
     * Envoie l'état du jeu au client (serveur uniquement)
     */
    private void sendGameStateToClient() {
        if (server != null && server.isClientConnected()) {
            // Limiter le taux d'envoi pour éviter de surcharger le réseau
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastNetworkUpdate < NETWORK_UPDATE_INTERVAL_MS) {
                return;
            }
            lastNetworkUpdate = currentTime;
            
            // Construire la chaîne des pièces: type,x,y,hp;type,x,y,hp;...
            // Inclure TOUTES les pièces (vivantes et mortes) pour synchroniser correctement
            StringBuilder piecesData = new StringBuilder();
            for (ChessPiece piece : chessBoard.getAllPieces()) {
                if (piecesData.length() > 0) {
                    piecesData.append(";");
                }
                // Envoyer hp=0 pour les pièces mortes
                int hp = piece.isAlive() ? piece.getHealth() : 0;
                piecesData.append(String.format(java.util.Locale.US, "%s,%.2f,%.2f,%d",
                    piece.getType().name(),
                    piece.getLayoutX(),
                    piece.getLayoutY(),
                    hp
                ));
            }
            
            server.sendGameState(
                puck.getCenterX(), puck.getCenterY(),
                puck.getVelocityX(), puck.getVelocityY(),
                topPaddle.getX(), topPaddle.getY(),
                bottomPaddle.getX(), bottomPaddle.getY(),
                piecesData.toString()
            );
        }
    }
    
    /**
     * Envoie la position de la raquette locale
     */
    private void sendPaddlePosition() {
        if (isServer && server != null) {
            // Le serveur envoie sa position via l'état du jeu
        } else if (client != null && client.isConnected()) {
            client.sendPaddlePosition(localPaddle.getX(), localPaddle.getY());
        }
    }
    
    /**
     * Vérifie les collisions avec les raquettes
     */
    private void checkCollisions() {
        // Collision avec la raquette du haut
        if (topPaddle.collidesWith(puck)) {
            String side = topPaddle.getCollisionSide(puck);
            double relativePos = topPaddle.getRelativePosition(puck);
            
            if ("bottom".equals(side)) {
                // Dessous de la raquette du haut
                puck.bounceWithSpin(relativePos, true);
                puck.setCenterY(topPaddle.getY() + topPaddle.getHeight() + puck.getRadius() + 1);
            } else if ("top".equals(side)) {
                // Dessus de la raquette du haut
                puck.bounceWithSpin(relativePos, true);
                puck.setCenterY(topPaddle.getY() - puck.getRadius() - 1);
            } else if ("left".equals(side)) {
                // Côté gauche
                puck.setCenterX(topPaddle.getX() - puck.getRadius() - 1);
                puck.setVelocity(-Math.abs(puck.getVelocityX()), puck.getVelocityY());
            } else if ("right".equals(side)) {
                // Côté droit
                puck.setCenterX(topPaddle.getX() + topPaddle.getWidth() + puck.getRadius() + 1);
                puck.setVelocity(Math.abs(puck.getVelocityX()), puck.getVelocityY());
            }
        }
        
        // Collision avec la raquette du bas
        if (bottomPaddle.collidesWith(puck)) {
            String side = bottomPaddle.getCollisionSide(puck);
            double relativePos = bottomPaddle.getRelativePosition(puck);
            
            if ("top".equals(side)) {
                // Dessus de la raquette du bas
                puck.bounceWithSpin(relativePos, false);
                puck.setCenterY(bottomPaddle.getY() - puck.getRadius() - 1);
            } else if ("bottom".equals(side)) {
                // Dessous de la raquette du bas
                puck.bounceWithSpin(relativePos, false);
                puck.setCenterY(bottomPaddle.getY() + bottomPaddle.getHeight() + puck.getRadius() + 1);
            } else if ("left".equals(side)) {
                // Côté gauche
                puck.setCenterX(bottomPaddle.getX() - puck.getRadius() - 1);
                puck.setVelocity(-Math.abs(puck.getVelocityX()), puck.getVelocityY());
            } else if ("right".equals(side)) {
                // Côté droit
                puck.setCenterX(bottomPaddle.getX() + bottomPaddle.getWidth() + puck.getRadius() + 1);
                puck.setVelocity(Math.abs(puck.getVelocityX()), puck.getVelocityY());
            }
        }
    }
    
    /**
     * Vérifie les collisions avec les pièces d'échecs
     */
    private void checkPieceCollision() {
        double puckX = puck.getCenterX();
        double puckY = puck.getCenterY();
        
        ChessPiece hitPiece = chessBoard.getPieceAt(puckX, puckY);
        
        if (hitPiece != null && hitPiece.isAlive()) {
            // La pièce perd 1 PV
            boolean stillAlive = hitPiece.takeDamage();
            
            // La balle rebondit dans la direction opposée verticale
            puck.bounce();
            
            // Ajoute un petit effet de rebond horizontal aléatoire pour rendre le jeu plus dynamique
            double currentVx = puck.getVelocityX();
            double randomSpin = (Math.random() - 0.5) * 2; // Entre -1 et 1
            puck.setVelocity(currentVx + randomSpin, puck.getVelocityY());
            
            if (!stillAlive) {
                System.out.println("💥 " + hitPiece.getType().getName() + " détruit!");
            }
        }
    }
    
    /**
     * Vérifie si la partie est terminée
     */
    private void checkGameOver() {
        ChessPiece whiteKing = chessBoard.getKing(ChessPiece.PieceColor.WHITE);
        ChessPiece blackKing = chessBoard.getKing(ChessPiece.PieceColor.BLACK);
        
        if (whiteKing == null || !whiteKing.isAlive()) {
            gameOver = true;
            winner = joueurDistant.getNom();
            if (onVictoryCallback != null) {
                onVictoryCallback.run();
            }
        } else if (blackKing == null || !blackKing.isAlive()) {
            gameOver = true;
            winner = joueurLocal.getNom();
            if (onVictoryCallback != null) {
                onVictoryCallback.run();
            }
        }
    }
    
    /**
     * Gestion des touches pressées
     */
    public void handleKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();
        String inputType = null;
        
        System.out.println("🎮 Touche pressée: " + code + " (isServer=" + isServer + ", localPaddle=" + (localPaddle == topPaddle ? "TOP" : "BOTTOM") + ")");
        
        // Les deux joueurs peuvent utiliser les flèches OU le pavé numérique
        switch (code) {
            // Flèches (pour tous)
            case LEFT:
            case NUMPAD4:
                localPaddle.startMovingLeft();
                inputType = "LEFT";
                break;
            case RIGHT:
            case NUMPAD6:
                localPaddle.startMovingRight();
                inputType = "RIGHT";
                break;
            case UP:
            case NUMPAD8:
                localPaddle.startMovingUp();
                inputType = "UP";
                break;
            case DOWN:
            case NUMPAD2:
                localPaddle.startMovingDown();
                inputType = "DOWN";
                break;
            case ENTER:
                if (!ballLaunched) {
                    launchBall();
                }
                break;
            default:
                break;
        }
        
        // Envoyer l'input au réseau
        if (inputType != null) {
            sendInput(inputType, true);
        }
    }
    
    /**
     * Gestion des touches relâchées
     */
    public void handleKeyReleased(KeyEvent event) {
        KeyCode code = event.getCode();
        String inputType = null;
        
        // Les deux joueurs peuvent utiliser les flèches OU le pavé numérique
        switch (code) {
            // Flèches (pour tous)
            case LEFT:
            case NUMPAD4:
                localPaddle.stopMovingLeft();
                inputType = "LEFT";
                break;
            case RIGHT:
            case NUMPAD6:
                localPaddle.stopMovingRight();
                inputType = "RIGHT";
                break;
            case UP:
            case NUMPAD8:
                localPaddle.stopMovingUp();
                inputType = "UP";
                break;
            case DOWN:
            case NUMPAD2:
                localPaddle.stopMovingDown();
                inputType = "DOWN";
                break;
            default:
                break;
        }
        
        // Envoyer l'input au réseau
        if (inputType != null) {
            sendInput(inputType, false);
        }
    }
    
    /**
     * Envoie un input au réseau
     */
    private void sendInput(String inputType, boolean pressed) {
        if (isServer && server != null) {
            server.sendMessage("INPUT|" + inputType + "|" + (pressed ? "1" : "0"));
        } else if (client != null) {
            client.sendInput(inputType, pressed);
        }
    }
    
    /**
     * Lance la balle
     */
    private void launchBall() {
        if (joueurActif == null) {
            return; // Pas de joueur prêt à lancer
        }
        
        // Récupérer l'angle actuel du joueur
        double[] velocite = joueurActif.getVelociteLancement();
        double angle = Math.toDegrees(Math.atan2(velocite[1], velocite[0]));
        
        launchBallWithAngle(angle);
        
        // Arrêter l'animation
        arrowAnimating = false;
        joueurActif.desactiver();
        joueurActif = null;
        arrow.setVisible(false);
        
        // Notifier l'autre joueur
        if (isServer && server != null) {
            server.sendMessage("LAUNCH|" + angle);
        } else if (client != null) {
            client.sendMessage("LAUNCH|" + angle);
        }
    }
    
    public void pause() { 
        isPaused = true;
        // Notifier l'autre joueur
        sendPauseMessage();
        System.out.println("⏸️ Jeu mis en pause");
    }
    
    public void resume() { 
        isPaused = false;
        // Notifier l'autre joueur
        sendResumeMessage();
        System.out.println("▶️ Jeu repris");
    }
    
    /**
     * Envoie un message de pause à l'autre joueur
     */
    private void sendPauseMessage() {
        if (isServer && server != null && server.isClientConnected()) {
            server.sendMessage("PAUSE");
        } else if (!isServer && client != null && client.isConnected()) {
            client.sendMessage("PAUSE");
        }
    }
    
    /**
     * Envoie un message de reprise à l'autre joueur
     */
    private void sendResumeMessage() {
        if (isServer && server != null && server.isClientConnected()) {
            server.sendMessage("RESUME");
        } else if (!isServer && client != null && client.isConnected()) {
            client.sendMessage("RESUME");
        }
    }
    
    /**
     * Active le joueur local pour préparer le tir
     * Positionne la balle devant la raquette et démarre l'animation de la flèche
     */
    public void activerJoueurLocal() {
        if (joueurActif != null) {
            joueurActif.desactiver();
        }
        joueurActif = joueurLocal;
        joueurActif.activer();
        
        // Arrêter la balle et la positionner devant la raquette locale
        ballLaunched = false;
        puck.setVelocity(0, 0);
        
        if (localPaddle == topPaddle) {
            // Raquette du haut
            puck.setCenterX(topPaddle.getX() + topPaddle.getWidth() / 2);
            puck.setCenterY(topPaddle.getY() + topPaddle.getHeight() + puck.getRadius() + 5);
            arrow.setLayoutX(topPaddle.getX() + topPaddle.getWidth() / 2);
            arrow.setLayoutY(topPaddle.getY() + topPaddle.getHeight() + 20);
        } else {
            // Raquette du bas
            puck.setCenterX(bottomPaddle.getX() + bottomPaddle.getWidth() / 2);
            puck.setCenterY(bottomPaddle.getY() - puck.getRadius() - 5);
            arrow.setLayoutX(bottomPaddle.getX() + bottomPaddle.getWidth() / 2);
            arrow.setLayoutY(bottomPaddle.getY() - 20);
        }
        
        // Démarrer l'animation de la flèche
        arrowAnimating = true;
        arrowAngleDirection = 1;
        joueurActif.setAngle(0); // Commencer à l'angle 0
        arrow.setAngle(0);
        arrow.setVisible(true);
        
        // Envoyer un message réseau pour informer l'autre joueur
        sendActivation();
    }
    
    /**
     * Envoie un message d'activation à l'autre joueur
     */
    private void sendActivation() {
        String message = "ACTIVATE|LOCAL";
        if (isServer && server != null) {
            server.sendMessage(message);
        } else if (!isServer && client != null) {
            client.sendMessage(message);
        }
    }
    
    // Getters
    public Puck getPuck() { return puck; }
    public Paddle getTopPaddle() { return topPaddle; }
    public Paddle getBottomPaddle() { return bottomPaddle; }
    public Arrow getArrow() { return arrow; }
    public boolean isGameOver() { return gameOver; }
    public String getWinner() { return winner; }
    public GameScore getGameScore() { return gameScore; }
    public boolean isServer() { return isServer; }
    public boolean isConnected() { return isConnected; }
    
    public void setOnVictoryCallback(Runnable callback) { this.onVictoryCallback = callback; }
    public void setOnClientConnectedCallback(Runnable callback) { this.onClientConnectedCallback = callback; }
    public void setOnDisconnectedCallback(Runnable callback) { this.onDisconnectedCallback = callback; }
    public void setOnPauseCallback(Runnable callback) { this.onPauseCallback = callback; }
    public void setOnResumeCallback(Runnable callback) { this.onResumeCallback = callback; }
    public void setOnConfigReceivedCallback(Runnable callback) { this.onConfigReceivedCallback = callback; }
    
    /**
     * Met à jour l'échiquier après réception de la configuration
     */
    public void updateChessBoard(ChessBoard newChessBoard) {
        this.chessBoard = newChessBoard;
        this.WIDTH = chessBoard.getBoardWidth();
        this.HEIGHT = chessBoard.getBoardHeight();
        
        // Repositionner les raquettes
        topPaddle.setX(WIDTH / 2 - 35);
        bottomPaddle.setX(WIDTH / 2 - 35);
        
        // Repositionner la balle
        puck.setCenterX(WIDTH / 2);
        puck.setCenterY(HEIGHT / 2);
    }
    
    public String getServerIP() {
        return isServer ? GameServer.getLocalIPAddress() : client.getServerIP();
    }
    
    public int getPort() {
        return isServer ? server.getPort() : client.getServerPort();
    }
}
