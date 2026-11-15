package br.com.fiap.ms.avaliacoes.dto;

import br.com.fiap.ms.avaliacoes.entities.Avaliacao;
import br.com.fiap.ms.avaliacoes.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AvaliacaoResponseDTO {

    private Long id;
    private String nomeUsuario;
    private String tipoDeficiencia;
    private Integer nota;
    private String comentario;
    private String barreiraEncontrada;
    private LocalDate dataAvaliacao;
    private Long localId;
    private Status status;

    public AvaliacaoResponseDTO(Avaliacao avaliacao) {
        id = avaliacao.getId();
        nomeUsuario = avaliacao.getNomeUsuario();
        tipoDeficiencia = avaliacao.getTipoDeficiencia();
        nota = avaliacao.getNota();
        comentario = avaliacao.getComentario();
        barreiraEncontrada = avaliacao.getBarreiraEncontrada();
        dataAvaliacao = avaliacao.getDataAvaliacao();
        localId = avaliacao.getLocalId();
        status = avaliacao.getStatus();
    }
}