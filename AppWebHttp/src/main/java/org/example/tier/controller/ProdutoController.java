package org.example.tier.controller;

import org.example.tier.controller.dto.ProdutoResponseDTO;
import org.example.tier.model.Produto;
import org.example.tier.service.ProdutoService;

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

            ProdutoResponseDTO produtoResponseDTO = this.atualizar(id, requestBody);

            return produtoResponseDTO;

        } else if (method.equals("DELETE") && path.matches("/produtos/\\d+")) {

            long id = Long.parseLong(
                    path.substring(
                            "/produtos/".length()
                    )
            );

            ProdutoResponseDTO produtoResponseDTO = this.excluir(id, requestBody);

            return produtoResponseDTO;

        } else if (
                path.equals("/produtos") || path.startsWith("/produtos/")
        ) {

            ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO();

            produtoResponseDTO.setStatus(405);
            produtoResponseDTO.setReasonPhrase("Method Not Allowed");
            produtoResponseDTO.setResponseBody("""
                    {"erro":"Método não permitido"}
                    """.trim());
            return produtoResponseDTO;

        } else {

            ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO();

            produtoResponseDTO.setStatus(404);
            produtoResponseDTO.setReasonPhrase("Not Found");
            produtoResponseDTO.setResponseBody("""
                    {"erro":"Caminho não encontrado"}
                    """.trim());

            return produtoResponseDTO;
        }

    }

    private ProdutoResponseDTO excluir(long id, String requestBody) {
        return produtoService.excluir(id, requestBody);
    }

    private ProdutoResponseDTO atualizar(long id, String requestBody) {
        return produtoService.atualizar(id, requestBody);
    }

}
