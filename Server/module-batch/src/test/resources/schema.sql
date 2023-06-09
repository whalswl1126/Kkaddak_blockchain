CREATE SCHEMA IF NOT EXISTS `coredb`;
USE `coredb`;
DROP TABLE IF EXISTS `member`;

CREATE TABLE IF NOT EXISTS `music` (
   `id` int NOT NULL AUTO_INCREMENT,
   PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `member` (
    `id` int NOT NULL AUTO_INCREMENT,
    `email` varchar(255) DEFAULT NULL,
    `nickname` varchar(255) DEFAULT NULL,
    `profile_path` varchar(255) DEFAULT NULL,
    `uuid` char(36) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE (`nickname`)
);