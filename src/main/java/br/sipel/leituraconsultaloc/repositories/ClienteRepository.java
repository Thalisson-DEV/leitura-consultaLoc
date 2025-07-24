package br.sipel.leituraconsultaloc.repositories;

import br.sipel.leituraconsultaloc.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional findByIdInstalacao(Long idInstalacao);

    Optional<Cliente> findByContaContrato(String contaContrato);

    Optional<Cliente> findByNumeroSerie(String numeroSerie);

    List<Cliente> findByNumeroPoste(String numeroPoste);

    Optional<Cliente> findByNomeCliente(String nomeCliente);

}
