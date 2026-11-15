package br.com.fiap.ms.locais.repository;

import br.com.fiap.ms.locais.entities.Local;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalRepository extends JpaRepository<Local, Long> {
}
