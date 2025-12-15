package com.echecpong.config.ejb;

import com.echecpong.config.entity.BoardConfigEntity;
import com.echecpong.config.entity.PieceHealthConfigEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EJB Stateless pour gérer les configurations
 */
@Stateless
public class ConfigServiceBean {
    
    @PersistenceContext
    private EntityManager em;
    
    /**
     * Récupère la configuration du plateau
     */
    public BoardConfigEntity getBoardConfig() {
        List<BoardConfigEntity> results = em.createQuery(
            "SELECT b FROM BoardConfigEntity b ORDER BY b.id DESC", 
            BoardConfigEntity.class)
            .setMaxResults(1)
            .getResultList();
        
        if (results.isEmpty()) {
            return getDefaultBoardConfig();
        }
        return results.get(0);
    }
    
    /**
     * Sauvegarde la configuration du plateau
     */
    public BoardConfigEntity saveBoardConfig(BoardConfigEntity config) {
        if (config.getId() == null) {
            em.persist(config);
        } else {
            config = em.merge(config);
        }
        return config;
    }
    
    /**
     * Récupère toutes les configurations des pièces
     */
    public List<PieceHealthConfigEntity> getAllPieceHealthConfig() {
        List<PieceHealthConfigEntity> results = em.createQuery(
            "SELECT p FROM PieceHealthConfigEntity p ORDER BY p.pieceName", 
            PieceHealthConfigEntity.class)
            .getResultList();
        
        if (results.isEmpty()) {
            return getDefaultPieceHealthConfigs();
        }
        return results;
    }
    
    /**
     * Récupère la configuration de santé pour une pièce spécifique
     */
    public PieceHealthConfigEntity getPieceHealthConfig(String pieceName) {
        List<PieceHealthConfigEntity> results = em.createQuery(
            "SELECT p FROM PieceHealthConfigEntity p WHERE p.pieceName = :pieceName ORDER BY p.id DESC", 
            PieceHealthConfigEntity.class)
            .setParameter("pieceName", pieceName)
            .setMaxResults(1)
            .getResultList();
        
        if (results.isEmpty()) {
            return getDefaultPieceHealthConfig(pieceName);
        }
        return results.get(0);
    }
    
    /**
     * Sauvegarde la configuration de santé d'une pièce
     */
    public PieceHealthConfigEntity savePieceHealthConfig(PieceHealthConfigEntity config) {
        if (config.getId() == null) {
            em.persist(config);
        } else {
            config = em.merge(config);
        }
        return config;
    }
    
    /**
     * Retourne la configuration par défaut du plateau
     */
    private BoardConfigEntity getDefaultBoardConfig() {
        return new BoardConfigEntity(8, "1.0");
    }
    
    /**
     * Retourne la configuration par défaut d'une pièce
     */
    private PieceHealthConfigEntity getDefaultPieceHealthConfig(String pieceName) {
        Map<String, Integer> defaults = new HashMap<>();
        defaults.put("KING", 100);
        defaults.put("QUEEN", 90);
        defaults.put("ROOK", 50);
        defaults.put("BISHOP", 40);
        defaults.put("KNIGHT", 30);
        defaults.put("PAWN", 10);
        
        return new PieceHealthConfigEntity(pieceName, defaults.getOrDefault(pieceName, 10), "1.0");
    }
    
    /**
     * Retourne la liste des configurations par défaut des pièces
     */
    private List<PieceHealthConfigEntity> getDefaultPieceHealthConfigs() {
        List<PieceHealthConfigEntity> defaults = new java.util.ArrayList<>();
        defaults.add(new PieceHealthConfigEntity("KING", 100, "1.0"));
        defaults.add(new PieceHealthConfigEntity("QUEEN", 90, "1.0"));
        defaults.add(new PieceHealthConfigEntity("ROOK", 50, "1.0"));
        defaults.add(new PieceHealthConfigEntity("BISHOP", 40, "1.0"));
        defaults.add(new PieceHealthConfigEntity("KNIGHT", 30, "1.0"));
        defaults.add(new PieceHealthConfigEntity("PAWN", 10, "1.0"));
        return defaults;
    }
}
