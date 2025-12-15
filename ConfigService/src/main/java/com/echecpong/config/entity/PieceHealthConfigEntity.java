package com.echecpong.config.entity;

import javax.persistence.*;

/**
 * Entité JPA pour la configuration des points de vie des pièces
 */
@Entity
@Table(name = "piece_health_config")
public class PieceHealthConfigEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "piece_name")
    private String pieceName; // KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
    
    @Column(name = "health_points")
    private Integer healthPoints;
    
    @Column(name = "config_version")
    private String configVersion;
    
    // Constructeurs
    public PieceHealthConfigEntity() {
    }
    
    public PieceHealthConfigEntity(String pieceName, Integer healthPoints, String configVersion) {
        this.pieceName = pieceName;
        this.healthPoints = healthPoints;
        this.configVersion = configVersion;
    }
    
    // Getters & Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPieceName() {
        return pieceName;
    }
    
    public void setPieceName(String pieceName) {
        this.pieceName = pieceName;
    }
    
    public Integer getHealthPoints() {
        return healthPoints;
    }
    
    public void setHealthPoints(Integer healthPoints) {
        this.healthPoints = healthPoints;
    }
    
    public String getConfigVersion() {
        return configVersion;
    }
    
    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }
}
