package br.com.unisenaisc.cadastro_produtos.service;

import br.com.unisenaisc.cadastro_produtos.model.Usuario;
import br.com.unisenaisc.cadastro_produtos.repository.UsuarioRepository;
import br.com.unisenaisc.cadastro_produtos.repository.dto.NovoUsuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

//    private final UsuarioRepository usuarioRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
//        this.usuarioRepository = usuarioRepository;
//        this.passwordEncoder = passwordEncoder;
//    }

    public Usuario salvar(NovoUsuario novoUsuario){
//        Usuario usuario = new Usuario();
//        usuario.setNome(novoUsuario.getNome());
//        usuario.setEmail(novoUsuario.getEmail());
//        usuario.setRole(novoUsuario.getRole());
//        String senhaCrypt = passwordEncoder.encode(novoUsuario.getPassword());
//        usuario.setPassword(senhaCrypt);
//        return usuarioRepository.save(usuario);
        return null;
    }

    public Optional<Usuario> getUsuarioAutenticacao(String email){
//        return usuarioRepository.findByEmail(email);
        return null;
    }


}
