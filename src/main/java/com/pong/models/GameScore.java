package com.pong.models;

/**
 * Classe pour gérer le score des joueurs
 */
public class GameScore {
    private int player1Wins;
    private int player2Wins;
    private final String player1Name;
    private final String player2Name;
    
    public GameScore(String player1Name, String player2Name) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.player1Wins = 0;
        this.player2Wins = 0;
    }
    
    public void addWinForPlayer1() {
        player1Wins++;
    }
    
    public void addWinForPlayer2() {
        player2Wins++;
    }
    
    public int getPlayer1Wins() {
        return player1Wins;
    }
    
    public int getPlayer2Wins() {
        return player2Wins;
    }
    
    public String getPlayer1Name() {
        return player1Name;
    }
    
    public String getPlayer2Name() {
        return player2Name;
    }
    
    public String getScoreText() {
        return player1Name + ": " + player1Wins + " - " + player2Name + ": " + player2Wins;
    }
}
