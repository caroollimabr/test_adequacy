package br.ufscar.dc.dsw.projeto.service;

import br.ufscar.dc.dsw.projeto.model.BugModel;
import br.ufscar.dc.dsw.projeto.model.TipoArquivo;
import br.ufscar.dc.dsw.projeto.repository.BugRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class BugService {

    private static final Logger logger = LoggerFactory.getLogger(BugService.class);

    @Autowired
    private BugRepository bugRepository;

    private final String uploadDir = "uploads/";

    @Transactional
    public BugModel salvar(BugModel bug, MultipartFile arquivo) throws IOException {
        // Salva o bug inicialmente para gerar o id
        BugModel bugSalvo = bugRepository.save(bug);

        if (arquivo != null && !arquivo.isEmpty()) {
            String contentType = arquivo.getContentType();
            TipoArquivo tipoArquivo = null;

            if (contentType != null) {
                if (contentType.startsWith("image/")) {
                    tipoArquivo = TipoArquivo.IMAGEM;
                } else if (contentType.startsWith("video/")) {
                    tipoArquivo = TipoArquivo.VIDEO;
                } else {
                    throw new IOException("Tipo de arquivo não suportado: " + contentType);
                }
            }

            // GERAR ARQUIVO COM PADRÃO NO NOME BUG_ID_BUG_S+ID_SESSAO
            String extensao = "";
            String nomeArquivoOriginal = arquivo.getOriginalFilename();
            if (nomeArquivoOriginal != null && nomeArquivoOriginal.contains(".")) {
                extensao = nomeArquivoOriginal.substring(nomeArquivoOriginal.lastIndexOf("."));
            }
            String nomeArquivo = String.format("bugs/Bug_%d_S%d%s", bugSalvo.getId(), bugSalvo.getSessao().getId(), extensao);

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path caminhoArquivo = uploadPath.resolve(nomeArquivo);

            Files.copy(arquivo.getInputStream(), caminhoArquivo);

            bugSalvo.setCaminhoArquivo(nomeArquivo);
            bugSalvo.setTipoArquivo(tipoArquivo);
            bugRepository.save(bugSalvo);
        }

        return bugSalvo;
    }

    @Transactional(readOnly = true)
    public BugModel buscarPorId(Long id) {
        return bugRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<BugModel> buscarPorSessaoId(Long sessaoId) {
        return bugRepository.findBySessaoId(sessaoId);
    }

    @Transactional
    public void deletar(Long id) {
        bugRepository.deleteById(id);
    }

    @Transactional
    public void excluirBug(Long id) {
        bugRepository.deleteById(id);
    }

    @Transactional
    public void marcarComoResolvido(Long bugId) {
        BugModel bug = bugRepository.findById(bugId).orElse(null);
        if (bug != null) {
            bug.setResolvido(true);
            bugRepository.save(bug);
        }
    }
    
    @Transactional
    public void reabrirBug(Long bugId) {
        BugModel bug = bugRepository.findById(bugId).orElse(null);
        if (bug != null) {
            bug.setResolvido(false);
            bugRepository.save(bug);
        }
    }
    
    @Transactional
    public boolean resolverBug(Long bugId) {
        try {
            BugModel bug = bugRepository.findById(bugId).orElse(null);
            if (bug != null) {
                bug.setResolvido(true);
                bugRepository.save(bug);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Erro ao resolver bug {}: {}", bugId, e.getMessage());
            return false;
        }
    }

    public List<BugModel> listarTodosComSessaoEProjeto() {
        return bugRepository.findAllOrdered();
    }

}