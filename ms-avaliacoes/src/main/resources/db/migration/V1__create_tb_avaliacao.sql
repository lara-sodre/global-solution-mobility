CREATE TABLE IF NOT EXISTS tb_avaliacao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome_usuario VARCHAR(100) NOT NULL,
    tipo_deficiencia VARCHAR(20) NOT NULL,
    nota INT NOT NULL,
    comentario VARCHAR(500) NOT NULL,
    barreira_encontrada VARCHAR(200) NULL,
    data_avaliacao DATE NOT NULL,
    local_id BIGINT NOT NULL,
    status VARCHAR(35) NOT NULL,
    CONSTRAINT pk_tb_avaliacao PRIMARY KEY (id)
);
