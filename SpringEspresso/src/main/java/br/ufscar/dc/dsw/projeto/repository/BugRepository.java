package br.ufscar.dc.dsw.projeto.repository;

import br.ufscar.dc.dsw.projeto.model.BugModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<BugModel, Long> {
    
    List<BugModel> findBySessaoId(Long sessaoId);

    @Query("""
        SELECT b FROM BugModel b
        JOIN FETCH b.sessao s
        JOIN FETCH s.projeto p
        JOIN FETCH s.tester t
        ORDER BY p.nome ASC, s.id ASC, b.dataRegistro ASC
    """)
    List<BugModel> findAllOrdered();

}
