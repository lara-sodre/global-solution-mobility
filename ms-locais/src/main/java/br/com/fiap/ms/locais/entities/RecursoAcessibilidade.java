package br.com.fiap.ms.locais.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_recurso_acessibilidade")
public class RecursoAcessibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Column(nullable = false, length = 20)
    private String tipoDeficiencia;

    @Column(nullable = false)
    private Boolean disponivel;

    @Column(nullable = false, length = 100)
    private String localizacao;

    @Column(length = 200)
    private String observacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "local_id")
    private Local local;
}
