package br.com.unisenaisc.cadastro_produtos.controller;

import br.com.unisenaisc.cadastro_produtos.model.Usuario;
import br.com.unisenaisc.cadastro_produtos.repository.dto.NovoUsuario;
import br.com.unisenaisc.cadastro_produtos.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> criarUsuario(@RequestBody NovoUsuario novoUsuario){
        Usuario salvar = usuarioService.salvar(novoUsuario);

        return ResponseEntity.ok(salvar);
    }

}
