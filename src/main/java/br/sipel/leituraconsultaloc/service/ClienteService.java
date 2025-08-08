package br.sipel.leituraconsultaloc.service;

import br.sipel.leituraconsultaloc.dto.EstatisticasDTO;
import br.sipel.leituraconsultaloc.dto.ImportacaoResponseDTO;
import br.sipel.leituraconsultaloc.exception.ResourceNotFoundException;
import br.sipel.leituraconsultaloc.model.Cliente;
import br.sipel.leituraconsultaloc.repositories.ClienteRepository;
import jakarta.validation.constraints.NotNull;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ImportacaoResponseDTO importarClientes(@NotNull MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("O arquivo enviado está vazio.");
        }

        List<Cliente> clientesParaSalvar = new ArrayList<>();
        List<String> erros = new ArrayList<>();
        int linhaAtual = 1;

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) {
                rows.next(); // pula cabeçalho
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                linhaAtual++;

                try {
                    String idInstalacaoStr = getStringCellValue(currentRow.getCell(0));
                    if (idInstalacaoStr.isBlank()) {
                        continue; // pula linhas vazias
                    }

                    Long idInstalacao;
                    try {
                        idInstalacao = Long.valueOf(idInstalacaoStr);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("ID de instalação inválido");
                    }

                    if (clienteRepository.findByIdInstalacao(idInstalacao).isPresent()) {
                        throw new RuntimeException("ID de instalação já cadastrado");
                    }

                    String contaContrato = getStringCellValue(currentRow.getCell(2));
                    String numeroSerie = getStringCellValue(currentRow.getCell(3));
                    String numeroPoste = getStringCellValue(currentRow.getCell(4));
                    String nomeCliente = getStringCellValue(currentRow.getCell(5));
                    String longitudeStr = getStringCellValue(currentRow.getCell(6));
                    String latitudeStr = getStringCellValue(currentRow.getCell(7));

                    Double longitude, latitude;
                    try {
                        longitude = Double.parseDouble(longitudeStr);
                        latitude = Double.parseDouble(latitudeStr);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Coordenadas inválidas");
                    }

                    Cliente novoCliente = new Cliente();
                    novoCliente.setIdInstalacao(idInstalacao);
                    novoCliente.setContaContrato(contaContrato);
                    novoCliente.setNumeroSerie(numeroSerie);
                    novoCliente.setNumeroPoste(numeroPoste);
                    novoCliente.setNomeCliente(nomeCliente);
                    novoCliente.setLongitude(longitude);
                    novoCliente.setLatitude(latitude);

                    clientesParaSalvar.add(novoCliente);

                } catch (Exception e) {
                    erros.add("Linha " + linhaAtual + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Falha ao ler o arquivo Excel: " + e.getMessage());
        }

        if (!clientesParaSalvar.isEmpty()) {
            clienteRepository.saveAll(clientesParaSalvar);
        }

        return new ImportacaoResponseDTO(clientesParaSalvar.size(), erros.size(), erros);
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return "";
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