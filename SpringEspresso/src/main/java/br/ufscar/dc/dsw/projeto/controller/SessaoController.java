package br.ufscar.dc.dsw.projeto.controller;

import br.ufscar.dc.dsw.projeto.model.EstrategiaModel;
import br.ufscar.dc.dsw.projeto.model.ProjetoModel;
import br.ufscar.dc.dsw.projeto.model.SessaoModel;
import br.ufscar.dc.dsw.projeto.model.StatusSessao;
import br.ufscar.dc.dsw.projeto.model.UsuarioModel;
import br.ufscar.dc.dsw.projeto.service.EstrategiaService;
import br.ufscar.dc.dsw.projeto.service.ProjetoService;
import br.ufscar.dc.dsw.projeto.service.SessaoService;
import br.ufscar.dc.dsw.projeto.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Controller
public class SessaoController {

    private final SessaoService sessaoService;
    private final ProjetoService projetoService;
    private final EstrategiaService estrategiaService;
    private final UsuarioService usuarioService; 

    public SessaoController(SessaoService sessaoService, ProjetoService projetoService, EstrategiaService estrategiaService, UsuarioService usuarioService) {
        this.sessaoService = sessaoService;
        this.projetoService = projetoService;
        this.estrategiaService = estrategiaService;
        this.usuarioService = usuarioService; 
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de listagem de sessoes de um projeto com uma estratégia")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Redireciona para sessoes/lista")
    })
    //
    // LISTAR SESSÕES DE UMA ESTRATÉGIA EM UM PROJETO
    @GetMapping("/projetos/{projetoId}/estrategias/{estrategiaId}/sessoes")
    public String listarSessoes(@PathVariable Long projetoId, @PathVariable Long estrategiaId, Model model) {
        List<SessaoModel> sessoes = sessaoService.buscarPorProjetoEEstrategia(projetoId, estrategiaId);
        ProjetoModel projeto = projetoService.buscar(projetoId);
        EstrategiaModel estrategia = estrategiaService.buscarPorId(estrategiaId);

        model.addAttribute("sessoes", sessoes);
        model.addAttribute("projeto", projeto);
        model.addAttribute("estrategia", estrategia);

        return "sessoes/lista";
    }

    /*  CADASTRAR NOVA SESSÃO
    @GetMapping("/projetos/{projetoId}/estrategias/{estrategiaId}/sessoes/nova")
    public String novaSessaoForm(@PathVariable Long projetoId, @PathVariable Long estrategiaId, Model model, Authentication authentication) {
        ProjetoModel projeto = projetoService.buscar(projetoId);
        EstrategiaModel estrategia = estrategiaService.buscarPorId(estrategiaId);

        SessaoModel sessao = new SessaoModel();
        sessao.setProjeto(projeto);
        sessao.setEstrategia(estrategia);

        model.addAttribute("sessao", sessao);

        // APENAS QUEM ESTÁ REGISTRADO NO PROJETO
        List<UsuarioModel> membrosDisponiveis = projeto.getMembros();
        model.addAttribute("usuarios", membrosDisponiveis);

        // USUARIO LOGADO
        String emailUsuario = authentication.getName();
        UsuarioModel usuarioLogado = usuarioService.buscarPorEmail(emailUsuario);
        model.addAttribute("usuarioLogado", usuarioLogado);
        
        // VERIFICAR ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        return "sessoes/formulario";
    }*/

    //documentacao OpenAPI
    @Operation(summary = "Página de detalhes da sessao")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Redireciona para /sessao/detalhes")
    })
    //
    // DETALHES DA SESSÃO
    @GetMapping("/sessoes/detalhes/{id}")
    public String detalhesSessao(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        SessaoModel sessao = sessaoService.buscarPorIdComBugs(id);

        if (sessao == null) {
            redirectAttributes.addFlashAttribute("fail", "Sessão não encontrada.");
            return "redirect:/home";
        }

        model.addAttribute("sessao", sessao);
        return "sessao/detalhes";
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de salvar sessao")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view /projetos/{id}/estrategias/{id}/sessoes")
    })
    // 
    // SALVAR SESSÃO
    @PostMapping("/sessoes/salvar")
    public String salvarSessao(@RequestParam Long projetoId,
                               @RequestParam Long estrategiaId,
                               @RequestParam String descricao,
                               @RequestParam(required = true) Long testerId,
                               @RequestParam Long duracaoMinutos,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {

        ProjetoModel projeto = projetoService.buscar(projetoId);
        EstrategiaModel estrategia = estrategiaService.buscarPorId(estrategiaId);

        if (projeto == null || estrategia == null) {
            redirectAttributes.addFlashAttribute("mensagemFalha", "Erro ao salvar: Projeto ou Estratégia inválido.");
            return "redirect:/home";
        }

        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        if (testerId == null) {
        redirectAttributes.addFlashAttribute("mensagemFalha", "Selecione um tester para continuar.");
        return "redirect:/projetos/" + projetoId + "/estrategias/" + estrategiaId + "/sessoes/novo";
    }

        SessaoModel sessao = new SessaoModel();
        sessao.setProjeto(projeto);
        sessao.setEstrategia(estrategia);
        sessao.setDescricao(descricao);
        sessao.setDuracao(Duration.ofMinutes(duracaoMinutos));

        // APENAS ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // ADMIN PODE ESCOLHER QUALQUER UM
            if (testerId != null) {
                UsuarioModel tester = usuarioService.buscarPorId(testerId);
                sessao.setTester(tester);
            }
        } else {
            // USER NORMAL SÓ PODE SER ELE MESMO
            String emailUsuario = authentication.getName();
            UsuarioModel usuarioLogado = usuarioService.buscarPorEmail(emailUsuario);
            sessao.setTester(usuarioLogado);
        }

        sessaoService.salvar(sessao);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Sessão criada com sucesso!");
        return "redirect:/projetos/" + projetoId + "/estrategias/" + estrategiaId + "/sessoes";
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de cadastro de nova sessao")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Redireciona para sessoes/formulario")
    })
    //
    // CADASTRAR NOVA SESSÃO - FORMULÁRIO
    @GetMapping("/sessoes/cadastro")
    public String formCadastro(@RequestParam Long projetoId, @RequestParam Long estrategiaId, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        ProjetoModel projeto = projetoService.buscar(projetoId);
        EstrategiaModel estrategia = estrategiaService.buscarPorId(estrategiaId);

        if (projeto == null || estrategia == null) {
            redirectAttributes.addFlashAttribute("mensagemFalha", "Projeto ou Estratégia não encontrado.");
            return "redirect:/projetos/listar";
        }

        SessaoModel sessao = new SessaoModel();
        sessao.setProjeto(projeto);
        sessao.setEstrategia(estrategia);

        model.addAttribute("sessao", sessao);
        
        // VERIFICAR APENAS MEMBROS DO PROJETO
        Set<UsuarioModel> membrosDisponiveis = projeto.getMembros();
        model.addAttribute("usuarios", membrosDisponiveis);
        
        // USUARIO LOGADO
        String emailUsuario = authentication.getName();
        UsuarioModel usuarioLogado = usuarioService.buscarPorEmail(emailUsuario);
        model.addAttribute("usuarioLogado", usuarioLogado);
        
        // É ADMIN?
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        
        // CASO AINDA NÃO HAJA MEMBROS
        if (membrosDisponiveis == null || membrosDisponiveis.isEmpty()) {
            model.addAttribute("avisoSemMembros", true);
        }
        
        return "sessoes/formulario";
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de atualizacao do status da sessao")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view /sessoes/detalhes/{id}")
    })
    // 
    // ATUALIZAR STATUS DA SESSÃO
    @PostMapping("/sessoes/atualizarStatus")
    public String atualizarStatus(@RequestParam("sessaoId") Long sessaoId,
                                  @RequestParam("novoStatus") String novoStatus,
                                  Authentication authentication,
                                  RedirectAttributes attr) {

        SessaoModel sessao = sessaoService.buscarPorId(sessaoId);

        if (sessao == null) {
            attr.addFlashAttribute("mensagemFalha", "Sessão não encontrada.");
            return "redirect:/home";
        }

        // APENAS TESTER RESPONSÁVEL OU ADMIN
        if (!temPermissaoParaSessao(sessao, authentication)) {
            attr.addFlashAttribute("mensagemFalha", "Você não tem permissão para alterar esta sessão.");
            return "redirect:/sessoes/detalhes/" + sessaoId;
        }

        try {
            StatusSessao novoStatusEnum = StatusSessao.valueOf(novoStatus);
            StatusSessao statusAtual = sessao.getStatus();
            
            // MUDAR DATAS CONFORME O NOVO STATUS (AGORA)
            if (statusAtual == StatusSessao.CRIADO && novoStatusEnum == StatusSessao.EM_EXECUCAO) {
                sessao.setDataInicioExecucao(LocalDateTime.now());
            } else if (novoStatusEnum == StatusSessao.FINALIZADO) {
                sessao.setDataFinalizacao(LocalDateTime.now());
                // SE NUNCA TIVER SIDO INICIADA, INICIAR AGORA
                if (sessao.getDataInicioExecucao() == null) {
                    sessao.setDataInicioExecucao(LocalDateTime.now());
                }
            }
            
            sessao.setStatus(novoStatusEnum);
            sessaoService.salvar(sessao);
            attr.addFlashAttribute("mensagemSucesso", "Status da sessão atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            attr.addFlashAttribute("mensagemFalha", "O status selecionado é inválido.");
        }

        return "redirect:/sessoes/detalhes/" + sessaoId;
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de exclusao de sessao")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Redireciona para /projetos/{id}/estrategias/{id}/sessoes")
    })
    // EXCLUIR SESSÃO
    @PostMapping("/sessoes/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, Authentication authentication, RedirectAttributes attr) {
        SessaoModel sessao = sessaoService.buscarPorId(id);
        if (sessao == null) {
            attr.addFlashAttribute("mensagemFalha", "Sessão não encontrada.");
            return "redirect:/";
        }

        // Verificar permissões
        if (!temPermissaoParaSessao(sessao, authentication)) {
            attr.addFlashAttribute("mensagemFalha", "Você não tem permissão para remover esta sessão.");
            return "redirect:/sessoes/detalhes/" + id;
        }

        try {
            sessaoService.excluirSessao(id);
            attr.addFlashAttribute("mensagemSucesso", "Sessão removida com sucesso!");
        } catch (Exception e) {
            attr.addFlashAttribute("mensagemFalha", "Erro ao remover a sessão.");
        }

        return "redirect:/projetos/" + sessao.getProjeto().getId() + "/estrategias/" + sessao.getEstrategia().getId() + "/sessoes";
    }

    // Método auxiliar para verificar permissões
    private boolean temPermissaoParaSessao(SessaoModel sessao, Authentication authentication) {
        // Se for ADMIN, sempre tem permissão
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        
        // Se for o tester responsável, tem permissão
        if (sessao.getTester() != null && 
            sessao.getTester().getEmail().equals(authentication.getName())) {
            return true;
        }
        
        return false;
    }
}
