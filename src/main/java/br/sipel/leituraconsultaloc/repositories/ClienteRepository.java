package br.sipel.leituraconsultaloc.repositories;

import br.sipel.leituraconsultaloc.model.Cliente;
import br.sipel.leituraconsultaloc.model.CoordenadasClientes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<CoordenadasClientes> findByIdInstalacao(Long idInstalacao);

    Optional<CoordenadasClientes> findByContaContratoIgnoreCase(String contaContrato);

    Optional<CoordenadasClientes> findByNumeroSerieIgnoreCase(String numeroSerie);

    Optional<CoordenadasClientes> findByNomeClienteStartingWithIgnoreCase(String nomeCliente);

    Optional<CoordenadasClientes> findByNumeroPoste(String numeroPoste);
}