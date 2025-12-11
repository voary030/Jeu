package com.pong.ui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Classe représentant la flèche directionnelle de lancement
 */
public class Arrow extends Polygon {
    private final double length = 50;
    private double angle = 0;
    
    public Arrow() {
        setFill(Color.web("#27ae60"));
        setStroke(Color.web("#229954"));
        setStrokeWidth(3);
        
        updateArrow();
    }
    
    /**
     * Met à jour la rotation de la flèche
     */
    public void setAngle(double angleInDegrees) {
        this.angle = angleInDegrees;
        updateArrow();
    }
    
    private void updateArrow() {
        getPoints().clear();
        
        double angleRad = Math.toRadians(angle);
        
        // Point de la flèche (avant) - depuis l'origine
        double endX = Math.sin(angleRad) * length;
        double endY = Math.cos(angleRad) * length;
        
        // Points de la base de la flèche
        double baseOffsetX = Math.cos(angleRad) * 10;
        double baseOffsetY = -Math.sin(angleRad) * 10;
        
        double base1X = baseOffsetX;
        double base1Y = baseOffsetY;
        
        double base2X = -baseOffsetX;
        double base2Y = -baseOffsetY;
        
        // Ajoute les points du triangle (pointe -> base)
        getPoints().addAll(
            endX, endY,           // Pointe
            base1X, base1Y,       // Base droit
            base2X, base2Y        // Base gauche
        );
    }
    
    public double getAngle() {
        return angle;
    }
}
