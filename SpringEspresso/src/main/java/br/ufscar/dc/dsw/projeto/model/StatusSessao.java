package br.ufscar.dc.dsw.projeto.model;

public enum StatusSessao {
    CRIADO("Criado"),
    EM_EXECUCAO("Em Execução"),
    FINALIZADO("Finalizado");

    private final String descricao;

    StatusSessao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}