package br.ufscar.dc.dsw.projeto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.ufscar.dc.dsw.projeto.model.EstrategiaModel;

public interface EstrategiaRepository extends JpaRepository<EstrategiaModel, Long> {
}
