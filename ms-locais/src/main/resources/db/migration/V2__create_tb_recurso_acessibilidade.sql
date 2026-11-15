CREATE TABLE IF NOT EXISTS tb_recurso_acessibilidade (
    id BIGINT NOT NULL AUTO_INCREMENT,
    descricao VARCHAR(150) NOT NULL,
    tipo_deficiencia VARCHAR(20) NOT NULL,
    disponivel BOOLEAN NOT NULL,
    localizacao VARCHAR(100) NOT NULL,
    observacao VARCHAR(200) NULL,
    local_id BIGINT NOT NULL,
    CONSTRAINT pk_tb_recurso_acessibilidade PRIMARY KEY (id),
    CONSTRAINT fk_tb_recurso_acessibilidade_local
        FOREIGN KEY (local_id)
        REFERENCES tb_local(id)
);
