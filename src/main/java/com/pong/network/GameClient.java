package com.pong.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Client de jeu pour le mode multijoueur LAN
 * Le joueur qui rejoint une partie utilise ce client
 */
public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String serverIP;
    private int serverPort;
    private AtomicBoolean connected = new AtomicBoolean(false);
    private NetworkMessageListener messageListener;
    private Thread listenerThread;
    
    public GameClient(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }
    
    /**
     * Se connecte au serveur
     */
    public boolean connect() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverIP, serverPort), 5000); // Timeout 5 secondes
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected.set(true);
            System.out.println("✅ Connecté au serveur " + serverIP + ":" + serverPort);
            
            // Démarrer l'écoute des messages
            startListening();
            
            return true;
        } catch (IOException e) {
            System.err.println("❌ Impossible de se connecter: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Démarre l'écoute des messages du serveur
     */
    private void startListening() {
        listenerThread = new Thread(() -> {
            try {
                String message;
                while (connected.get() && (message = in.readLine()) != null) {
                    if (messageListener != null) {
                        messageListener.onMessage(message);
                    }
                }
            } catch (IOException e) {
                if (connected.get()) {
                    System.err.println("❌ Connexion perdue: " + e.getMessage());
                    connected.set(false);
                    if (messageListener != null) {
                        messageListener.onMessage("DISCONNECTED");
                    }
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }
    
    /**
     * Envoie un message au serveur
     */
    public void sendMessage(String message) {
        if (out != null && connected.get()) {
            out.println(message);
        }
    }
    
    /**
     * Envoie les inputs du joueur au serveur
     */
    public void sendInput(String inputType, boolean pressed) {
        sendMessage("INPUT|" + inputType + "|" + (pressed ? "1" : "0"));
    }
    
    /**
     * Envoie la position de la raquette au serveur
     */
    public void sendPaddlePosition(double x, double y) {
        sendMessage(String.format(java.util.Locale.US, "PADDLE|%.2f|%.2f", x, y));
    }
    
    /**
     * Envoie le lancement de la balle
     */
    public void sendBallLaunch(double angle) {
        sendMessage(String.format(java.util.Locale.US, "LAUNCH|%.2f", angle));
    }
    
    /**
     * Se déconnecte du serveur
     */
    public void disconnect() {
        connected.set(false);
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("🛑 Déconnecté du serveur");
        } catch (IOException e) {
            System.err.println("Erreur déconnexion: " + e.getMessage());
        }
    }
    
    /**
     * Définit le listener pour les messages reçus
     */
    public void setMessageListener(NetworkMessageListener listener) {
        this.messageListener = listener;
    }
    
    /**
     * Vérifie si connecté au serveur
     */
    public boolean isConnected() {
        return connected.get();
    }
    
    public String getServerIP() {
        return serverIP;
    }
    
    public int getServerPort() {
        return serverPort;
    }
}
