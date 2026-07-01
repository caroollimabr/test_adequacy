package br.ufscar.dc.dsw.projeto.model;

public enum TipoArquivo {
    IMAGEM("Imagem"),
    VIDEO("Vídeo");

    private final String descricao;

    TipoArquivo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}