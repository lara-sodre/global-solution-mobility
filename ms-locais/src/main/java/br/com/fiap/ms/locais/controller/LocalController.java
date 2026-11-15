package br.com.fiap.ms.locais.controller;

import br.com.fiap.ms.locais.dto.LocalRequestDTO;
import br.com.fiap.ms.locais.dto.LocalResponseDTO;
import br.com.fiap.ms.locais.service.LocalService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/locais")
public class LocalController {

    @Autowired
    private LocalService localService;

    @Operation(summary = "Lista todos os locais")
    @GetMapping
    public ResponseEntity<List<LocalResponseDTO>> getAll(){

        List<LocalResponseDTO> list = localService.findAllLocais();

        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Busca um local por ID")
    @GetMapping("/{id}")
    public ResponseEntity<LocalResponseDTO> getLocal(@PathVariable Long id){

        LocalResponseDTO localDTO = localService.findLocalById(id);
        return ResponseEntity.ok(localDTO);
    }

    @Operation(summary = "Cadastra um novo local")
    @PostMapping
    public ResponseEntity<LocalResponseDTO> saveLocal(@RequestBody @Valid
                                                      LocalRequestDTO requestDTO){

        LocalResponseDTO responseDTO = localService.saveLocal(requestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(responseDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @Operation(summary = "Atualiza um local (não permitido se já VERIFICADO)")
    @PutMapping("/{id}")
    public ResponseEntity<LocalResponseDTO> updateLocal(@PathVariable Long id,
                                                        @Valid @RequestBody LocalRequestDTO requestDTO){

        LocalResponseDTO responseDTO = localService.updateLocal(id, requestDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Marca o local como VERIFICADO (chamado pelo ms-avaliacoes via OpenFeign)")
    @PutMapping("/{localId}/avaliacao/validada")
    public ResponseEntity<Void> confirmarAvaliacao(@PathVariable Long localId){

        localService.confirmarAvaliacao(localId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Exclui um local")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocal(@PathVariable Long id){

        localService.deleteLocalById(id);

        return ResponseEntity.noContent().build();
    }
}
