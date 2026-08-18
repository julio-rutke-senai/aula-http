package br.com.unisenaisc.cadastro_produtos.model;

import java.util.Locale;

public class Produto {

    private long id;
    private String nome;
    private double preco;
    private int estoque;

    public Produto(long id, String nome, double preco, int estoque) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    public Produto(String nome, double preco, int estoque) {
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

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getEstoque() {
        return estoque;
    }
}
