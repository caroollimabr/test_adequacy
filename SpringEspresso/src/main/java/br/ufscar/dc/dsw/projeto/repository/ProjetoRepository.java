package br.ufscar.dc.dsw.projeto.repository;
import br.ufscar.dc.dsw.projeto.model.ProjetoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjetoRepository extends JpaRepository<ProjetoModel, Long> {
    
    // BUSCAR OS PROJETOS QUE UM MEMBRO PARTICIPA (POR EMAIL)
    @Query("SELECT DISTINCT p FROM ProjetoModel p JOIN p.membros m WHERE m.email = :email")
    List<ProjetoModel> findByMembrosEmail(@Param("email") String email);
    
}