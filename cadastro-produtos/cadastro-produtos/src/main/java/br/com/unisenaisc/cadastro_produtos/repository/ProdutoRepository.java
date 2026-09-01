package br.com.unisenaisc.cadastro_produtos.repository;

import br.com.unisenaisc.cadastro_produtos.model.Produto;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProdutoRepository {

    private final EntityManager entityManager;

    List<Produto> produtos = new ArrayList<>();

    long proximoId = 3;

    public ProdutoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Produto> getProdutos(){
        List<Produto> produtos = entityManager.createQuery("select p from Produto p", Produto.class).getResultList();
        return produtos;
    }

    public Produto buscarPorCodigo(Long id){
        return entityManager.find(Produto.class, id);
    }

    public Produto addProduto(String nome, BigDecimal preco, Integer estoque){
        Produto produto = new Produto(
                nome,
                preco,
                estoque
        );
        produto = this.addProduto(produto);
        return produto;
    }

    public Produto addProduto(Produto produto) {
        entityManager.persist(produto);
        return produto;
    }

    public Produto alterarProduto(Produto produto){
        Produto produtoPersistido = entityManager.find(Produto.class, produto.getId());

        produtoPersistido.atualizar(produto);

        entityManager.merge(produtoPersistido);

        return produtoPersistido;
    }

}
