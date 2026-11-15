package br.com.fiap.ms.avaliacoes.repository;

import br.com.fiap.ms.avaliacoes.entities.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
}
