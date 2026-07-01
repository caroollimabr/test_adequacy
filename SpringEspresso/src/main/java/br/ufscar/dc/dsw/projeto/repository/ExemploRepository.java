package br.ufscar.dc.dsw.projeto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufscar.dc.dsw.projeto.model.ExemploModel;

public interface ExemploRepository extends JpaRepository<ExemploModel, Long>{
    
}
