package br.com.unisenaisc.cadastro_produtos.controller;

import br.com.unisenaisc.cadastro_produtos.controller.dto.ProdutoRequestDTO;
import br.com.unisenaisc.cadastro_produtos.controller.dto.ProdutoResponseDTO;
import br.com.unisenaisc.cadastro_produtos.model.Produto;
import br.com.unisenaisc.cadastro_produtos.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produto")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService){
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<Produto>> buscarProdutos(){
        List<Produto> produtos = produtoService.listarProdutos();
        return ResponseEntity.ok(produtos);
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody ProdutoRequestDTO produtoRequestDTO){
        ProdutoResponseDTO produtoResponseDTO = produtoService.cadastrar(produtoRequestDTO);
        return ResponseEntity.ok(produtoResponseDTO.getResponseBody());
    }

}
