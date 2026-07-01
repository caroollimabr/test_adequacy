package br.ufscar.dc.dsw.projeto.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.ufscar.dc.dsw.projeto.model.EstrategiaModel;
import br.ufscar.dc.dsw.projeto.model.ProjetoModel;
import br.ufscar.dc.dsw.projeto.model.SessaoModel;
import br.ufscar.dc.dsw.projeto.model.StatusSessao;
import br.ufscar.dc.dsw.projeto.model.UsuarioModel;
import br.ufscar.dc.dsw.projeto.repository.EstrategiaRepository;
import br.ufscar.dc.dsw.projeto.repository.ProjetoRepository;
import br.ufscar.dc.dsw.projeto.repository.SessaoRepository;
import br.ufscar.dc.dsw.projeto.repository.UsuarioRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProjetoRepository projetoRepository;
    private final EstrategiaRepository estrategiaRepository;
    private final SessaoRepository sessaoRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
        UsuarioRepository usuarioRepository,
        ProjetoRepository projetoRepository,
        EstrategiaRepository estrategiaRepository,
        SessaoRepository sessaoRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.projetoRepository = projetoRepository;
        this.estrategiaRepository = estrategiaRepository;
        this.sessaoRepository = sessaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CRIA DADOS DO BANCO DE DADOS DE MANEIRA AUTOMÁTICA

    @Override
    public void run(String... args) {

        boolean popula = true;  // teste

        if (!popula) {
            System.out.println("População do banco de dados desativada.");
            return;
        }

        // Limpa o banco na ordem certa
        sessaoRepository.deleteAll();
        projetoRepository.deleteAll();    
        estrategiaRepository.deleteAll(); 
        usuarioRepository.deleteAll();    
        
        // Cria usuários com senha criptografada
        UsuarioModel admin = new UsuarioModel("Admin", "admin@admin.com", passwordEncoder.encode("admin")); 
        admin.setRole("ROLE_ADMIN"); 

        UsuarioModel user = new UsuarioModel("Maria Silva", "user@user.com", passwordEncoder.encode("user")); 
        user.setRole("ROLE_USER");

        UsuarioModel user2 = new UsuarioModel("User Usuario", "useruser@user.com", passwordEncoder.encode("useruser")); 
        user2.setRole("ROLE_USER");

        List<UsuarioModel> usuariosSalvos = usuarioRepository.saveAll(List.of(admin, user, user2));
        
        // Cria estratégias padrão
        List<EstrategiaModel> estrategiasPadrao = new ArrayList<>();
        
        EstrategiaModel singleSession = new EstrategiaModel("Newbie", "Consiste em deixar o jogador ter um primeiro contato com o jogo, e jogar da maneira que achar melhor, sem nenhuma estratégia em mente, apenas para se acostumar com o jogo");
        EstrategiaModel goldenPath = new EstrategiaModel("Golden Path", "Consiste em apresentar ao jogador a maneira ótima de se jogar, ou seja, seguir o caminho recomendado pelos desenvolvedores, de maneira otimizada e direta, evitando desvios desnecessários, e rotas não desejadas.");
        EstrategiaModel noobJourney = new EstrategiaModel("Noob Journey", "Tentar jogar o jogo de maneira errada, não seguindo recomendações, e tentando achar maneiras alternativas de realizar as ações (basicamente tentar fazer o contrário do que é recomendado no \"Golden Path Strategy\"), a fim de descobrir maneiras alternativas de prosseguir no jogo.");
        EstrategiaModel completionist = new EstrategiaModel("Completionist", "Consiste em jogar o jogo de maneira a realizar todas as ações possíveis dentro dele (pegar todas as moedas, matar todos os inimigos, abrir todas as portas, etc), de maneira que não reste mais nenhum objetivo a ser completo.");
        EstrategiaModel stressTest = new EstrategiaModel("Stress Test", "Consiste em realizar comandos e inválidos, ou não esperados pelos desenvolvedores, como por exemplo apertar botões que não realizam ações, ou apertá-los de maneira muito rápida, com o intuito de \"quebrar\" o jogo");
        EstrategiaModel speedrun = new EstrategiaModel("Speedrun", "Consiste em tentar finalizar a sessão da maneira mais rápida possível, fazendo o mínimo de ações necessárias para concluir o objetivo.");
        EstrategiaModel userInterface = new EstrategiaModel("User Interface", "Testar menus, interfaces e gráficos do jogo, de maneira a ver se estão funcionando da maneira adequada.");
        EstrategiaModel neighboring = new EstrategiaModel("Neighboring", "Uma vez encontrado um bug, o usuário tenta encontrar mais deles realizando ações parecidas e em locais próximos, uma vez que a possibilidade de encontrar mais bugs mostra-se maior em áreas onde já foram encontrados outros anteriormente");
        EstrategiaModel overtime = new EstrategiaModel("Overtime", "Testar novamente algum bug conhecido, em um outro momento (aplicar o neighboring após melhorias/evoluções)");
        
        estrategiasPadrao.addAll(List.of(singleSession, goldenPath, noobJourney, completionist, 
                                       stressTest, speedrun, userInterface, neighboring, overtime));
        
        // Salvar estratégias primeiro e capturar as entidades salvas
        List<EstrategiaModel> estrategiasSalvas = estrategiaRepository.saveAll(estrategiasPadrao);
        
        // Criar estratégia adicional
        EstrategiaModel automatedTesting = new EstrategiaModel("Automated Testing", "Execução de testes automatizados para validação de funcionalidades críticas");
        EstrategiaModel automatedTestingSalva = estrategiaRepository.save(automatedTesting);
        
        // Criar projetos SEM associações iniciais
        ProjetoModel projeto1 = new ProjetoModel("Projeto Alpha", "Teste Projeto Alpha");
        ProjetoModel projeto2 = new ProjetoModel("Projeto Beta", "Teste Projeto Beta");
        
        // Salvar projetos primeiro (vazios)
        ProjetoModel projeto1Salvo = projetoRepository.save(projeto1);
        ProjetoModel projeto2Salvo = projetoRepository.save(projeto2);

        // AGORA associar usuários e estratégias usando as entidades managed
        projeto1Salvo.getMembros().add(usuariosSalvos.get(1)); // user (Maria Silva)
        projeto1Salvo.getMembros().add(usuariosSalvos.get(2)); // user2 (User Usuario)
        projeto1Salvo.getEstrategias().addAll(estrategiasSalvas);
        
        projeto2Salvo.getMembros().add(usuariosSalvos.get(1)); // user (Maria Silva)
        projeto2Salvo.getEstrategias().addAll(estrategiasSalvas);
        projeto2Salvo.getEstrategias().add(automatedTestingSalva);
        
        // Salvar projetos com as associações
        projetoRepository.saveAll(List.of(projeto1Salvo, projeto2Salvo));

        // ===== CRIAR SESSÕES FINALIZADAS =====
        List<EstrategiaModel> estrategiasProj2 = new ArrayList<>(estrategiasSalvas);
        estrategiasProj2.add(automatedTestingSalva);
        
        criarSessoesParaProjeto(projeto1Salvo, estrategiasSalvas, List.of(usuariosSalvos.get(1), usuariosSalvos.get(2)));
        criarSessoesParaProjeto(projeto2Salvo, estrategiasProj2, List.of(usuariosSalvos.get(1)));

        System.out.println("Banco populado com Usuarios, Projetos, Estratégias e Sessões!");
    }

    private void criarSessoesParaProjeto(ProjetoModel projeto, List<EstrategiaModel> estrategias, List<UsuarioModel> usuarios) {
        List<SessaoModel> sessoes = new ArrayList<>();
        
        for (int i = 0; i < estrategias.size(); i++) {
            EstrategiaModel estrategia = estrategias.get(i);
            
            // Criar diferentes quantidades de sessões para cada estratégia
            int quantidadeSessoes = calcularQuantidadeSessoes(i, estrategia.getNome());
            
            for (int j = 0; j < quantidadeSessoes; j++) {
                SessaoModel sessao = new SessaoModel();
                sessao.setProjeto(projeto);
                sessao.setEstrategia(estrategia);
                sessao.setTester(usuarios.get(j % usuarios.size()));
                sessao.setStatus(StatusSessao.FINALIZADO);
                
                // Definir duração variada (1-4 horas)
                Duration duracaoSessao = Duration.ofHours(1 + (j % 4));
                sessao.setDuracao(duracaoSessao);
                
                // Descrições variadas
                sessao.setDescricao(gerarDescricaoSessao(estrategia.getNome(), j + 1));
                
                sessoes.add(sessao);
            }
        }
        
        sessaoRepository.saveAll(sessoes);
    }

    private int calcularQuantidadeSessoes(int indice, String nomeEstrategia) {
        // Verificar por nome específico primeiro
        if (nomeEstrategia.contains("Automated")) {
            return 12; // Automated Testing - 12 sessões (deve ter borda dourada)
        }
        
        // Criar padrões interessantes para demonstrar as bordas especiais
        return switch (indice) {
            case 0 -> 4;  // Single Session Strategy - 4 sessões
            case 1 -> 7;  // Golden Path Strategy - 7 sessões  
            case 2 -> 2;  // Noob Journey - 2 sessões
            case 3 -> 10; // Completionist - 10 sessões (deve ter borda dourada)
            case 4 -> 1;  // Stress Test - 1 sessão
            case 5 -> 6;  // Speedrun - 6 sessões
            case 6 -> 3;  // User Interface - 3 sessões
            case 7 -> 8;  // Neighboring - 8 sessões
            case 8 -> 5;  // Overtime - 5 sessões
            default -> 1;
        };
    }

    private String gerarDescricaoSessao(String estrategia, int numero) {
        return switch (estrategia) {
            case "Single Session Strategy" -> 
                "Sessão " + numero + ": Primeiro contato com o jogo. Jogador explorou livremente o ambiente inicial. Duração média de teste.";
            case "Golden Path Strategy" -> 
                "Sessão " + numero + ": Seguindo o caminho otimizado recomendado. Tutorial completado com sucesso. Fluxo principal testado.";
            case "Noob Journey" -> 
                "Sessão " + numero + ": Testando caminhos alternativos. Descobertas interessantes sobre mecânicas não documentadas. Exploração criativa.";
            case "Completionist" -> 
                "Sessão " + numero + ": Coletando todos os itens possíveis. Explorando 100% do mapa disponível. Teste exaustivo realizado.";
            case "Stress Test" -> 
                "Sessão " + numero + ": Testando limites do sistema. Comandos rápidos e inválidos executados. Verificação de robustez.";
            case "Speedrun" -> 
                "Sessão " + numero + ": Tentativa de recorde de velocidade. Focando no mínimo de ações necessárias. Otimização de tempo.";
            case "User Interface" -> 
                "Sessão " + numero + ": Validando menus e interfaces. Testando responsividade e usabilidade. Experiência do usuário avaliada.";
            case "Neighboring" -> 
                "Sessão " + numero + ": Investigando área próxima a bugs conhecidos. Procurando por padrões similares. Análise de correlação.";
            case "Overtime" -> 
                "Sessão " + numero + ": Re-testando bugs após atualizações. Verificando se correções foram efetivas. Validação de melhorias.";
            case "Automated Testing" -> 
                "Sessão " + numero + ": Execução de testes automatizados. Scripts validando funcionalidades críticas. Cobertura abrangente.";
            default -> 
                "Sessão " + numero + ": Testando estratégia " + estrategia + ". Coletando dados e feedback detalhado do processo.";
        };
    }
}


