CREATE TABLE cliente (
    id_instalacao BIGSERIAL PRIMARY KEY,
    conta_contrato VARCHAR(100),
    numero_serie VARCHAR(100),
    numero_poste VARCHAR(100),
    nome_cliente VARCHAR(200),
    longitude DOUBLE PRECISION,
    latitude DOUBLE PRECISION
);