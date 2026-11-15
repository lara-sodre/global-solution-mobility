package br.com.fiap.ms.locais.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_local")
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 200)
    private String endereco;

    @Column(nullable = false, length = 80)
    private String cidade;

    @Column(nullable = false, length = 50)
    private String tipoLocal;

    @OneToMany(mappedBy = "local",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecursoAcessibilidade> recursos = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
}
