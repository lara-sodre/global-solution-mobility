package br.com.fiap.ms.locais.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LocalRequestDTO {

    @NotBlank(message = "Nome é requerido")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Endereço é requerido")
    @Size(min = 5, max = 200, message = "O endereço deve ter entre 5 e 200 caracteres")
    private String endereco;

    @NotBlank(message = "Cidade é requerida")
    @Size(min = 2, max = 80, message = "A cidade deve ter entre 2 e 80 caracteres")
    private String cidade;

    @NotBlank(message = "Tipo do local é requerido")
    @Size(min = 3, max = 50, message = "O tipo do local deve ter entre 3 e 50 caracteres")
    private String tipoLocal;

    @NotEmpty(message = "Local deve ter pelo menos um recurso de acessibilidade")
    private List<@Valid RecursoAcessibilidadeRequestDTO> recursos = new ArrayList<>();

}
