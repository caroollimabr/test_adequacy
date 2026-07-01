package br.ufscar.dc.dsw.projeto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.ufscar.dc.dsw.projeto.model.EstrategiaModel;
import br.ufscar.dc.dsw.projeto.repository.EstrategiaRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = false)
public class EstrategiaService {

    @Autowired
    private EstrategiaRepository estrategiaRepository;

    public List<EstrategiaModel> buscarTodas() {
        return estrategiaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public EstrategiaModel buscarPorId(Long id) {
        Optional<EstrategiaModel> op = estrategiaRepository.findById(id);
        return op.orElse(null);
    }

    public EstrategiaModel salvar(EstrategiaModel estrategia) {
        return estrategiaRepository.save(estrategia);
    }

    public void remover(Long id) {
        estrategiaRepository.deleteById(id);
    }


}
