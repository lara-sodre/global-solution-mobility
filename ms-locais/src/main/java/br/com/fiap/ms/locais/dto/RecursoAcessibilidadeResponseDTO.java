package br.com.fiap.ms.locais.dto;

import br.com.fiap.ms.locais.entities.RecursoAcessibilidade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecursoAcessibilidadeResponseDTO {

    private Long id;

    private String descricao;
    private String tipoDeficiencia;
    private Boolean disponivel;
    private String localizacao;
    private String observacao;

    public RecursoAcessibilidadeResponseDTO(RecursoAcessibilidade recurso) {
        id = recurso.getId();
        descricao = recurso.getDescricao();
        tipoDeficiencia = recurso.getTipoDeficiencia();
        disponivel = recurso.getDisponivel();
        localizacao = recurso.getLocalizacao();
        observacao = recurso.getObservacao();
    }
}
