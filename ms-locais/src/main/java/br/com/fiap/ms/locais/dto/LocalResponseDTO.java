package br.com.fiap.ms.locais.dto;

import br.com.fiap.ms.locais.entities.Local;
import br.com.fiap.ms.locais.entities.RecursoAcessibilidade;
import br.com.fiap.ms.locais.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LocalResponseDTO {

    private Long id;

    private String nome;
    private String endereco;
    private String cidade;
    private String tipoLocal;

    private List<RecursoAcessibilidadeResponseDTO> recursos = new ArrayList<>();

    private Status status;

    public LocalResponseDTO(Local local) {
        id = local.getId();
        nome = local.getNome();
        endereco = local.getEndereco();
        cidade = local.getCidade();
        tipoLocal = local.getTipoLocal();

        for (RecursoAcessibilidade recurso : local.getRecursos()) {

            RecursoAcessibilidadeResponseDTO recursoDTO = new RecursoAcessibilidadeResponseDTO(recurso);
            recursos.add(recursoDTO);
        }

        status = local.getStatus();
    }
}