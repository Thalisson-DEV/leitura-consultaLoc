package br.sipel.leituraconsultaloc.service;

import br.sipel.leituraconsultaloc.exception.ResourceNotFoundException;
import br.sipel.leituraconsultaloc.model.Cliente;
import br.sipel.leituraconsultaloc.repositories.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // AJUSTE: Mude o retorno de void para long
    public long importarCsvEmLotes(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String linha;
            boolean primeiraLinha = true;
            List<Cliente> lote = new ArrayList<>();
            final int TAMANHO_LOTE = 1000;

            while ((linha = reader.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                String[] campos = linha.split(",");
                if (campos.length < 7) {
                    continue;
                }

                Cliente cliente = new Cliente();
                cliente.setIdInstalacao(Long.parseLong(campos[0].trim()));
                cliente.setContaContrato(campos[1].trim());
                cliente.setNumeroSerie(campos[2].trim());
                cliente.setNumeroPoste(campos[3].trim());
                cliente.setNomeCliente(campos[4].trim());
                cliente.setLongitude(Double.parseDouble(campos[5].trim()));
                cliente.setLatitude(Double.parseDouble(campos[6].trim()));

                lote.add(cliente);

                if (lote.size() == TAMANHO_LOTE) {
                    clienteRepository.saveAll(lote);
                    lote.clear();
                }
            }

            if (!lote.isEmpty()) {
                clienteRepository.saveAll(lote);
            }
        }
        // AJUSTE: Retorne a contagem total de registros
        return clienteRepository.count();
    }

    /**
     * Busca um cliente pelo seu ID (chave primária: idInstalacao).
     * @param instalacao O ID da instalação.
     * @return O objeto Cliente encontrado.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    public Cliente buscarPorInstalacao(long instalacao) {
        // CORREÇÃO: Usando o método padrão findById() para buscar pela chave primária.
        // Este é o método correto e mais eficiente para essa operação.
        return clienteRepository.findById((int) instalacao)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para a instalação: " + instalacao));
    }

    // --- MÉTODO NOVO NECESSÁRIO ---
    /**
     * Busca um cliente pelo número da conta contrato.
     * @param contaContrato O número da conta contrato.
     * @return O objeto Cliente encontrado.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    public Cliente buscarPorContaContrato(String contaContrato) {
        return clienteRepository.findByContaContrato(contaContrato)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para a conta contrato: " + contaContrato));
    }

    // --- MÉTODO NOVO NECESSÁRIO ---
    /**
     * Busca um cliente pelo número da conta contrato.
     * @param nomeCliente O número da conta contrato.
     * @return O objeto Cliente encontrado.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    public Cliente buscarPorNomeCliente(String nomeCliente) {
        return clienteRepository.findByNomeCliente(nomeCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o nome: " + nomeCliente));
    }

    // --- MÉTODO NOVO NECESSÁRIO ---
    /**
     * Busca um cliente pelo número da serie do medidor.
     * @param numeroSerie O número de serie do medidor.
     * @return O objeto Cliente encontrado.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    public Cliente buscarPorNumeroSerie(String numeroSerie) {
        return clienteRepository.findByNumeroSerie(numeroSerie)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o N° de Série: " + numeroSerie));
    }

    // --- MÉTODO NOVO NECESSÁRIO ---
    /**
     * Busca por clientes com um determinado número de poste e retorna o primeiro encontrado.
     * Ideal para casos onde o número do poste não é uma chave única.
     * @param numeroPoste O número do poste a ser buscado.
     * @return O primeiro objeto Cliente encontrado que corresponde ao critério.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    public Cliente buscarPorNumeroPoste(String numeroPoste) {
        // 1. O repositório agora retorna uma lista de clientes.
        List<Cliente> clientesEncontrados = clienteRepository.findByNumeroPoste(numeroPoste);

        // 2. Verifica se a lista está vazia.
        if (clientesEncontrados.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum cliente encontrado para o número do poste: " + numeroPoste);
        }

        // 3. Se a lista não estiver vazia, retorna apenas o primeiro elemento.
        return clientesEncontrados.get(0);
    }
}