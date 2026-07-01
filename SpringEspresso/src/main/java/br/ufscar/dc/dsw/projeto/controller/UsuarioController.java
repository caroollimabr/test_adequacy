package br.ufscar.dc.dsw.projeto.controller;

import br.ufscar.dc.dsw.projeto.model.UsuarioModel;
import br.ufscar.dc.dsw.projeto.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasAuthority('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de listar usuarios")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Redireciona para usuarios/listar")
    })
    //
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/listar";
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de novo usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Redireciona para usuarios/formulario")
    })
    //
    @GetMapping("/novo")
    public String novoUsuario(Model model) {
        model.addAttribute("usuario", new UsuarioModel());
        return "usuarios/formulario";
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de edicao de usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Redireciona para /usuarios/formulario")
    })
    //
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        UsuarioModel usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("fail", "Usuário não encontrado.");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", usuario);
        return "usuarios/formulario";
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de detalhes do usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Redireciona para usuarios/detalhes")
    })
    //
    @GetMapping("/detalhes/{id}")
    public String detalhesUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        UsuarioModel usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("fail", "Usuário não encontrado.");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", usuario);
        return "usuarios/detalhes";
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de salvar usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view /usuarios/novo ou /usuarios/editar/{id}")
    })
    // 
    @PostMapping("/salvar")
    public String salvarUsuario(@ModelAttribute UsuarioModel usuario, RedirectAttributes redirectAttributes) {
        try {
            // Validar email único
            if (usuario.getId() == null) {
                if (usuarioService.emailJaExiste(usuario.getEmail())) {
                    redirectAttributes.addFlashAttribute("fail", "Email já está em uso por outro usuário.");
                    redirectAttributes.addFlashAttribute("usuario", usuario);
                    return "redirect:/usuarios/novo";
                }
                usuarioService.salvar(usuario);
                redirectAttributes.addFlashAttribute("success", "Usuário criado com sucesso!");
            } else {
                if (usuarioService.emailJaExiste(usuario.getEmail(), usuario.getId())) {
                    redirectAttributes.addFlashAttribute("fail", "Email já está em uso por outro usuário.");
                    redirectAttributes.addFlashAttribute("usuario", usuario);
                    return "redirect:/usuarios/editar/" + usuario.getId();
                }
                usuarioService.atualizar(usuario);
                redirectAttributes.addFlashAttribute("success", "Usuário atualizado com sucesso!");
            }
            
            return "redirect:/usuarios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("fail", "Erro ao salvar usuário: " + e.getMessage());
            return usuario.getId() == null ? "redirect:/usuarios/novo" : "redirect:/usuarios/editar/" + usuario.getId();
        }
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de exclusao de usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Redireciona para /usuarios")
    })
    //
    @GetMapping("/remover/{id}")
    public String removerUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            UsuarioModel usuario = usuarioService.buscarPorId(id);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("fail", "Usuário não encontrado.");
                return "redirect:/usuarios";
            }
            // O USUÁRIO NÃO PODE SE REMOVER
            if(usuario.getEmail().equals(usuarioService.getUsuarioLogado().getEmail())) {
                redirectAttributes.addFlashAttribute("fail", "Você não pode remover seu próprio usuário.");
                return "redirect:/usuarios";
            }   
            usuarioService.remover(id);



            redirectAttributes.addFlashAttribute("success", "Usuário removido com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("fail", "Erro ao remover usuário: " + e.getMessage());
        }
        
        return "redirect:/usuarios";
    }
}
