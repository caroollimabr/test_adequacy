package br.ufscar.dc.dsw.projeto.repository;

import br.ufscar.dc.dsw.projeto.model.DicaModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DicaRepository extends JpaRepository<DicaModel, Long> {
}