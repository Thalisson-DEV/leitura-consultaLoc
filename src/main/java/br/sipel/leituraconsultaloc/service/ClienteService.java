package br.sipel.leituraconsultaloc.service;

import br.sipel.leituraconsultaloc.dto.EstatisticasDTO;
import br.sipel.leituraconsultaloc.exception.FileImportException;
import br.sipel.leituraconsultaloc.exception.ResourceNotFoundException;
import br.sipel.leituraconsultaloc.model.Cliente;
import br.sipel.leituraconsultaloc.repositories.ClienteRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.cache.annotation.Cacheable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private static final int BATCH_SIZE = 1000; // Define o tamanho de cada lote

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public long importarEAtualizar(MultipartFile file) throws IOException {
        List<Cliente> clientesDoArquivo;
        String filename = file.getOriginalFilename();

        if (filename != null && (filename.endsWith(".xlsx"))) {
            clientesDoArquivo = lerXlsx(file);
        } else if (filename != null && filename.endsWith(".csv")) {
            clientesDoArquivo = lerCsv(file);
        } else {
            throw new IllegalArgumentException("Formato de arquivo não suportado. Use .csv ou .xlsx");
        }

        if (clientesDoArquivo.isEmpty()) {
            return clienteRepository.count();
        }

        // Processa a lista de clientes em lotes para evitar o erro de limite de parâmetros
        for (int i = 0; i < clientesDoArquivo.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, clientesDoArquivo.size());
            List<Cliente> batch = clientesDoArquivo.subList(i, end);
            processarLote(batch);
        }

        return clienteRepository.count();
    }

    private void processarLote(List<Cliente> lote) {
        Map<Long, Cliente> mapaClientesArquivo = lote.stream()
                .collect(Collectors.toMap(Cliente::getIdInstalacao, Function.identity(), (existente, novo) -> novo));

        List<Long> idsDoLote = new ArrayList<>(mapaClientesArquivo.keySet());
        List<Cliente> clientesExistentes = clienteRepository.findAllById(idsDoLote);

        Map<Long, Cliente> mapaClientesExistentes = clientesExistentes.stream()
                .collect(Collectors.toMap(Cliente::getIdInstalacao, Function.identity()));

        List<Cliente> listaParaSalvar = new ArrayList<>();

        for (Cliente clienteDoArquivo : lote) {
            Cliente clienteExistente = mapaClientesExistentes.get(clienteDoArquivo.getIdInstalacao());
            if (clienteExistente != null) {
                // Se o cliente existe, atualiza os dados e o adiciona para salvar
                clienteExistente.setLatitude(clienteDoArquivo.getLatitude());
                clienteExistente.setLongitude(clienteDoArquivo.getLongitude());
                listaParaSalvar.add(clienteExistente);
            } else {
                // Se o cliente não existe, o adiciona para ser inserido
                listaParaSalvar.add(clienteDoArquivo);
            }
        }

        // Salva o lote de clientes (atualizados e novos)
        if (!listaParaSalvar.isEmpty()) {
            clienteRepository.saveAll(listaParaSalvar);
        }
    }

    // Seus métodos de leitura (lerCsv, lerXlsx, criarCliente) e de busca permanecem aqui...
    private List<Cliente> lerCsv(MultipartFile file) throws IOException {
        List<Cliente> clientes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String linha;
            int numeroLinha = 1;
            reader.readLine(); // Pula o cabeçalho

            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                String[] campos = linha.split(",");
                if (campos.length >= 7) {
                    try {
                        clientes.add(criarCliente(campos));
                    } catch (Exception e) {
                        throw new FileImportException("Erro ao processar a linha " + numeroLinha + " do CSV: " + e.getMessage());
                    }
                }
            }
        }
        return clientes;
    }

    private List<Cliente> lerXlsx(MultipartFile file) throws IOException {
        List<Cliente> clientes = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int numeroLinha = 0;
            for (Row row : sheet) {
                numeroLinha++;
                if (numeroLinha == 1) continue; // Pula o cabeçalho

                try {
                    String[] campos = new String[7];
                    for (int i = 0; i < 7; i++) {
                        campos[i] = getCellStringValue(row.getCell(i));
                    }
                    clientes.add(criarCliente(campos));
                } catch (Exception e) {
                    throw new FileImportException("Erro ao processar a linha " + numeroLinha + " do XLSX: " + e.getMessage());
                }
            }
        }
        return clientes;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.format("%.0f", cell.getNumericCellValue());
        }
        return cell.toString().trim();
    }

    private Cliente criarCliente(String[] campos) {
        Cliente cliente = new Cliente();
        cliente.setIdInstalacao(Long.parseLong(campos[0].trim()));
        cliente.setContaContrato(campos[1].trim());
        cliente.setNumeroSerie(campos[2].trim());
        cliente.setNumeroPoste(campos[3].trim());
        cliente.setNomeCliente(campos[4].trim());
        cliente.setLongitude(Double.parseDouble(campos[5].trim().replace(",", ".")));
        cliente.setLatitude(Double.parseDouble(campos[6].trim().replace(",", ".")));
        return cliente;
    }



    /**
     * Busca um cliente pelo seu ID (chave primária: idInstalacao).
     * @param instalacao O ID da instalação.
     * @return O objeto Cliente encontrado.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    @Cacheable("clientes")
    public Cliente buscarPorInstalacao(long instalacao) {
        return clienteRepository.findById(instalacao)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para a instalação: " + instalacao));
    }

    /**
     * Busca um cliente pelo número da conta contrato.
     * @param contaContrato O número da conta contrato.
     * @return O objeto Cliente encontrado.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    @Cacheable("clientes")
    public Cliente buscarPorContaContrato(String contaContrato) {
        return clienteRepository.findByContaContrato(contaContrato)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para a conta contrato: " + contaContrato));
    }

    /**
     * Busca um cliente pelo número da conta contrato.
     * @param nomeCliente O número da conta contrato.
     * @return O objeto Cliente encontrado.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    @Cacheable("clientes")
    public Cliente buscarPorNomeCliente(String nomeCliente) {
        return clienteRepository.findByNomeCliente(nomeCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o nome: " + nomeCliente));
    }

    /**
     * Busca um cliente pelo número da serie do medidor.
     * @param numeroSerie O número de serie do medidor.
     * @return O objeto Cliente encontrado.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    @Cacheable("clientes")
    public Cliente buscarPorNumeroSerie(String numeroSerie) {
        return clienteRepository.findByNumeroSerie(numeroSerie)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o N° de Série: " + numeroSerie));
    }

    /**
     * Busca por clientes com um determinado número de poste e retorna o primeiro encontrado.
     * Ideal para casos onde o número do poste não é uma chave única.
     * @param numeroPoste O número do poste a ser buscado.
     * @return O primeiro objeto Cliente encontrado que corresponde ao critério.
     * @throws ResourceNotFoundException se nenhum cliente for encontrado.
     */
    @Cacheable("clientes")
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