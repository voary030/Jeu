package com.pong.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Serveur de jeu pour le mode multijoueur LAN
 * Le joueur qui héberge la partie lance ce serveur
 */
public class GameServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int port;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean clientConnected = new AtomicBoolean(false);
    private NetworkMessageListener messageListener;
    private Thread listenerThread;
    
    public GameServer(int port) {
        this.port = port;
    }
    
    /**
     * Démarre le serveur et attend une connexion client
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running.set(true);
        System.out.println("🎮 Serveur démarré sur le port " + port);
        System.out.println("📡 En attente d'un joueur...");
        
        // Attendre la connexion dans un thread séparé
        new Thread(() -> {
            try {
                clientSocket = serverSocket.accept();
                clientConnected.set(true);
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println("✅ Joueur connecté: " + clientSocket.getInetAddress());
                
                // Démarrer l'écoute des messages
                startListening();
                
                // Notifier que le client est connecté
                if (messageListener != null) {
                    messageListener.onMessage("CLIENT_CONNECTED");
                }
            } catch (IOException e) {
                if (running.get()) {
                    System.err.println("❌ Erreur connexion client: " + e.getMessage());
                }
            }
        }).start();
    }
    
    /**
     * Démarre l'écoute des messages du client
     */
    private void startListening() {
        listenerThread = new Thread(() -> {
            try {
                String message;
                while (running.get() && (message = in.readLine()) != null) {
                    if (messageListener != null) {
                        messageListener.onMessage(message);
                    }
                }
            } catch (IOException e) {
                if (running.get()) {
                    System.err.println("❌ Erreur lecture: " + e.getMessage());
                    clientConnected.set(false);
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }
    
    /**
     * Envoie un message au client
     */
    public void sendMessage(String message) {
        if (out != null && clientConnected.get()) {
            out.println(message);
        }
    }
    
    /**
     * Envoie l'état du jeu au client
     */
    public void sendGameState(double ballX, double ballY, double ballVx, double ballVy,
                              double paddle1X, double paddle1Y,
                              double paddle2X, double paddle2Y,
                              String pieceStates) {
        // Utiliser Locale.US pour forcer le point comme séparateur décimal
        String state = String.format(java.util.Locale.US, "STATE|%.2f|%.2f|%.2f|%.2f|%.2f|%.2f|%.2f|%.2f|%s",
                ballX, ballY, ballVx, ballVy,
                paddle1X, paddle1Y, paddle2X, paddle2Y, pieceStates);
        sendMessage(state);
    }
    
    /**
     * Arrête le serveur
     */
    public void stop() {
        running.set(false);
        clientConnected.set(false);
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
            System.out.println("🛑 Serveur arrêté");
        } catch (IOException e) {
            System.err.println("Erreur fermeture serveur: " + e.getMessage());
        }
    }
    
    /**
     * Définit le listener pour les messages reçus
     */
    public void setMessageListener(NetworkMessageListener listener) {
        this.messageListener = listener;
    }
    
    /**
     * Vérifie si un client est connecté
     */
    public boolean isClientConnected() {
        return clientConnected.get();
    }
    
    /**
     * Vérifie si le serveur est en cours d'exécution
     */
    public boolean isRunning() {
        return running.get();
    }
    
    /**
     * Obtient l'adresse IP locale du serveur
     */
    public static String getLocalIPAddress() {
        try {
            // Essayer de trouver l'adresse IP LAN
            for (NetworkInterface ni : java.util.Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress addr : java.util.Collections.list(ni.getInetAddresses())) {
                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }
    
    public int getPort() {
        return port;
    }
}
