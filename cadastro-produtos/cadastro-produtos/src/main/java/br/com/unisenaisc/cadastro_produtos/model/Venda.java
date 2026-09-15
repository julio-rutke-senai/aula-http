package br.com.unisenaisc.cadastro_produtos.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToMany
    @JoinTable(name = "produtos_vendas",
            joinColumns = @JoinColumn(name = "venda"),
            inverseJoinColumns = @JoinColumn(name = "produto"))
    private List<Produto> produtos;

}
