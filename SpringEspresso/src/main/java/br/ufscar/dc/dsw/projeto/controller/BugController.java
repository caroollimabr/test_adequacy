package br.ufscar.dc.dsw.projeto.controller;

import br.ufscar.dc.dsw.projeto.model.BugModel;
import br.ufscar.dc.dsw.projeto.model.SessaoModel;
import br.ufscar.dc.dsw.projeto.service.BugService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import br.ufscar.dc.dsw.projeto.repository.SessaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/bugs")
public class BugController {

    @Autowired
    private BugService bugService;
    
    @Autowired
    private SessaoRepository sessaoRepository;
    
    //documentacao OpenAPI
    @Operation(summary = "Página de detalhes dos bugs")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view detalhes")
    })
    //
    @GetMapping("/detalhes/{id}")
    public String detalhesBug(@PathVariable Long id, Model model) {
        BugModel bug = bugService.buscarPorId(id);
        if (bug != null) {
            model.addAttribute("bug", bug);
            return "bug/detalhes";
        }
        return "redirect:/";
    }
  
    //documentacao OpenAPI
    @Operation(summary = "Página de bugs cadastrados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view bug/cadastro")
    })
    //
    @GetMapping("/cadastro")
    public String formularioCadastro(@RequestParam Long idSessao, Model model) {
        SessaoModel sessao = sessaoRepository.findById(idSessao).orElse(null);
        if (sessao != null) {
            model.addAttribute("sessao", sessao);
            model.addAttribute("bug", new BugModel());
            return "bug/cadastro";
        }
        return "redirect:/";
    }

    //documentacao OpenAPI
    @Operation(summary = "Salva o bug")
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "Sucesso: Redireciona para /bugs/cadastro?idSessao={id}"),
        @ApiResponse(responseCode = "400", description = "Erro de validação")
    })
    //
    @PostMapping("/cadastro")
    @Transactional
    public String salvarBug(@ModelAttribute BugModel bug,
                           @RequestParam Long idSessao,
                           @RequestParam(required = false) MultipartFile arquivo,
                           RedirectAttributes redirectAttributes) {
        try {
            SessaoModel sessao = sessaoRepository.findById(idSessao).orElse(null);
            if (sessao != null) {
                bug.setSessao(sessao);
                bug.setDataRegistro(LocalDateTime.now());
                bug.setResolvido(false);
                
                bugService.salvar(bug, arquivo);

                redirectAttributes.addFlashAttribute("mensagemSucesso", "Bug cadastrado com sucesso!");
                return "redirect:/sessoes/detalhes/" + idSessao;
            } else {
                redirectAttributes.addFlashAttribute("mensagemFalha", "Sessão não encontrada!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemFalha", "Erro ao cadastrar bug: " + e.getMessage());
        }
        
        return "redirect:/bugs/cadastro?idSessao=" + idSessao;
    }

    //documentacao OpenAPI
    @Operation(summary = "Salva o bug como resolvido")
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "Sucesso: Redireciona para /bugs/detalhes/{id}"),
        @ApiResponse(responseCode = "400", description = "Erro de validação")
    })
    //
    @PostMapping("/resolver")
    @Transactional
    public String resolverBug(@RequestParam Long bugId, 
                            RedirectAttributes redirectAttributes) {
        try {
            boolean sucesso = bugService.resolverBug(bugId);
            if (sucesso) {
                redirectAttributes.addFlashAttribute("mensagemSucesso", "Bug marcado como resolvido!");
            } else {
                redirectAttributes.addFlashAttribute("mensagemFalha", "Bug não encontrado!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemFalha", "Erro: " + e.getMessage());
        }
        return "redirect:/bugs/detalhes/" + bugId;
    }

    //documentacao OpenAPI
    @Operation(summary = "Reabre um bug já resolvido")
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "Sucesso: Redireciona para /bugs/detalhes/{id}"),
        @ApiResponse(responseCode = "400", description = "Erro de validação")
    })
    //
    @PostMapping("/reabrir")
    @Transactional
    public String reabrirBug(@RequestParam Long bugId, 
                            RedirectAttributes redirectAttributes) {
        try {
            bugService.reabrirBug(bugId);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Bug reaberto!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemFalha", "Erro: " + e.getMessage());
        }
        return "redirect:/bugs/detalhes/" + bugId;
    }
    
    //documentacao OpenAPI
    @Operation(summary = "Página de listagem dos bugs")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view bug/listar")
    })
    //
    @GetMapping("/listar")
public String listarTodos(Model model) {
    List<BugModel> bugs = bugService.listarTodosComSessaoEProjeto();

    model.addAttribute("bugs", bugs);
    return "bug/listar";
}

}