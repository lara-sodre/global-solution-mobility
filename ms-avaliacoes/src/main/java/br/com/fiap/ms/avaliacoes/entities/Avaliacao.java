package br.com.fiap.ms.avaliacoes.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nomeUsuario;

    @Column(nullable = false, length = 20)
    private String tipoDeficiencia;

    @Column(nullable = false)
    private Integer nota;

    @Column(nullable = false, length = 500)
    private String comentario;

    @Column(length = 200)
    private String barreiraEncontrada;

    @Column(nullable = false)
    private LocalDate dataAvaliacao;

    @Column(nullable = false)
    private Long localId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
}
