package com.pong.ui;

import com.pong.config.BoardConfig;
import com.pong.models.ChessPiece;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente l'échiquier avec la zone de jeu au centre
 * La taille s'adapte dynamiquement selon BoardConfig
 */
public class ChessBoard extends GridPane {
    
    private final int ROWS;
    private final int COLS;
    private final double SQUARE_SIZE;
    
    private List<ChessPiece> pieces;
    private Rectangle[][] squares;
    
    public ChessBoard() {
        // Utilise la configuration dynamique
        this.ROWS = BoardConfig.getNumberOfRows();
        this.COLS = BoardConfig.getNumberOfColumns();
        this.SQUARE_SIZE = BoardConfig.getCellSize();
        
        pieces = new ArrayList<>();
        squares = new Rectangle[ROWS][COLS];
        
        createBoard();
        setupPieces();
    }
    
    private void createBoard() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
                
                // Zone de jeu bleue au centre (lignes 2-5, colonnes 0-7)
                if (row >= 2 && row <= 5) {
                    square.setFill(Color.web("#6c7285ff")); // Bleu comme dans le HTML
                } else {
                    // Damier pour les lignes avec pièces
                    if ((row + col) % 2 == 0) {
                        square.setFill(Color.web("#f0d9b5")); // Beige clair
                    } else {
                        square.setFill(Color.web("#b58863")); // Marron clair
                    }
                }
                
                square.setStroke(Color.web("#7d5a44"));
                square.setStrokeWidth(1);
                
                squares[row][col] = square;
                add(square, col, row);
            }
        }
    }
    
    private void setupPieces() {
        // Pièces BLANCHES en haut (lignes 0 et 1)
        setupWhitePieces();
        
        // Pièces NOIRES en bas (lignes 6 et 7)
        setupBlackPieces();
    }
    
    private void setupWhitePieces() {
        // Calcul du centre pour positionner Roi et Reine
        int center = COLS / 2;
        
        // Ligne 1: Pions blancs (toujours présents)
        for (int col = 0; col < COLS; col++) {
            addPiece(ChessPiece.PieceType.PAWN, ChessPiece.PieceColor.WHITE, 1, col);
        }
        
        // Ligne 0: Pièces majeures selon la configuration
        // Roi et Reine toujours au centre
        if (COLS % 2 == 0) {
            // Nombre pair de colonnes
            addPiece(ChessPiece.PieceType.QUEEN, ChessPiece.PieceColor.WHITE, 0, center - 1);
            addPiece(ChessPiece.PieceType.KING, ChessPiece.PieceColor.WHITE, 0, center);
        }
        
        // Ajoute les Fous si configuration >= 4 pions
        if (BoardConfig.hasBishops() && COLS >= 4) {
            if (COLS == 4) {
                addPiece(ChessPiece.PieceType.BISHOP, ChessPiece.PieceColor.WHITE, 0, 0);
                addPiece(ChessPiece.PieceType.BISHOP, ChessPiece.PieceColor.WHITE, 0, COLS - 1);
            } else {
                addPiece(ChessPiece.PieceType.BISHOP, ChessPiece.PieceColor.WHITE, 0, center - 2);
                addPiece(ChessPiece.PieceType.BISHOP, ChessPiece.PieceColor.WHITE, 0, center + 1);
            }
        }
        
        // Ajoute les Cavaliers si configuration >= 6 pions
        if (BoardConfig.hasKnights() && COLS >= 6) {
            if (COLS == 6) {
                addPiece(ChessPiece.PieceType.KNIGHT, ChessPiece.PieceColor.WHITE, 0, 0);
                addPiece(ChessPiece.PieceType.KNIGHT, ChessPiece.PieceColor.WHITE, 0, COLS - 1);
            } else {
                addPiece(ChessPiece.PieceType.KNIGHT, ChessPiece.PieceColor.WHITE, 0, 1);
                addPiece(ChessPiece.PieceType.KNIGHT, ChessPiece.PieceColor.WHITE, 0, COLS - 2);
            }
        }
        
        // Ajoute les Tours si configuration >= 8 pions
        if (BoardConfig.hasRooks() && COLS >= 8) {
            addPiece(ChessPiece.PieceType.ROOK, ChessPiece.PieceColor.WHITE, 0, 0);
            addPiece(ChessPiece.PieceType.ROOK, ChessPiece.PieceColor.WHITE, 0, COLS - 1);
        }
    }
    
    private void setupBlackPieces() {
        // Calcul du centre pour positionner Roi et Reine
        int center = COLS / 2;
        
        // Ligne 6: Pions noirs (toujours présents)
        for (int col = 0; col < COLS; col++) {
            addPiece(ChessPiece.PieceType.PAWN, ChessPiece.PieceColor.BLACK, 6, col);
        }
        
        // Ligne 7: Pièces majeures selon la configuration
        // Roi et Reine toujours au centre
        if (COLS % 2 == 0) {
            // Nombre pair de colonnes - configuration miroir des blancs
            addPiece(ChessPiece.PieceType.KING, ChessPiece.PieceColor.BLACK, 7, center - 1);
            addPiece(ChessPiece.PieceType.QUEEN, ChessPiece.PieceColor.BLACK, 7, center);
        }
        
        // Ajoute les Fous si configuration >= 4 pions
        if (BoardConfig.hasBishops() && COLS >= 4) {
            if (COLS == 4) {
                addPiece(ChessPiece.PieceType.BISHOP, ChessPiece.PieceColor.BLACK, 7, 0);
                addPiece(ChessPiece.PieceType.BISHOP, ChessPiece.PieceColor.BLACK, 7, COLS - 1);
            } else {
                addPiece(ChessPiece.PieceType.BISHOP, ChessPiece.PieceColor.BLACK, 7, center - 2);
                addPiece(ChessPiece.PieceType.BISHOP, ChessPiece.PieceColor.BLACK, 7, center + 1);
            }
        }
        
        // Ajoute les Cavaliers si configuration >= 6 pions
        if (BoardConfig.hasKnights() && COLS >= 6) {
            if (COLS == 6) {
                addPiece(ChessPiece.PieceType.KNIGHT, ChessPiece.PieceColor.BLACK, 7, 0);
                addPiece(ChessPiece.PieceType.KNIGHT, ChessPiece.PieceColor.BLACK, 7, COLS - 1);
            } else {
                addPiece(ChessPiece.PieceType.KNIGHT, ChessPiece.PieceColor.BLACK, 7, 1);
                addPiece(ChessPiece.PieceType.KNIGHT, ChessPiece.PieceColor.BLACK, 7, COLS - 2);
            }
        }
        
        // Ajoute les Tours si configuration >= 8 pions
        if (BoardConfig.hasRooks() && COLS >= 8) {
            addPiece(ChessPiece.PieceType.ROOK, ChessPiece.PieceColor.BLACK, 7, 0);
            addPiece(ChessPiece.PieceType.ROOK, ChessPiece.PieceColor.BLACK, 7, COLS - 1);
        }
    }
    
    private void addPiece(ChessPiece.PieceType type, ChessPiece.PieceColor color, int row, int col) {
        ChessPiece piece = new ChessPiece(type, color, row, col);
        pieces.add(piece);
        add(piece, col, row);
    }
    
    /**
     * Retourne toutes les pièces vivantes
     */
    public List<ChessPiece> getAlivePieces() {
        List<ChessPiece> alivePieces = new ArrayList<>();
        for (ChessPiece piece : pieces) {
            if (piece.isAlive()) {
                alivePieces.add(piece);
            }
        }
        return alivePieces;
    }
    
    /**
     * Retourne toutes les pièces (vivantes et mortes)
     */
    public List<ChessPiece> getAllPieces() {
        return pieces;
    }
    
    /**
     * Retourne une pièce à une position donnée (en pixels)
     */
    public ChessPiece getPieceAt(double x, double y) {
        int col = (int) (x / SQUARE_SIZE);
        int row = (int) (y / SQUARE_SIZE);
        
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return null;
        }
        
        for (ChessPiece piece : pieces) {
            if (piece.getRow() == row && piece.getCol() == col && piece.isAlive()) {
                return piece;
            }
        }
        return null;
    }
    
    /**
     * Cherche le roi d'une couleur donnée
     */
    public ChessPiece getKing(ChessPiece.PieceColor color) {
        for (ChessPiece piece : pieces) {
            if (piece.getType() == ChessPiece.PieceType.KING 
                && piece.getPieceColor() == color 
                && piece.isAlive()) {
                return piece;
            }
        }
        return null;
    }
    
    public double getSquareSize() {
        return SQUARE_SIZE;
    }
    
    public int getRows() {
        return ROWS;
    }
    
    public int getCols() {
        return COLS;
    }
    
    public double getBoardWidth() {
        return COLS * SQUARE_SIZE;
    }
    
    public double getBoardHeight() {
        return ROWS * SQUARE_SIZE;
    }
    
    /**
     * Réinitialise l'échiquier pour une nouvelle partie
     */
    public void resetBoard() {
        // Retire toutes les pièces actuelles
        for (ChessPiece piece : pieces) {
            this.getChildren().remove(piece);
        }
        pieces.clear();
        
        // Recrée les pièces
        setupPieces();
    }
}
