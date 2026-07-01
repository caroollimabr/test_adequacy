package br.ufscar.dc.dsw.projeto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "bugs")
public class BugModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    @Column(nullable = false)
    private boolean resolvido = false;

    @Column(name = "caminho_arquivo")
    private String caminhoArquivo;

    @Column(name = "tipo_arquivo") // IMAGEM ou VIDEO
    @Enumerated(EnumType.STRING)
    private TipoArquivo tipoArquivo; 

    @NotNull(message = "A criticidade é obrigatória")
    @Column(name = "criticidade")
    @Enumerated(EnumType.STRING)
    private CriticidadeBug criticidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_id", nullable = false)
    private SessaoModel sessao;

    public BugModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public boolean isResolvido() {
        return resolvido;
    }

    public void setResolvido(boolean resolvido) {
        this.resolvido = resolvido;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public TipoArquivo getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(TipoArquivo tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public CriticidadeBug getCriticidade() {
        return criticidade;
    }

    public void setCriticidade(CriticidadeBug criticidade) {
        this.criticidade = criticidade;
    }

    public SessaoModel getSessao() {
        return sessao;
    }

    public void setSessao(SessaoModel sessao) {
        this.sessao = sessao;
    }

    // Método auxiliar para compatibilidade
    public String getCaminhoImagem() {
        return TipoArquivo.IMAGEM.equals(tipoArquivo) ? caminhoArquivo : null;
    }

    public String getCaminhoVideo() {
        return TipoArquivo.VIDEO.equals(tipoArquivo) ? caminhoArquivo : null;
    }
}