-- ==========================================================
-- Esquema REAL confirmado de la base de datos del proyecto
-- Torneo Primera Malaga: 8 tablas, base de datos "campeonato_futbol".
--
-- CONFIRMADO el 2026-09-05 ejecutando directamente en MySQL Workbench:
--   SHOW TABLES;
--   SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE,
--          COLUMN_KEY, COLUMN_DEFAULT
--   FROM INFORMATION_SCHEMA.COLUMNS
--   WHERE TABLE_SCHEMA = 'campeonato_futbol'
--   ORDER BY TABLE_NAME, ORDINAL_POSITION;
--
-- Reemplaza la version anterior de este archivo, que contenia varias
-- columnas [supuesto] (adivinadas a partir de archivos .ibd) que
-- resultaron ser incorrectas. Diferencias mas importantes encontradas:
--   - Todas las PK usan el patron id_<tabla> (nunca "id" a secas).
--   - equipo, arbitro y sugerencia SI pertenecen a un campeonato
--     (relacion que no se habia contemplado antes).
--   - goleador se relaciona con partido (no con campeonato).
--   - partido NO tiene relacion con arbitro.
-- ==========================================================

CREATE DATABASE IF NOT EXISTS campeonato_futbol;
USE campeonato_futbol;

CREATE TABLE campeonato (
    id_campeonato INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    sede VARCHAR(100),
    categoria VARCHAR(50)
);

CREATE TABLE equipo (
    id_equipo INT AUTO_INCREMENT PRIMARY KEY,
    id_campeonato INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    ciudad VARCHAR(80),
    dt VARCHAR(100),
    colores VARCHAR(50),
    estadio VARCHAR(100),
    FOREIGN KEY (id_campeonato) REFERENCES campeonato(id_campeonato)
);

CREATE TABLE arbitro (
    id_arbitro INT AUTO_INCREMENT PRIMARY KEY,
    id_campeonato INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    rol VARCHAR(50),
    licencia VARCHAR(30),
    FOREIGN KEY (id_campeonato) REFERENCES campeonato(id_campeonato)
);

CREATE TABLE jugador (
    id_jugador INT AUTO_INCREMENT PRIMARY KEY,
    id_equipo INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    posicion VARCHAR(30),
    numero_camiseta INT,
    fecha_nacimiento DATE,
    FOREIGN KEY (id_equipo) REFERENCES equipo(id_equipo)
);

CREATE TABLE partido (
    id_partido INT AUTO_INCREMENT PRIMARY KEY,
    id_campeonato INT NOT NULL,
    id_equipo_local INT NOT NULL,
    id_equipo_visitante INT NOT NULL,
    fecha DATE,
    hora TIME,
    goles_local INT DEFAULT 0,
    goles_visitante INT DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'Programado',
    FOREIGN KEY (id_campeonato) REFERENCES campeonato(id_campeonato),
    FOREIGN KEY (id_equipo_local) REFERENCES equipo(id_equipo),
    FOREIGN KEY (id_equipo_visitante) REFERENCES equipo(id_equipo)
);

CREATE TABLE goleador (
    id_goleador INT AUTO_INCREMENT PRIMARY KEY,
    id_jugador INT NOT NULL,
    id_partido INT NOT NULL,
    goles INT DEFAULT 1,
    FOREIGN KEY (id_jugador) REFERENCES jugador(id_jugador),
    FOREIGN KEY (id_partido) REFERENCES partido(id_partido)
);

CREATE TABLE tabla_posiciones (
    id_posicion INT AUTO_INCREMENT PRIMARY KEY,
    id_campeonato INT NOT NULL,
    id_equipo INT NOT NULL,
    puntos INT DEFAULT 0,
    partidos_jugados INT DEFAULT 0,
    partidos_ganados INT DEFAULT 0,
    partidos_empatados INT DEFAULT 0,
    partidos_perdidos INT DEFAULT 0,
    goles_favor INT DEFAULT 0,
    goles_contra INT DEFAULT 0,
    FOREIGN KEY (id_campeonato) REFERENCES campeonato(id_campeonato),
    FOREIGN KEY (id_equipo) REFERENCES equipo(id_equipo)
);

CREATE TABLE sugerencia (
    id_sugerencia INT AUTO_INCREMENT PRIMARY KEY,
    id_campeonato INT NOT NULL,
    nombre_usuario VARCHAR(100),
    mensaje TEXT NOT NULL,
    fecha_envio DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_campeonato) REFERENCES campeonato(id_campeonato)
);
