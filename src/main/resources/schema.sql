-- Šema baze podataka za katalog video igara
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS esrb_fact;
DROP TABLE IF EXISTS game_platform;
DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS platform;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS app_user;
DROP TABLE IF EXISTS admin;

CREATE TABLE admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    address VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE platform (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE game (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    image LONGBLOB,
    image_content_type VARCHAR(100),
    publisher VARCHAR(150) NOT NULL,
    release_year INT NOT NULL,
    description TEXT,
    esrb_rating VARCHAR(20) NOT NULL,
    for_sale BOOLEAN NOT NULL DEFAULT FALSE,
    price DECIMAL(10,2),
    category_id BIGINT NOT NULL,
    CONSTRAINT fk_game_category FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT chk_release_year CHECK (release_year BETWEEN 1970 AND 2100),
    CONSTRAINT chk_price_nonneg CHECK (price IS NULL OR price >= 0)
);

CREATE TABLE game_platform (
    game_id BIGINT NOT NULL,
    platform_id BIGINT NOT NULL,
    PRIMARY KEY (game_id, platform_id),
    CONSTRAINT fk_gp_game FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE,
    CONSTRAINT fk_gp_platform FOREIGN KEY (platform_id) REFERENCES platform(id)
);

CREATE TABLE esrb_fact (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    text VARCHAR(500) NOT NULL,
    CONSTRAINT fk_fact_game FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE
);

CREATE TABLE review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    rejection_reason VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_game FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_game UNIQUE (user_id, game_id),
    CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5)
);
