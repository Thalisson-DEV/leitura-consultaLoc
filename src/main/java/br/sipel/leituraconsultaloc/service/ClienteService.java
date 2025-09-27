package br.sipel.leituraconsultaloc.service;

import br.sipel.leituraconsultaloc.exception.ResourceNotFoundException;
import br.sipel.leituraconsultaloc.model.CoordenadasClientes;
import br.sipel.leituraconsultaloc.repositories.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Busca um cliente pelo seu ID (chave primária: idInstalacao).
     * @param instalacao O ID da instalação.
     * @return O objeto CoordenadasClientes encontrado.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    @Cacheable("clientes")
    public CoordenadasClientes buscarPorInstalacao(Long instalacao) {
        return clienteRepository.findByIdInstalacao(instalacao)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para a instalação: " + instalacao));
    }

    /**
     * Busca um cliente pelo número da conta contrato.
     * @param contaContrato O número da conta contrato.
     * @return O objeto CoordenadasClientes encontrado.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    @Cacheable("clientes")
    public CoordenadasClientes buscarPorContaContrato(String contaContrato) {

        String contaContratoFormatada = contaContrato;

        if (!contaContrato.endsWith(".0")) {
            contaContratoFormatada = contaContrato + ".0";
        }

        // Query com a conta contrato formatada
        return clienteRepository.findByContaContratoIgnoreCase(contaContratoFormatada)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para a conta contrato: " + contaContrato));
    }

    /**
     * Busca um cliente pelo número da conta contrato.
     * @param nomeCliente O número da conta contrato.
     * @return O objeto CoordenadasClientes encontrado.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    @Cacheable("clientes")
    public CoordenadasClientes buscarPorNomeCliente(String nomeCliente) {
        return clienteRepository.findByNomeClienteStartingWithIgnoreCase(nomeCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o nome do cliente: " + nomeCliente));
    }

    /**
     * Busca um cliente pelo número da serie do medidor.
     * @param numeroSerie O número de serie do medidor.
     * @return O objeto CoordenadasClientes encontrado.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    @Cacheable("clientes")
    public CoordenadasClientes buscarPorNumeroSerie(String numeroSerie) {

        String numeroSerieFormatado = numeroSerie;

        if (!numeroSerie.endsWith(".0")) {
            numeroSerieFormatado = numeroSerie + ".0";
        }

        return clienteRepository.findByNumeroSerieIgnoreCase(numeroSerieFormatado)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o N° de Série: " + numeroSerie));
    }

    /**
     * Busca por clientes com um determinado número de poste e retorna o primeiro encontrado.
     * @param numeroPoste O número do poste a ser buscado.
     * @return O objeto CoordenadasClientes encontrado.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    @Cacheable("clientes")
    public CoordenadasClientes buscarPorNumeroPoste(String numeroPoste) {
        return clienteRepository.findByNumeroPoste(numeroPoste)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o N° de Poste: " + numeroPoste));
    }
}