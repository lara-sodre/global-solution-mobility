INSERT INTO tb_recurso_acessibilidade(descricao, tipo_deficiencia, disponivel, localizacao, observacao, local_id) VALUES
    ('Rampa de acesso', 'FISICA', TRUE, 'Entrada principal', 'Inclinação adequada e corrimão dos dois lados', 1),
    ('Cardápio em braille', 'VISUAL', FALSE, 'Salão', 'Em falta no momento', 1),
    ('Atendimento em Libras', 'AUDITIVA', TRUE, 'Balcão de atendimento', NULL, 1),
    ('Elevador acessível', 'FISICA', TRUE, 'Hall central', 'Botoeira em braille', 2),
    ('Piso tátil direcional', 'VISUAL', TRUE, 'Da entrada ao balcão', NULL, 2),
    ('Sinalização com pictogramas', 'INTELECTUAL', TRUE, 'Todos os andares', NULL, 2),
    ('Calçada nivelada e sem degraus', 'FISICA', TRUE, 'Entorno do parque', NULL, 3),
    ('Sinalização sonora nas travessias', 'VISUAL', TRUE, 'Travessias da avenida', NULL, 3),
    ('Placas informativas com apoio visual', 'AUDITIVA', TRUE, 'Entrada e trilhas', NULL, 3);
