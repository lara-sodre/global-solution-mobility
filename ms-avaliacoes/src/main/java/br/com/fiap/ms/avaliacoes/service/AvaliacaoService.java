package br.com.fiap.ms.avaliacoes.service;

import br.com.fiap.ms.avaliacoes.client.LocalClient;
import br.com.fiap.ms.avaliacoes.config.ResilienceConfig;
import br.com.fiap.ms.avaliacoes.dto.AvaliacaoRequestDTO;
import br.com.fiap.ms.avaliacoes.dto.AvaliacaoResponseDTO;
import br.com.fiap.ms.avaliacoes.entities.Avaliacao;
import br.com.fiap.ms.avaliacoes.entities.Status;
import br.com.fiap.ms.avaliacoes.exceptions.AvaliacaoValidadaException;
import br.com.fiap.ms.avaliacoes.exceptions.ResourceNotFoundException;
import br.com.fiap.ms.avaliacoes.repository.AvaliacaoRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private LocalClient localClient;

    @Autowired
    private CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    @Transactional(readOnly = true)
    public List<AvaliacaoResponseDTO> findAllAvaliacoes() {

        return avaliacaoRepository.findAll()
                .stream()
                .map(AvaliacaoResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponseDTO findAvaliacaoById(Long id) {

        Avaliacao avaliacao = avaliacaoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        return new AvaliacaoResponseDTO(avaliacao);
    }

    @Transactional
    public AvaliacaoResponseDTO saveAvaliacao(AvaliacaoRequestDTO requestDTO) {

        Avaliacao avaliacao = new Avaliacao();
        mapDtoToAvaliacao(requestDTO, avaliacao);
        avaliacao.setDataAvaliacao(LocalDate.now());
        avaliacao.setStatus(Status.REGISTRADA);
        avaliacao = avaliacaoRepository.save(avaliacao);
        return new AvaliacaoResponseDTO(avaliacao);
    }

    @Transactional
    public AvaliacaoResponseDTO updateAvaliacao(Long id, AvaliacaoRequestDTO requestDTO) {

        Avaliacao avaliacao = avaliacaoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        if (avaliacao.getStatus().equals(Status.VALIDADA)) {
            throw new AvaliacaoValidadaException(
                    String.format("Avaliação id %d já está VALIDADA e não pode ser alterada", id)
            );
        }

        mapDtoToAvaliacao(requestDTO, avaliacao);
        avaliacao.setDataAvaliacao(LocalDate.now());
        avaliacao.setStatus(Status.REGISTRADA);
        avaliacao = avaliacaoRepository.save(avaliacao);
        return new AvaliacaoResponseDTO(avaliacao);
    }

    @Transactional
    public void deleteAvaliacao(Long id) {

        if (!avaliacaoRepository.existsById(id)) {

            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }

        avaliacaoRepository.deleteById(id);
    }


    @Transactional
    public AvaliacaoResponseDTO validarAvaliacaoDoLocal(Long id) {

        Avaliacao avaliacao = avaliacaoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        if (avaliacao.getStatus().equals(Status.VALIDADA)) {
            return new AvaliacaoResponseDTO(avaliacao);
        }

        Long localId = avaliacao.getLocalId();

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(ResilienceConfig.ATUALIZAR_LOCAL);

        Boolean localAtualizado = circuitBreaker.run(
                () -> {
                    try {
                        localClient.confirmarAvaliacao(localId);
                        return Boolean.TRUE;
                    } catch (FeignException.NotFound e) {
                        throw new ResourceNotFoundException("Local não encontrado. ID: " + localId);
                    }
                },
                throwable -> fallbackValidarAvaliacao(id, throwable)
        );

        avaliacao.setStatus(localAtualizado ? Status.VALIDADA : Status.VALIDACAO_PENDENTE);
        avaliacao = avaliacaoRepository.save(avaliacao);
        return new AvaliacaoResponseDTO(avaliacao);
    }

    private Boolean fallbackValidarAvaliacao(Long id, Throwable throwable) {

        Throwable causa = throwable;
        while (causa != null) {
            if (causa instanceof ResourceNotFoundException notFound) {
                throw notFound; // erro de negócio: segue para o handler (404)
            }
            causa = causa.getCause();
        }

        log.error("Falha ao validar avaliação {}. Ativando fallback. Erro: {}", id, throwable.getMessage());

        return Boolean.FALSE;
    }

    private void mapDtoToAvaliacao(AvaliacaoRequestDTO requestDTO, Avaliacao avaliacao) {

        avaliacao.setNomeUsuario(requestDTO.getNomeUsuario());
        avaliacao.setTipoDeficiencia(requestDTO.getTipoDeficiencia());
        avaliacao.setNota(requestDTO.getNota());
        avaliacao.setComentario(requestDTO.getComentario());
        avaliacao.setBarreiraEncontrada(requestDTO.getBarreiraEncontrada());
        avaliacao.setLocalId(requestDTO.getLocalId());
    }

}
