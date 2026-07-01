package br.ufscar.dc.dsw.projeto.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "projeto")
public class ProjetoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "projeto_estrategias",
            joinColumns = @JoinColumn(name = "projeto_id"),
            inverseJoinColumns = @JoinColumn(name = "estrategia_id")
    )
    //private List<EstrategiaModel> estrategias = new ArrayList<>();
    private Set<EstrategiaModel> estrategias = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "projeto_membros",
            joinColumns = @JoinColumn(name = "projeto_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    //private List<UsuarioModel> membros = new ArrayList<>();
    private Set<UsuarioModel> membros = new HashSet<>();

    public ProjetoModel() {}
    
    public ProjetoModel(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
        this.estrategias = new HashSet<>();
        this.membros = new HashSet<>();
    }
    
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Set<EstrategiaModel> getEstrategias() { return estrategias; }
    public void setEstrategias(Set<EstrategiaModel> estrategias) { this.estrategias = estrategias; }

    public Set<UsuarioModel> getMembros() { 
        return membros; 
    }
    
    public void setMembros(Set<UsuarioModel> membros) { 
        this.membros = membros != null ? membros : new HashSet<>(); 
    }

    // GERENCIAR MEMBROS
    public void adicionarMembro(UsuarioModel usuario) {
        if (usuario != null && !this.membros.contains(usuario)) {
            this.membros.add(usuario);
        }
    }

    public void removerMembro(UsuarioModel usuario) {
        this.membros.remove(usuario);
    }

    public boolean temMembro(UsuarioModel usuario) {
        return this.membros.contains(usuario);
    }

    public boolean temMembro(Long usuarioId) {
        return this.membros.stream().anyMatch(m -> m.getId().equals(usuarioId));
    }

    // PEGAR NOMES DOS MEMBROS COMO STRING
    public String getNomesMembros() {
        return this.membros.stream()
                .map(UsuarioModel::getNome)
                .collect(java.util.stream.Collectors.joining(", "));
    }
}


