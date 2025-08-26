package br.sipel.leituraconsultaloc.repositories;

import br.sipel.leituraconsultaloc.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional findByIdInstalacao(Long idInstalacao);

    Optional<Cliente> findByContaContrato(String contaContrato);

    @Query(
            nativeQuery = true,
            value = " SELECT c.* FROM clientes c " +
                    "WHERE (:numeroSerie IS NULL OR LOWER(CAST(c.numero_serie AS TEXT)) LIKE LOWER(CONCAT('%', :numeroSerie, '%')))"
    )
    Optional<Cliente> findByNumeroSerie(
            @Param("numeroSerie") String numeroSerie
    );

    List<Cliente> findByNumeroPoste(String numeroPoste);

    @Query(
            nativeQuery = true,
            value = "SELECT c.* FROM clientes c " +
                    "WHERE (:nomeCliente IS NULL OR LOWER(CAST(c.nome_cliente AS TEXT)) LIKE LOWER(CONCAT('%', :nomeCliente, '%')))"
    )
    List<Cliente> findByNomeCliente(
            @Param("nomeCliente") String nomeCliente
    );
}