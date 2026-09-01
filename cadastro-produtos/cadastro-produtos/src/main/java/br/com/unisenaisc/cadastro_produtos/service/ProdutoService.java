package br.com.unisenaisc.cadastro_produtos.service;

import br.com.unisenaisc.cadastro_produtos.controller.dto.ProdutoRequestDTO;
import br.com.unisenaisc.cadastro_produtos.controller.dto.ProdutoResponseDTO;
import br.com.unisenaisc.cadastro_produtos.model.Produto;
import br.com.unisenaisc.cadastro_produtos.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    Pattern nomePattern = Pattern.compile(
            "\"nome\"\\s*:\\s*\"([^\"]+)\""
    );

    Pattern precoPattern = Pattern.compile(
            "\"preco\"\\s*:\\s*(\\d+(?:\\.\\d+)?)"
    );

    Pattern estoquePattern = Pattern.compile(
            "\"estoque\"\\s*:\\s*(\\d+)"
    );

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public void cadastrar(Produto produto){
        produtoRepository.addProduto(produto);
    }

    public ProdutoResponseDTO cadastrar(ProdutoRequestDTO produtoDTO){

        if (produtoDTO.getNome().isEmpty() || Optional.ofNullable(produtoDTO.getPreco()).isEmpty() || Optional.ofNullable(produtoDTO.getEstoque()).isEmpty()) {

            ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO();
            produtoResponseDTO.setStatus(400);
            produtoResponseDTO.setReasonPhrase("Bad Request");
            produtoResponseDTO.setResponseBody("""
                    {"erro":"Informe nome, preco e estoque"}
                    """.trim());

            return produtoResponseDTO;

        } else {
            Produto produto = produtoRepository.addProduto(produtoDTO.getNome(),
                    BigDecimal.valueOf(produtoDTO.getPreco()),
                    produtoDTO.getEstoque());
            ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO();
            produtoResponseDTO.setStatus(201);
            produtoResponseDTO.setReasonPhrase("Created");
            produtoResponseDTO.setResponseBody(produto.toJson());

            return produtoResponseDTO;
        }

    }

    public List<Produto> listarProdutos(){
        return produtoRepository.getProdutos();
    }

    public ProdutoResponseDTO buscarPorId(long id){

        Produto encontrado = listarProdutos().stream()
                .filter(
                        produto ->
                                produto.getId() == id
                )
                .findFirst()
                .orElse(null);

        if (encontrado == null) {
            ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO();
            produtoResponseDTO.setStatus(404);
            produtoResponseDTO.setReasonPhrase("Not Found");
            produtoResponseDTO.setResponseBody("""
                    {"erro":"Produto não encontrado"}
                    """.trim());
            return produtoResponseDTO;
        } else {
            ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO();
            produtoResponseDTO.setStatus(200);
            produtoResponseDTO.setReasonPhrase("OK");
            produtoResponseDTO.setResponseBody(encontrado.toJson());

            return produtoResponseDTO;
        }
    }

    public ProdutoResponseDTO atualizar(long id, String requestBody){

        List<Produto> produtos = produtoRepository.getProdutos();

        int index = -1;

        for (
                int i = 0;
                i < produtos.size();
                i++
        ) {

            if (produtos.get(i).getId() == id) {
                index = i;
                break;
            }
        }

        Matcher nomeMatcher =
                nomePattern.matcher(requestBody);

        Matcher precoMatcher =
                precoPattern.matcher(requestBody);

        Matcher estoqueMatcher =
                estoquePattern.matcher(requestBody);

        boolean possuiNome =
                nomeMatcher.find();

        boolean possuiPreco =
                precoMatcher.find();

        boolean possuiEstoque =
                estoqueMatcher.find();

        if (index < 0) {

            ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO();
            produtoResponseDTO.setStatus(404);
            produtoResponseDTO.setReasonPhrase("Not Found");
            produtoResponseDTO.setResponseBody("""
                    {"erro":"Produto não encontrado"}
                    """.trim());

            return produtoResponseDTO;

        } else if (!possuiNome || !possuiPreco || !possuiEstoque) {

            ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO();
            produtoResponseDTO.setStatus(400);
            produtoResponseDTO.setReasonPhrase("Bad Request");
            produtoResponseDTO.setResponseBody("""
                    {"erro":"Informe nome, preco e estoque"}
                    """.trim());

            return produtoResponseDTO;

        } else {

            Produto atualizado =
                    new Produto(
                            id,
                            nomeMatcher.group(1),
                            BigDecimal.valueOf(Double.parseDouble(precoMatcher.group(1))),
                            Integer.parseInt(
                                    estoqueMatcher.group(1)
                            )
                    );

            produtos.set(index, atualizado);

            ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO();
            produtoResponseDTO.setStatus(200);
            produtoResponseDTO.setReasonPhrase("OK");
            produtoResponseDTO.setResponseBody(atualizado.toJson());

            return produtoResponseDTO;
        }

    }

    public ProdutoResponseDTO excluir(long id, String requestBody) {

        List<Produto> produtos = produtoRepository.getProdutos();

        boolean removido = produtos.removeIf(
                produto -> produto.getId() == id
        );

        if (removido) {

            ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO();
            produtoResponseDTO.setStatus(204);
            produtoResponseDTO.setReasonPhrase("No Content");
            produtoResponseDTO.setResponseBody("");

            return produtoResponseDTO;

        } else {

            ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO();
            produtoResponseDTO.setStatus(404);
            produtoResponseDTO.setReasonPhrase("Not Found");
            produtoResponseDTO.setResponseBody("""
                    {"erro":"Produto não encontrado"}
                    """.trim());

            return produtoResponseDTO;
        }

    }
}
