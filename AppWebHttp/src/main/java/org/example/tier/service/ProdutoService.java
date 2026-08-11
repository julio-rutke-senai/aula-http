package org.example.tier.service;

import org.example.tier.controller.dto.ProdutoResponseDTO;
import org.example.tier.model.Produto;
import org.example.tier.repository.ProdutoRepository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProdutoService {

    ProdutoRepository produtoRepository = new ProdutoRepository();

    Pattern nomePattern = Pattern.compile(
            "\"nome\"\\s*:\\s*\"([^\"]+)\""
    );

    Pattern precoPattern = Pattern.compile(
            "\"preco\"\\s*:\\s*(\\d+(?:\\.\\d+)?)"
    );

    Pattern estoquePattern = Pattern.compile(
            "\"estoque\"\\s*:\\s*(\\d+)"
    );

    public void cadastrar(Produto produto){
        produtoRepository.addProduto(produto);
    }

    public ProdutoResponseDTO cadastrar(String requestBody){
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

        if (!possuiNome || !possuiPreco || !possuiEstoque) {

            ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO();
            produtoResponseDTO.setStatus(400);
            produtoResponseDTO.setReasonPhrase("Bad Request");
            produtoResponseDTO.setResponseBody("""
                    {"erro":"Informe nome, preco e estoque"}
                    """.trim());

            return produtoResponseDTO;

        } else {
            Produto produto = produtoRepository.addProduto(nomeMatcher.group(1),
                    Double.parseDouble(
                            precoMatcher.group(1)
                    ),
                    Integer.parseInt(
                            estoqueMatcher.group(1)
                    ));
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

}
