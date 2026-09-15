package br.com.unisenaisc.cadastro_produtos.repository;

import br.com.unisenaisc.cadastro_produtos.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    Page<Produto> findByNome(Pageable pageable, String nome);

}
