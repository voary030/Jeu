package com.echecpong.config.entity;

import javax.persistence.*;

/**
 * Entité JPA pour la configuration du plateau
 */
@Entity
@Table(name = "board_config")
public class BoardConfigEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "number_of_pawns")
    private Integer numberOfPawns;
    
    @Column(name = "config_version")
    private String configVersion;
    
    // Constructeurs
    public BoardConfigEntity() {
    }
    
    public BoardConfigEntity(Integer numberOfPawns, String configVersion) {
        this.numberOfPawns = numberOfPawns;
        this.configVersion = configVersion;
    }
    
    // Getters & Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getNumberOfPawns() {
        return numberOfPawns;
    }
    
    public void setNumberOfPawns(Integer numberOfPawns) {
        this.numberOfPawns = numberOfPawns;
    }
    
    public String getConfigVersion() {
        return configVersion;
    }
    
    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }
}
