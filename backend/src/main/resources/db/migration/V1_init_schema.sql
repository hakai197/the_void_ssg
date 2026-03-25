-- V1__init_schema.sql
-- The Void SSG Database Schema

CREATE TABLE IF NOT EXISTS sites (
                                     id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    output_path VARCHAR(500),
    entropy_mode VARCHAR(50) NOT NULL DEFAULT 'DAILY',
    sanity_threshold INT DEFAULT 50,
    corruption_intensity INT DEFAULT 30,
    build_hash VARCHAR(64),
    entity_ward VARCHAR(255),
    last_built TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_entropy_mode (entropy_mode),
    INDEX idx_last_built (last_built)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS entries (
                                       id VARCHAR(36) PRIMARY KEY,
    site_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content LONGTEXT,
    slug VARCHAR(255) NOT NULL,
    corruption_level INT DEFAULT 0,
    corruption_pattern JSON,
    entity_influence VARCHAR(100),
    requires_ritual BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_corrupted TIMESTAMP,
    version INT DEFAULT 0,
    FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE,
    UNIQUE KEY uk_site_slug (site_id, slug),
    INDEX idx_corruption (corruption_level),
    INDEX idx_entity (entity_influence),
    INDEX idx_requires_ritual (requires_ritual)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS build_logs (
                                          id VARCHAR(36) PRIMARY KEY,
    site_id VARCHAR(36) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    narrative LONGTEXT,
    entity_detections INT DEFAULT 0,
    corrupted_entries INT DEFAULT 0,
    build_duration_ms BIGINT,
    build_successful BOOLEAN DEFAULT TRUE,
    warnings_count INT DEFAULT 0,
    whispers_generated INT DEFAULT 0,
    FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE,
    INDEX idx_timestamp (timestamp),
    INDEX idx_build_successful (build_successful)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS visitors (
                                        id VARCHAR(36) PRIMARY KEY,
    site_id VARCHAR(36) NOT NULL,
    user_hash VARCHAR(255) NOT NULL,
    visit_count INT DEFAULT 1,
    last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE,
    UNIQUE KEY uk_site_user (site_id, user_hash),
    INDEX idx_last_seen (last_seen)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;