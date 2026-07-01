package br.ufscar.dc.dsw.projeto.service;

import br.ufscar.dc.dsw.projeto.dto.ProjetoCadastroDTO;
import br.ufscar.dc.dsw.projeto.dto.ProjetoEdicaoDTO;
import br.ufscar.dc.dsw.projeto.model.EstrategiaModel;
import br.ufscar.dc.dsw.projeto.model.ProjetoModel;
import br.ufscar.dc.dsw.projeto.repository.EstrategiaRepository;
import br.ufscar.dc.dsw.projeto.repository.ProjetoRepository;
import br.ufscar.dc.dsw.projeto.model.UsuarioModel;
import br.ufscar.dc.dsw.projeto.repository.UsuarioRepository;
import br.ufscar.dc.dsw.projeto.repository.SessaoRepository;
import br.ufscar.dc.dsw.projeto.repository.BugRepository;
import br.ufscar.dc.dsw.projeto.model.SessaoModel;
import br.ufscar.dc.dsw.projeto.model.BugModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final EstrategiaRepository estrategiaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SessaoRepository sessaoRepository;
    
    @Autowired
    private BugRepository bugRepository;

    public ProjetoService(ProjetoRepository projetoRepository, EstrategiaRepository estrategiaRepository) {
        this.projetoRepository = projetoRepository;
        this.estrategiaRepository = estrategiaRepository;
    }

    public List<ProjetoModel> listar() {
        return projetoRepository.findAll();
    }

    public List<ProjetoModel> listarTodos() {
        return projetoRepository.findAll();
    }

    public ProjetoModel buscar(Long id) {
        return projetoRepository.findById(id).orElse(null);
    }

    public ProjetoModel salvar(ProjetoModel projeto) {
        return projetoRepository.save(projeto);
    }

    @Transactional
    public void remover(Long id) {
        ProjetoModel projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        try {
            // TODAS AS SESSÕES DO PROJETO
            List<SessaoModel> sessoes = sessaoRepository.findAll()
                    .stream()
                    .filter(s -> s.getProjeto() != null && s.getProjeto().getId() != null && s.getProjeto().getId().equals(id))
                    .collect(Collectors.toList());
            
            // DELETAR OS BUGS DAS SESSÕES (PROBLEMA DE FK)
            for (SessaoModel sessao : sessoes) {
                try {
                    // BUSCAR BUGS
                    List<BugModel> bugs = bugRepository.findAll()
                            .stream()
                            .filter(b -> b.getSessao() != null && b.getSessao().getId() != null && b.getSessao().getId().equals(sessao.getId()))
                            .collect(Collectors.toList());
                    
                    if (!bugs.isEmpty()) {
                        bugRepository.deleteAll(bugs);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao deletar bugs da sessão " + sessao.getId() + ": " + e.getMessage());
                }
            }
            
            // DELETAR SESSÕES
            if (!sessoes.isEmpty()) {
                sessaoRepository.deleteAll(sessoes);
            }

            //  LIMPAR ASSOCIAÇÕES DE ESTRATÉGIAS E MEMBROS
            if (projeto.getEstrategias() != null) {
                projeto.getEstrategias().clear();
            }
            if (projeto.getMembros() != null) {
                projeto.getMembros().clear();
            }
            projetoRepository.save(projeto);

            // AGORA DELETAR PROJETOS (SEM FK)
            projetoRepository.delete(projeto);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover projeto: " + e.getMessage(), e);
        }
    }

    public void salvar(ProjetoCadastroDTO dto) {
        ProjetoModel projeto = new ProjetoModel();
        projeto.setNome(dto.getNome());
        projeto.setDescricao(dto.getDescricao());

        // ADD ESTRATEGIAS
        if (dto.getEstrategiasIds() != null && !dto.getEstrategiasIds().isEmpty()) {
            Set<EstrategiaModel> estrategias = new HashSet<>(estrategiaRepository.findAllById(dto.getEstrategiasIds()));
            projeto.setEstrategias(estrategias);
        }

        if (dto.getMembrosIds() != null && !dto.getMembrosIds().isEmpty()) {
            Set<UsuarioModel> membros = new HashSet<>(usuarioRepository.findAllById(dto.getMembrosIds()));
            projeto.setMembros(membros);
        }

        projetoRepository.save(projeto);
    }

    // ATUALIZAR PROJETO
    public void atualizar(ProjetoEdicaoDTO dto) {
        ProjetoModel projeto = projetoRepository.findById(dto.getId()).orElse(null);
        if (projeto != null) {
            projeto.setNome(dto.getNome());
            projeto.setDescricao(dto.getDescricao());



            // Atualizar membros
            if (dto.getMembrosIds() != null) {
                Set<UsuarioModel> membros = new HashSet<>(usuarioRepository.findAllById(dto.getMembrosIds()));
                projeto.setMembros(membros);
            } else {
                projeto.setMembros(new HashSet<>());
            }

            projetoRepository.save(projeto);
        }
    }

    public ProjetoModel criarProjetoComEstrategiasPadrao(String nome, String descricao) {
        ProjetoModel projeto = new ProjetoModel(nome, descricao);
        List<EstrategiaModel> estrategiasPadrao = estrategiaRepository.findAll();
        projeto.getEstrategias().addAll(estrategiasPadrao);
        return projetoRepository.save(projeto);
    }

    @Transactional(readOnly = true)
    public List<ProjetoModel> buscarProjetosPorMembro(String emailUsuario) {
        return projetoRepository.findByMembrosEmail(emailUsuario);
    }


}
