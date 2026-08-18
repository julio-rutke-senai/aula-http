package br.com.unisenaisc.cadastro_produtos.repository;

import br.com.unisenaisc.cadastro_produtos.model.Produto;

import java.util.ArrayList;
import java.util.List;

public class ProdutoRepository {

    List<Produto> produtos = new ArrayList<>();

    long proximoId = 3;

    public List<Produto> getProdutos(){
        return produtos;
    }

    public Produto addProduto(String nome, double preco, int estoque){
        Produto produto = new Produto(
                proximoId++,
                nome,
                preco,
                estoque
        );
        produtos.add(produto);
        return produto;
    }

    public Produto addProduto(Produto produto) {
        Produto produtoNovo = new Produto(
                proximoId++,
                produto.getNome(),
                produto.getPreco(),
                produto.getEstoque()
        );
        produtos.add(produtoNovo);
        return produtoNovo;
    }
}
