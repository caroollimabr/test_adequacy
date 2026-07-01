package br.ufscar.dc.dsw.projeto.model;

public enum CriticidadeBug {
    BAIXA("Baixa", "#99f8afff"),
    MEDIA("Média", "#f8e4a7ff"), 
    CRITICA("Crítica", "#f59aa3ff");

    private final String descricao;
    private final String cor;

    CriticidadeBug(String descricao, String cor) {
        this.descricao = descricao;
        this.cor = cor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCor() {
        return cor;
    }
}