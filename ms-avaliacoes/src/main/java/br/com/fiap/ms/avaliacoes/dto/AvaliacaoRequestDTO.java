package br.com.fiap.ms.avaliacoes.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class AvaliacaoRequestDTO {

    @NotBlank(message = "O campo nome do usuário é obrigatório")
    @Size(min = 3, max = 100, message = "O campo nome do usuário deve ter entre 3 e 100 caracteres")
    private String nomeUsuario;

    @NotBlank(message = "O campo tipo de deficiência é obrigatório")
    @Pattern(regexp = "FISICA|VISUAL|AUDITIVA|INTELECTUAL",
            message = "O campo tipo de deficiência deve ser FISICA, VISUAL, AUDITIVA ou INTELECTUAL")
    private String tipoDeficiencia;

    @NotNull(message = "O campo nota é obrigatório")
    @Min(value = 1, message = "O campo nota deve ser no mínimo 1")
    @Max(value = 5, message = "O campo nota deve ser no máximo 5")
    private Integer nota;

    @NotBlank(message = "O campo comentário é obrigatório")
    @Size(min = 5, max = 500, message = "O campo comentário deve ter entre 5 e 500 caracteres")
    private String comentario;

    @Size(max = 200, message = "O campo barreira encontrada deve ter no máximo 200 caracteres")
    private String barreiraEncontrada;

    @NotNull(message = "O campo local id é obrigatório")
    private Long localId;

}