package br.com.fiap.ms.locais.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecursoAcessibilidadeRequestDTO {

    @NotBlank(message = "Descrição do recurso é requerida")
    @Size(min = 3, max = 150, message = "A descrição deve ter entre 3 e 150 caracteres")
    private String descricao;

    @NotBlank(message = "Tipo de deficiência é requerido")
    @Pattern(regexp = "FISICA|VISUAL|AUDITIVA|INTELECTUAL",
            message = "Tipo de deficiência deve ser FISICA, VISUAL, AUDITIVA ou INTELECTUAL")
    private String tipoDeficiencia;

    @NotNull(message = "Disponibilidade do recurso é requerida")
    private Boolean disponivel;

    @NotBlank(message = "Localização do recurso é requerida")
    @Size(min = 3, max = 100, message = "A localização deve ter entre 3 e 100 caracteres")
    private String localizacao;

    @Size(max = 200, message = "A observação deve ter no máximo 200 caracteres")
    private String observacao;

}