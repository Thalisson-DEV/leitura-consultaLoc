package br.sipel.leituraconsultaloc.service;

import br.sipel.leituraconsultaloc.dto.EstatisticasDTO;
import br.sipel.leituraconsultaloc.dto.ImportacaoResponseDTO;
import br.sipel.leituraconsultaloc.exception.ResourceNotFoundException;
import br.sipel.leituraconsultaloc.infra.config.ExcelHelper;
import br.sipel.leituraconsultaloc.infra.config.ImportJobService;
import br.sipel.leituraconsultaloc.infra.config.ImportJobStatus;
import br.sipel.leituraconsultaloc.model.Cliente;
import br.sipel.leituraconsultaloc.repositories.ClienteRepository;
import com.monitorjbl.xlsx.StreamingReader;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Adicione este novo método. Ele é transacional para garantir a integridade dos dados.
    @Transactional
    public long importarEAtualizar(MultipartFile file) throws IOException {
        List<Cliente> clientesDoArquivo = new ArrayList<>();

        // Detecta o tipo de arquivo e chama o método de leitura apropriado
        String filename = file.getOriginalFilename();
        if (filename != null && filename.endsWith(".xlsx")) {
            clientesDoArquivo = lerXlsx(file);
        } else if (filename != null && filename.endsWith(".csv")) {
            clientesDoArquivo = lerCsv(file);
        } else {
            throw new IllegalArgumentException("Formato de arquivo não suportado. Use .csv ou .xlsx");
        }

        if (clientesDoArquivo.isEmpty()) {
            return clienteRepository.count();
        }

        // 1. Cria um Mapa dos clientes do arquivo para acesso rápido pela chave primária
        Map<Long, Cliente> mapaClientesArquivo = clientesDoArquivo.stream()
                .collect(Collectors.toMap(Cliente::getIdInstalacao, Function.identity(), (existente, novo) -> novo));

        // 2. Busca no banco TODOS os clientes que já existem com as chaves do arquivo (MUITO eficiente)
        List<Cliente> clientesExistentes = clienteRepository.findAllById(mapaClientesArquivo.keySet());

        // 3. Itera sobre os clientes que já existem e atualiza a latitude e longitude
        for (Cliente existente : clientesExistentes) {
            Cliente dadosNovos = mapaClientesArquivo.get(existente.getIdInstalacao());
            if (dadosNovos != null) {
                existente.setLatitude(dadosNovos.getLatitude());
                existente.setLongitude(dadosNovos.getLongitude());
                // Remove do mapa para que apenas os novos clientes permaneçam
                mapaClientesArquivo.remove(existente.getIdInstalacao());
            }
        }

        // 4. Prepara a lista final para salvar:
        //    - clientesExistentes (que foram atualizados)
        //    - os valores restantes no mapa (que são os novos clientes a serem inseridos)
        List<Cliente> listaParaSalvar = new ArrayList<>(clientesExistentes);
        listaParaSalvar.addAll(mapaClientesArquivo.values());

        // 5. Salva tudo de uma vez. O Spring Data JPA é inteligente e fará UPDATEs e INSERTs conforme necessário.
        clienteRepository.saveAll(listaParaSalvar);

        return clienteRepository.count();
    }

    // Método auxiliar para ler CSV
    private List<Cliente> lerCsv(MultipartFile file) throws IOException {
        List<Cliente> clientes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String linha;
            boolean primeiraLinha = true;
            while ((linha = reader.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }
                String[] campos = linha.split(",");
                if (campos.length >= 7) {
                    clientes.add(criarCliente(campos));
                }
            }
        }
        return clientes;
    }

    // Método auxiliar para ler XLSX
    private List<Cliente> lerXlsx(MultipartFile file) throws IOException {
        List<Cliente> clientes = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean primeiraLinha = true;
            for (Row row : sheet) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }
                String[] campos = new String[7];
                for (int i = 0; i < 7; i++) {
                    campos[i] = row.getCell(i) != null ? row.getCell(i).toString() : "";
                }
                clientes.add(criarCliente(campos));
            }
        }
        return clientes;
    }

    // Método auxiliar para criar um objeto Cliente a partir dos campos
    private Cliente criarCliente(String[] campos) {
        Cliente cliente = new Cliente();
        cliente.setIdInstalacao((long) Double.parseDouble(campos[0].trim()));
        cliente.setContaContrato(campos[1].trim());
        cliente.setNumeroSerie(campos[2].trim());
        cliente.setNumeroPoste(campos[3].trim());
        cliente.setNomeCliente(campos[4].trim());
        cliente.setLongitude(Double.parseDouble(campos[5].trim()));
        cliente.setLatitude(Double.parseDouble(campos[6].trim()));
        return cliente;
    }


    // Lê uma célula como String, tratando nulos e tipos diferentes
    public static String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            // Se for número, converte para String removendo .0 se for inteiro
            double val = cell.getNumericCellValue();
            if (val == (long) val) {
                return String.valueOf((long) val);
            }
            return String.valueOf(val);
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == CellType.FORMULA) {
            try {
                return cell.getStringCellValue().trim();
            } catch (IllegalStateException e) {
                try {
                    double val = cell.getNumericCellValue();
                    return String.valueOf(val);
                } catch (Exception ex) {
                    return "";
                }
            }
        }

        return "";
    }

    // Converte para Double de forma segura
    public static Double parseDoubleSafe(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else {
                String str = getStringCellValue(cell);
                if (str.isEmpty()) return null;
                return Double.parseDouble(str.replace(",", "."));
            }
        } catch (NumberFormatException e) {
            return null;
        }
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
        return clienteRepository.findById(instalacao)
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

    /**
     * Obtém estatísticas gerais do sistema para exibição no dashboard
     * @return Objeto com as estatísticas calculadas
     */
    public EstatisticasDTO obterEstatisticas() {
        // Contagem total de clientes no sistema
        long totalClientes = clienteRepository.count();

        return EstatisticasDTO.builder()
                .totalClientes(totalClientes)
                .build();
    }
}