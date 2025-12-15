-- Script d'initialisation de la base de données ConfigService
-- Exécutez ce script après le premier déploiement

-- Création de la table board_config
CREATE TABLE IF NOT EXISTS board_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    number_of_pawns INT NOT NULL DEFAULT 8,
    config_version VARCHAR(50) NOT NULL DEFAULT '1.0',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Création de la table piece_health_config
CREATE TABLE IF NOT EXISTS piece_health_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    piece_name VARCHAR(50) NOT NULL UNIQUE,
    health_points INT NOT NULL,
    config_version VARCHAR(50) NOT NULL DEFAULT '1.0',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Données initiales pour board_config
INSERT INTO board_config (number_of_pawns, config_version) 
VALUES (8, '1.0')
ON DUPLICATE KEY UPDATE id=id;

-- Données initiales pour piece_health_config
INSERT INTO piece_health_config (piece_name, health_points, config_version) VALUES
('KING', 100, '1.0'),
('QUEEN', 90, '1.0'),
('ROOK', 50, '1.0'),
('BISHOP', 40, '1.0'),
('KNIGHT', 30, '1.0'),
('PAWN', 10, '1.0')
ON DUPLICATE KEY UPDATE health_points=VALUES(health_points);

-- Afficher les données
SELECT 'Board Configuration:' AS info;
SELECT * FROM board_config;

SELECT 'Piece Health Configuration:' AS info;
SELECT * FROM piece_health_config;
