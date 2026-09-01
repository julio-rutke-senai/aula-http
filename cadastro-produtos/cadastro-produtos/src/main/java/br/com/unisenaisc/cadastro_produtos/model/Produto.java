package br.com.unisenaisc.cadastro_produtos.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Locale;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;
    @Column(nullable = false)
    private Integer estoque;

    public Produto() {
    }

    public Produto(Long id, String nome, BigDecimal preco, Integer estoque) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    public Produto(String nome, BigDecimal preco, Integer estoque) {
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    public String toJson() {
        return String.format(
                Locale.US,
                """
                {"id":%d,"nome":"%s","preco":%.2f,"estoque":%d}
                """,
                id,
                nome,
                preco,
                estoque
        ).trim();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void atualizar(Produto produto) {
        this.nome = produto.getNome();
        this.estoque = produto.getEstoque();
        this.preco = produto.getPreco();
    }
}
