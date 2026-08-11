package org.example.tier.controller;

import org.example.tier.controller.dto.ProdutoResponseDTO;
import org.example.tier.model.Produto;
import org.example.tier.service.ProdutoService;

import java.util.regex.Matcher;

public class ProdutoController {

    public ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    public ProdutoResponseDTO cadastrar(String requestBody){
        ProdutoResponseDTO produtoResponseDTO = produtoService.cadastrar(requestBody);
        return produtoResponseDTO;
    }

    public String listarProdutos(){
        String responseBody = produtoService.listarProdutos().stream()
                .map(Produto::toJson)
                .reduce(
                        (left, right) ->
                                left + "," + right
                )
                .map(json -> "[" + json + "]")
                .orElse("[]");

        return responseBody;
    }

    public ProdutoResponseDTO buscarProdutoPorCodigo(long id){
        ProdutoResponseDTO produtoResponseDTO = produtoService.buscarPorId(id);
        return produtoResponseDTO;
    }

    public ProdutoResponseDTO handler(String method, String path, String requestBody){
        if (method.equals("GET")) {
            ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO(this.listarProdutos());
            return produtoResponseDTO;

        } else if (method.equals("GET") && path.matches("/produtos/\\d+")) {
            long id = Long.parseLong(
                    path.substring(
                            "/produtos/".length()
                    )
            );

            ProdutoResponseDTO produtoResponseDTO = this.buscarProdutoPorCodigo(id);

            return produtoResponseDTO;

        } else if (method.equals("POST")) {

            ProdutoResponseDTO produtoResponseDTO = this.cadastrar(requestBody);

            return produtoResponseDTO;

        } else if (
                method.equals("PUT")
                        && path.matches("/produtos/\\d+")
        ) {
            long id = Long.parseLong(
                    path.substring(
                            "/produtos/".length()
                    )
            );

            int index = -1;

            for (
                    int i = 0;
                    i < produtos.size();
                    i++
            ) {

                if (produtos.get(i).id() == id) {
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

                status = 404;
                reasonPhrase = "Not Found";

                responseBody =
                        """
                        {"erro":"Produto não encontrado"}
                        """.trim();

            } else if (!possuiNome || !possuiPreco || !possuiEstoque) {

                status = 400;
                reasonPhrase = "Bad Request";

                responseBody =
                        """
                        {"erro":"Informe nome, preco e estoque"}
                        """.trim();

            } else {

                Produto atualizado =
                        new Produto(
                                id,
                                nomeMatcher.group(1),
                                Double.parseDouble(
                                        precoMatcher.group(1)
                                ),
                                Integer.parseInt(
                                        estoqueMatcher.group(1)
                                )
                        );

                produtos.set(index, atualizado);

                responseBody =
                        atualizado.toJson();
            }

        } else if (method.equals("DELETE") && path.matches("/produtos/\\d+")) {

            long id = Long.parseLong(
                    path.substring(
                            "/produtos/".length()
                    )
            );

            boolean removido = produtos.removeIf(
                    produto -> produto.id() == id
            );

            if (removido) {

                status = 204;
                reasonPhrase = "No Content";
                responseBody = "";

            } else {

                status = 404;
                reasonPhrase = "Not Found";

                responseBody =
                        """
                        {"erro":"Produto não encontrado"}
                        """.trim();
            }

        } else if (
                path.equals("/produtos") || path.startsWith("/produtos/")
        ) {

            status = 405;
            reasonPhrase =
                    "Method Not Allowed";

            responseBody =
                    """
                    {"erro":"Método não permitido"}
                    """.trim();

        } else {

            status = 404;
            reasonPhrase = "Not Found";

            responseBody =
                    """
                    {"erro":"Caminho não encontrado"}
                    """.trim();
        }

    }

}
