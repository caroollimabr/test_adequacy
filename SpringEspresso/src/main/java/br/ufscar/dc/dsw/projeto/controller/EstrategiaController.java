package br.ufscar.dc.dsw.projeto.controller;

import br.ufscar.dc.dsw.projeto.model.DicaModel;
import br.ufscar.dc.dsw.projeto.model.EstrategiaModel;
import br.ufscar.dc.dsw.projeto.model.ExemploModel;
import br.ufscar.dc.dsw.projeto.repository.DicaRepository;
import br.ufscar.dc.dsw.projeto.repository.ExemploRepository;
import br.ufscar.dc.dsw.projeto.service.EstrategiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/estrategias")
public class EstrategiaController {

    @Autowired
    private EstrategiaService estrategiaService;

    @Autowired
    private DicaRepository dicaRepository;

    @Autowired
    private ExemploRepository exemploRepository;

    private static final String UPLOAD_DIR = "uploads/estrategias/";

    //documentacao OpenAPI
    @Operation(summary = "Página de listagem das estrategias")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view estrategias/listar")
    })
    //   
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("estrategias", estrategiaService.buscarTodas());
        return "estrategias/listar";
    }

        //documentacao OpenAPI
    @Operation(summary = "Página de nova estrategia")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view estrategias/formulario")
    })
    //
    @GetMapping("/novo")
    public String exibirFormularioNovo(Model model) {
        model.addAttribute("estrategia", new EstrategiaModel());
        return "estrategias/formulario";
    }

        //documentacao OpenAPI
    @Operation(summary = "Página de edicao da estrategia")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view estrategias/formulario")
    })
    //
    @GetMapping("/editar/{id}")
    public String exibirFormularioEditar(@PathVariable("id") Long id, Model model) {
        model.addAttribute("estrategia", estrategiaService.buscarPorId(id));
        return "estrategias/formulario";
    }

        //documentacao OpenAPI
    @Operation(summary = "Página de detalhes da estrategia")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view estrategias/detalhes")
    })
    //
    @GetMapping("/detalhes/{id}")
    public String detalhes(@PathVariable("id") Long id, Model model) {
        model.addAttribute("estrategia", estrategiaService.buscarPorId(id));
        return "estrategias/detalhes";
    }

        //documentacao OpenAPI
    @Operation(summary = "Salva nova estrategia")
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "Sucesso: Redireciona para /estrategias"),
        @ApiResponse(responseCode = "400", description = "Erro de validação")
    })
    //
    @PostMapping("/salvar")
    public String salvar(

            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("nome") String nome,
            @RequestParam("descricao") String descricao,
            @RequestParam(value = "dicasId", required = false) List<Long> dicasId,
            @RequestParam(value = "dicasTexto", required = false) List<String> dicasTexto,
            @RequestParam(value = "exemplosId", required = false) List<Long> exemplosId,
            @RequestParam(value = "exemplosTexto", required = false) List<String> exemplosTexto,
            @RequestParam(value = "exemplosUrlImagem", required = false) List<String> exemplosUrlImagem,
            @RequestParam(value = "imagensExemplo", required = false) List<MultipartFile> imagensExemplo,
            RedirectAttributes attr) {


        EstrategiaModel estrategia;
        if (id != null) {

            estrategia = estrategiaService.buscarPorId(id);
        } else {

            estrategia = new EstrategiaModel();
        }

        estrategia.setNome(nome);
        estrategia.setDescricao(descricao);

        estrategia.getDicas().clear();
        estrategia.getExemplos().clear();


        List<DicaModel> dicasProcessadas = new ArrayList<>();
        if (dicasTexto != null) {
            for (int i = 0; i < dicasTexto.size(); i++) {
                if (!dicasTexto.get(i).isBlank()) {
                    
                    Long idDica = (dicasId != null && i < dicasId.size()) ? dicasId.get(i) : null;
                    DicaModel dica = (idDica != null) ? dicaRepository.findById(idDica).orElse(new DicaModel()) : new DicaModel();
                    dica.setTexto(dicasTexto.get(i));
                    dica.setEstrategia(estrategia);
                    dicasProcessadas.add(dica);
                }
            }
        }

        estrategia.getDicas().addAll(dicasProcessadas);


        List<ExemploModel> exemplosProcessados = new ArrayList<>();
        if (exemplosTexto != null) {
            for (int i = 0; i < exemplosTexto.size(); i++) {
                if (!exemplosTexto.get(i).isBlank()) {
                    Long idExemplo = (exemplosId != null && i < exemplosId.size()) ? exemplosId.get(i) : null;
                    ExemploModel exemplo = (idExemplo != null) ? exemploRepository.findById(idExemplo).orElse(new ExemploModel()) : new ExemploModel();

                    exemplo.setTexto(exemplosTexto.get(i));
                    exemplo.setEstrategia(estrategia);

                    String urlImagemAtual = (exemplosUrlImagem != null && i < exemplosUrlImagem.size()) ? exemplosUrlImagem.get(i) : "";
                    MultipartFile imagem = (imagensExemplo != null && i < imagensExemplo.size()) ? imagensExemplo.get(i) : null;


                    if (imagem == null || imagem.isEmpty()) {
                        exemplo.setUrlImagem(urlImagemAtual);
                    }

                    
                    exemplosProcessados.add(exemplo);
                }
            }
        }
        estrategia.getExemplos().addAll(exemplosProcessados);

        EstrategiaModel estrategiaSalva = estrategiaService.salvar(estrategia);

        boolean precisaSalvarDeNovo = false;
        if (imagensExemplo != null) {
            for (int i = 0; i < estrategiaSalva.getExemplos().size(); i++) {
                ExemploModel exemploSalvo = estrategiaSalva.getExemplos().get(i);
                MultipartFile imagem = (i < imagensExemplo.size()) ? imagensExemplo.get(i) : null;

                if (imagem != null && !imagem.isEmpty()) {
                    try {
                        String nomeArquivoOriginal = imagem.getOriginalFilename();
                        String extensao = "";
                        if (nomeArquivoOriginal != null && nomeArquivoOriginal.contains(".")) {
                            extensao = nomeArquivoOriginal.substring(nomeArquivoOriginal.lastIndexOf("."));
                        }


                        String nomeArquivoFormatado = String.format("exemplo_%d_E%d%s", estrategia.getId(), estrategiaSalva.getId(), extensao);

                        Path caminhoArquivo = Paths.get(UPLOAD_DIR, nomeArquivoFormatado);
                        Files.createDirectories(caminhoArquivo.getParent());
                        Files.write(caminhoArquivo, imagem.getBytes());
                        
                        exemploSalvo.setUrlImagem(nomeArquivoFormatado);
                        precisaSalvarDeNovo = true;

                    } catch (IOException e) {
                        e.printStackTrace();
                        attr.addFlashAttribute("fail", "Falha ao fazer upload da imagem para o exemplo: " + exemploSalvo.getTexto());
                        return "redirect:/estrategias/editar/" + estrategiaSalva.getId();
                    }
                }
            }
        }

        if (precisaSalvarDeNovo) {
            estrategiaService.salvar(estrategiaSalva);
        }


        attr.addFlashAttribute("success", "Estratégia salva com sucesso!");
        return "redirect:/estrategias";
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de remocao da estrategia")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view /estrategias")
    })
    //
    @GetMapping("/remover/{id}")
    public String remover(@PathVariable("id") Long id, RedirectAttributes attr) {
        try {
            estrategiaService.remover(id);
            attr.addFlashAttribute("success", "Estratégia removida com sucesso.");
        } catch (Exception e) {
            attr.addFlashAttribute("fail", "Não foi possível remover a estratégia. Verifique se ela não está em uso.");
        }
        return "redirect:/estrategias";
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de listagem das estrategias publica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view estrategias/list")
    })
    //
    // Visualização de estratégias
    @GetMapping("/public/estrategias") // PUBLICO PARA GUEST
    public String listarEstrategiasPublico(Model model) {
        model.addAttribute("estrategias", estrategiaService.buscarTodas());
        model.addAttribute("isGuest", true);
        return "estrategias/list";
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de listagem das estrategias para usuarios autenticados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view estrategias/list")
    })
    //
    // PROTEGIDO PARA USUARIOS AUTENTICADOS
    @GetMapping("/estrategias")
    public String listarEstrategias(Model model, @RequestParam(value = "guest", defaultValue = "false") boolean guest) {
        if (guest) {
            return "redirect:/public/estrategias";
        }
        model.addAttribute("estrategias", estrategiaService.buscarTodas());
        model.addAttribute("isGuest", false);
        return "estrategias/list";
    }

    //documentacao OpenAPI
    @Operation(summary = "Página de detalhes da estrategia")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view /estrategias")
    })
    //
    @GetMapping("/estrategias/{id}")
    public String detalheEstrategia(@PathVariable Long id, Model model) {
        EstrategiaModel estrategia = estrategiaService.buscarPorId(id);
        if (estrategia != null) {
            model.addAttribute("estrategia", estrategia);
            return "estrategias/detalhes";
        }
        return "redirect:/estrategias";
    }
}
