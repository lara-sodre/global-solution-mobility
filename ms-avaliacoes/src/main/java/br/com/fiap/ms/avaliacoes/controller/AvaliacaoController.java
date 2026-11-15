package br.com.fiap.ms.avaliacoes.controller;

import br.com.fiap.ms.avaliacoes.dto.AvaliacaoRequestDTO;
import br.com.fiap.ms.avaliacoes.dto.AvaliacaoResponseDTO;
import br.com.fiap.ms.avaliacoes.entities.Status;
import br.com.fiap.ms.avaliacoes.service.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Operation(summary = "Lista todas as avaliações")
    @GetMapping
    public ResponseEntity<List<AvaliacaoResponseDTO>> getAllAvaliacoes() {

        List<AvaliacaoResponseDTO> list = avaliacaoService.findAllAvaliacoes();

        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Busca uma avaliação por ID")
    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> getAvaliacaoById(@PathVariable Long id) {

        AvaliacaoResponseDTO avaliacaoDTO = avaliacaoService.findAvaliacaoById(id);

        return ResponseEntity.ok(avaliacaoDTO);
    }

    @Operation(summary = "Registra uma nova avaliação")
    @PostMapping
    public ResponseEntity<AvaliacaoResponseDTO> saveAvaliacao(@RequestBody @Valid AvaliacaoRequestDTO requestDTO) {

        AvaliacaoResponseDTO responseDTO = avaliacaoService.saveAvaliacao(requestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(responseDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @Operation(summary = "Atualiza uma avaliação (não permitido se já VALIDADA)")
    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> updateAvaliacao(@PathVariable Long id,
                                                                @Valid @RequestBody AvaliacaoRequestDTO requestDTO) {

        AvaliacaoResponseDTO responseDTO = avaliacaoService.updateAvaliacao(id, requestDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Valida a avaliação e atualiza o status do local no ms-locais via OpenFeign")
    @PatchMapping("/{id}/validar")
    public ResponseEntity<AvaliacaoResponseDTO> validarAvaliacaoDoLocal(@PathVariable
                                                                        @NotNull Long id) {

        AvaliacaoResponseDTO dto = avaliacaoService.validarAvaliacaoDoLocal(id);

        if (dto.getStatus().equals(Status.VALIDACAO_PENDENTE)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(dto);
        }

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Exclui uma avaliação")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        avaliacaoService.deleteAvaliacao(id);

        return ResponseEntity.noContent().build();
    }
}