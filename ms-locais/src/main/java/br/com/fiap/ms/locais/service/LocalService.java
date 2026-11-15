package br.com.fiap.ms.locais.service;

import br.com.fiap.ms.locais.dto.LocalRequestDTO;
import br.com.fiap.ms.locais.dto.LocalResponseDTO;
import br.com.fiap.ms.locais.dto.RecursoAcessibilidadeRequestDTO;
import br.com.fiap.ms.locais.entities.Local;
import br.com.fiap.ms.locais.entities.RecursoAcessibilidade;
import br.com.fiap.ms.locais.entities.Status;
import br.com.fiap.ms.locais.exceptions.LocalVerificadoException;
import br.com.fiap.ms.locais.exceptions.ResourceNotFoundException;
import br.com.fiap.ms.locais.repository.LocalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocalService {

    @Autowired
    private LocalRepository localRepository;

    @Transactional(readOnly = true)
    public List<LocalResponseDTO> findAllLocais(){

        return localRepository.findAll()
                .stream().map(LocalResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public LocalResponseDTO findLocalById(Long id){

        Local local = localRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );
        return new LocalResponseDTO(local);
    }

    @Transactional
    public LocalResponseDTO saveLocal(LocalRequestDTO requestDTO){

        Local local = new Local();
        local.setStatus(Status.CADASTRADO);
        mapDtoToLocal(requestDTO, local);
        local = localRepository.save(local);
        return new LocalResponseDTO(local);
    }

    @Transactional
    public LocalResponseDTO updateLocal(Long id, LocalRequestDTO requestDTO){

        Local local = localRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        if (local.getStatus().equals(Status.VERIFICADO)){
            throw new LocalVerificadoException(
                    String.format("Local id: %d já está VERIFICADO e não pode ser alterado.", id)
            );
        }

        local.getRecursos().clear();
        local.setStatus(Status.CADASTRADO);
        mapDtoToLocal(requestDTO, local);
        local = localRepository.save(local);
        return new LocalResponseDTO(local);
    }

    @Transactional
    public void deleteLocalById(Long id){

        if(!localRepository.existsById(id)){
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }

        localRepository.deleteById(id);
    }

    // chamado pelo ms-avaliacoes (OpenFeign) quando uma avaliação é validada
    @Transactional
    public void confirmarAvaliacao(Long id){

        Local local = localRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Local não encontrado. ID: " + id)
        );

        local.setStatus(Status.VERIFICADO);
        localRepository.save(local);
    }

    private void mapDtoToLocal(LocalRequestDTO requestDTO, Local local) {

        local.setNome(requestDTO.getNome());
        local.setEndereco(requestDTO.getEndereco());
        local.setCidade(requestDTO.getCidade());
        local.setTipoLocal(requestDTO.getTipoLocal());

        for (RecursoAcessibilidadeRequestDTO recursoDTO : requestDTO.getRecursos()){

            RecursoAcessibilidade recurso = new RecursoAcessibilidade();
            recurso.setDescricao(recursoDTO.getDescricao());
            recurso.setTipoDeficiencia(recursoDTO.getTipoDeficiencia());
            recurso.setDisponivel(recursoDTO.getDisponivel());
            recurso.setLocalizacao(recursoDTO.getLocalizacao());
            recurso.setObservacao(recursoDTO.getObservacao());
            recurso.setLocal(local);
            local.getRecursos().add(recurso);
        }
    }
}
