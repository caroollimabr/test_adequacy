package br.ufscar.dc.dsw.projeto.service;

import br.ufscar.dc.dsw.projeto.model.BugModel;
import br.ufscar.dc.dsw.projeto.model.SessaoModel;
import br.ufscar.dc.dsw.projeto.model.StatusSessao;
import br.ufscar.dc.dsw.projeto.repository.SessaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = false)
public class SessaoService {

    @Autowired
    private SessaoRepository sessaoRepository;

    public List<SessaoModel> buscarPorProjetoEEstrategia(Long projetoId, Long estrategiaId) {
        return sessaoRepository.findByProjetoIdAndEstrategiaId(projetoId, estrategiaId);
    }

    @Transactional(readOnly = true)
    public SessaoModel buscarPorId(Long id) {
        return sessaoRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public SessaoModel buscarPorIdComBugs(Long id) {
        return sessaoRepository.findByIdWithBugs(id).orElse(null);
    }

    public SessaoModel salvar(SessaoModel sessao) {
        return sessaoRepository.save(sessao);
    }

    @Transactional
    public void excluirSessao(Long id) {
        SessaoModel bug = sessaoRepository.findById(id).orElseThrow(() -> 
                new RuntimeException("Sessão não encontrada: " + id));
        sessaoRepository.delete(bug); // Depois de removido da sessão, pode deletar

    }

    public Map<Long, Long> contarSessoesPorEstrategia(Long projetoId, StatusSessao status) {
        Map<Long, Long> resultado = new HashMap<>();
        
        try {
            List<Object[]> contagens = sessaoRepository.countSessoesPorEstrategiaAndStatus(projetoId, status);
            
            for (Object[] linha : contagens) {
                Long estrategiaId = (Long) linha[0];
                Long count = (Long) linha[1];
                resultado.put(estrategiaId, count);
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao contar sessões por estratégia: " + e.getMessage());
        }
        
        return resultado;
    }
}
