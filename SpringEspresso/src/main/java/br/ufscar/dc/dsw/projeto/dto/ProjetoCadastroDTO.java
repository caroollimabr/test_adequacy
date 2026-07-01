package br.ufscar.dc.dsw.projeto.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class ProjetoCadastroDTO {

    @NotBlank(message = "{NotBlank.projeto.nome}")
    private String nome;

    private String descricao;

    private List<Long> membrosIds = new ArrayList<>();

    private List<Long> estrategiasIds;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public List<Long> getMembrosIds() {
        return membrosIds;
    }

    public void setMembrosIds(List<Long> membrosIds) {
        this.membrosIds = membrosIds != null ? membrosIds : new ArrayList<>();
    }

    public List<Long> getEstrategiasIds() { return estrategiasIds; }
    public void setEstrategiasIds(List<Long> estrategiasIds) { this.estrategiasIds = estrategiasIds; }
}
