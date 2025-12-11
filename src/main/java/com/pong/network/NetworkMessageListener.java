package com.pong.network;

/**
 * Interface pour écouter les messages réseau
 */
public interface NetworkMessageListener {
    void onMessage(String message);
}
