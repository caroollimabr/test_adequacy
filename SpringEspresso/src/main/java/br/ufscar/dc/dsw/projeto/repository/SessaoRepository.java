package br.ufscar.dc.dsw.projeto.repository;

import br.ufscar.dc.dsw.projeto.model.SessaoModel;
import br.ufscar.dc.dsw.projeto.model.StatusSessao;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessaoRepository extends JpaRepository<SessaoModel, Long> {

    List<SessaoModel> findByProjetoIdAndEstrategiaId(Long projetoId, Long estrategiaId);

    @Query("SELECT s FROM SessaoModel s LEFT JOIN FETCH s.bugs WHERE s.id = :id")
    Optional<SessaoModel> findByIdWithBugs(@Param("id") Long id);

    @Query("SELECT s.estrategia.id, COUNT(s) FROM SessaoModel s WHERE s.projeto.id = :projetoId AND s.status = :status GROUP BY s.estrategia.id")
    List<Object[]> countSessoesPorEstrategiaAndStatus(@Param("projetoId") Long projetoId, @Param("status") StatusSessao status);

    List<SessaoModel> findByStatus(StatusSessao status);
}
