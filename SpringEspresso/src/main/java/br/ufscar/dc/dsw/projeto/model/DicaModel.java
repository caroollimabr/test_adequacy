package br.ufscar.dc.dsw.projeto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dicas")
public class DicaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String texto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estrategia_id")
    private EstrategiaModel estrategia;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public EstrategiaModel getEstrategia() { return estrategia; }
    public void setEstrategia(EstrategiaModel estrategia) { this.estrategia = estrategia; }
}