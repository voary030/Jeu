package com.pong.models;

import com.pong.config.PieceHealthConfig;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Représente une pièce d'échecs avec des points de vie
 */
public class ChessPiece extends StackPane {
    
    public enum PieceType {
        PAWN("♟", "Pion"),
        ROOK("♜", "Tour"),
        KNIGHT("♞", "Cavalier"),
        BISHOP("♝", "Fou"),
        QUEEN("♛", "Reine"),
        KING("♚", "Roi");
        
        private final String symbol;
        private final String name;
        
        PieceType(String symbol, String name) {
            this.symbol = symbol;
            this.name = name;
        }
        
        public String getSymbol() { return symbol; }
        public String getName() { return name; }
        
        public int getMaxHealth() {
            switch (this) {
                case KING: return PieceHealthConfig.getKingHealth();
                case QUEEN: return PieceHealthConfig.getQueenHealth();
                case ROOK: return PieceHealthConfig.getRookHealth();
                case BISHOP: return PieceHealthConfig.getBishopHealth();
                case KNIGHT: return PieceHealthConfig.getKnightHealth();
                case PAWN: return PieceHealthConfig.getPawnHealth();
                default: return 1;
            }
        }
    }
    
    public enum PieceColor {
        WHITE, BLACK
    }
    
    private final PieceType type;
    private final PieceColor color;
    private int health;
    private final int maxHealth;
    
    private Text symbolText;
    private Text healthText;
    private Rectangle background;
    
    private final int row;
    private final int col;
    
    public ChessPiece(PieceType type, PieceColor color, int row, int col) {
        this.type = type;
        this.color = color;
        this.row = row;
        this.col = col;
        this.maxHealth = type.getMaxHealth();
        this.health = maxHealth;
        
        createVisuals();
    }
    
    private void createVisuals() {
        // Fond transparent pour la pièce
        background = new Rectangle(60, 60);
        background.setFill(Color.TRANSPARENT);
        
        // Symbole de la pièce
        symbolText = new Text(type.getSymbol());
        symbolText.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        symbolText.setFill(color == PieceColor.WHITE ? Color.WHITE : Color.BLACK);
        
        // Indicateur de vie
        healthText = new Text(health + " PV");
        healthText.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        healthText.setFill(Color.RED);
        healthText.setTranslateY(20);
        
        getChildren().addAll(background, symbolText, healthText);
    }
    
    /**
     * Réduit la vie de la pièce de 1 point
     * @return true si la pièce est toujours en vie
     */
    public boolean takeDamage() {
        health--;
        updateHealthDisplay();
        
        if (health <= 0) {
            setVisible(false);
            return false;
        }
        
        // Animation de dégât (rouge clignotant)
        animateDamage();
        return true;
    }
    
    private void updateHealthDisplay() {
        healthText.setText(health + " PV");
        
        // Couleur selon la vie restante
        if (health <= maxHealth * 0.3) {
            healthText.setFill(Color.DARKRED);
        } else if (health <= maxHealth * 0.6) {
            healthText.setFill(Color.ORANGE);
        } else {
            healthText.setFill(Color.RED);
        }
    }
    
    private void animateDamage() {
        // Effet de clignotement
        symbolText.setFill(Color.RED);
        new Thread(() -> {
            try {
                Thread.sleep(100);
                javafx.application.Platform.runLater(() -> {
                    symbolText.setFill(color == PieceColor.WHITE ? Color.WHITE : Color.BLACK);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    public PieceType getType() {
        return type;
    }
    
    public PieceColor getPieceColor() {
        return color;
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public boolean isAlive() {
        return health > 0;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
}
