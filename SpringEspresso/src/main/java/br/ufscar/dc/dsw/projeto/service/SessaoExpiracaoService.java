package br.ufscar.dc.dsw.projeto.service;

import br.ufscar.dc.dsw.projeto.model.SessaoModel;
import br.ufscar.dc.dsw.projeto.model.StatusSessao;
import br.ufscar.dc.dsw.projeto.repository.SessaoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

// ESTA CLASSE VERIFICA PERIODICAMENTE SESSÕES QUE DEVERIAM TER SIDO FINALIZADAS, 
// PELO TEMPO QUE FORAM INICIADAS + DURAÇÃO, E AS FINALIZA AUTOMATICAMENTE

@Service
public class SessaoExpiracaoService {
    
    private static final Logger logger = LoggerFactory.getLogger(SessaoExpiracaoService.class);
    
    @Autowired
    private SessaoRepository sessaoRepository;

    @PostConstruct
    public void init() {
    }

    @Scheduled(fixedRate = 20000) // Executa a cada 10 segundos
    @Transactional
    public void verificarSessoesExpiradas() {
        
        
        try {
            LocalDateTime agora = LocalDateTime.now();
            
            List<SessaoModel> sessoesEmExecucao = sessaoRepository.findByStatus(StatusSessao.EM_EXECUCAO);
            
            
            int sessoesFinalizadas = 0;
            
            for (SessaoModel sessao : sessoesEmExecucao) {
                
                if (sessao.getDataInicioExecucao() != null && 
                    sessao.getDuracao() != null) {
                    
                    LocalDateTime tempoLimite = sessao.getDataInicioExecucao()
                        .plus(sessao.getDuracao());
                    
                    
                    
                    if (agora.isAfter(tempoLimite)) {
                        logger.info("Finalizando sessão {} por expiração", sessao.getId());
                        
                        sessao.setStatus(StatusSessao.FINALIZADO);
                        sessao.setDataFinalizacao(agora);
                        sessaoRepository.save(sessao);
                        
                        sessoesFinalizadas++;
                        logger.info("Sessão {} finalizada automaticamente por expiração", sessao.getId());
                    }
                } else {
                    logger.debug("Sessão {} sem data de início ou duração definida", sessao.getId());
                }
            }
            
            if (sessoesFinalizadas > 0) {
                logger.info("Total de {} sessões finalizadas automaticamente", sessoesFinalizadas);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao verificar sessões expiradas: {}", e.getMessage(), e);
        }
    }
}