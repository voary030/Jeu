package com.echecpong.config.rest;

import com.echecpong.config.ejb.ConfigServiceBean;
import com.echecpong.config.entity.BoardConfigEntity;
import com.echecpong.config.entity.PieceHealthConfigEntity;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API pour accéder aux configurations
 */
@Path("/config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigResource {
    
    @EJB
    private ConfigServiceBean configService;
    
    /**
     * GET /config/board
     * Récupère la configuration du plateau
     */
    @GET
    @Path("/board")
    public Response getBoardConfig() {
        try {
            BoardConfigEntity config = configService.getBoardConfig();
            Map<String, Object> response = new HashMap<>();
            response.put("numberOfPawns", config.getNumberOfPawns());
            response.put("configVersion", config.getConfigVersion());
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse(e.getMessage())).build();
        }
    }
    
    /**
     * PUT /config/board
     * Sauvegarde la configuration du plateau
     */
    @PUT
    @Path("/board")
    public Response updateBoardConfig(BoardConfigEntity config) {
        try {
            config.setConfigVersion("1.0");
            BoardConfigEntity saved = configService.saveBoardConfig(config);
            return Response.ok(saved).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse(e.getMessage())).build();
        }
    }
    
    /**
     * GET /config/pieces
     * Récupère toutes les configurations des pièces
     */
    @GET
    @Path("/pieces")
    public Response getAllPieceHealthConfig() {
        try {
            List<PieceHealthConfigEntity> configs = configService.getAllPieceHealthConfig();
            Map<String, Integer> response = new HashMap<>();
            for (PieceHealthConfigEntity config : configs) {
                response.put(config.getPieceName(), config.getHealthPoints());
            }
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse(e.getMessage())).build();
        }
    }
    
    /**
     * GET /config/pieces/{pieceName}
     * Récupère la configuration de santé pour une pièce spécifique
     */
    @GET
    @Path("/pieces/{pieceName}")
    public Response getPieceHealthConfig(@PathParam("pieceName") String pieceName) {
        try {
            PieceHealthConfigEntity config = configService.getPieceHealthConfig(pieceName);
            Map<String, Object> response = new HashMap<>();
            response.put("pieceName", config.getPieceName());
            response.put("healthPoints", config.getHealthPoints());
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse(e.getMessage())).build();
        }
    }
    
    /**
     * PUT /config/pieces/{pieceName}
     * Met à jour la configuration de santé d'une pièce
     */
    @PUT
    @Path("/pieces/{pieceName}")
    public Response updatePieceHealthConfig(@PathParam("pieceName") String pieceName, 
                                           PieceHealthConfigEntity config) {
        try {
            config.setPieceName(pieceName);
            config.setConfigVersion("1.0");
            PieceHealthConfigEntity saved = configService.savePieceHealthConfig(config);
            return Response.ok(saved).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse(e.getMessage())).build();
        }
    }
    
    /**
     * GET /config/health
     * Contrôle de santé du service
     */
    @GET
    @Path("/health")
    public Response health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "ConfigService");
        return Response.ok(response).build();
    }
    
    /**
     * Crée une réponse d'erreur JSON
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return error;
    }
}
