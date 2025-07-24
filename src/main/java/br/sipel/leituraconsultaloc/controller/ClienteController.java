package br.sipel.leituraconsultaloc.controller;

import br.sipel.leituraconsultaloc.model.Cliente;
import br.sipel.leituraconsultaloc.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

/**
 * Controller para gerir as operações relacionadas aos Clientes.
 * As URLs seguem o padrão RESTful e são versionadas.
 * A responsabilidade deste controller é apenas de orquestrar as chamadas para a camada de serviço
 * e formatar as respostas HTTP.
 */
@RestController
@RequestMapping("/api/v1/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    // Injetando apenas a camada de serviço, como deve ser.
    @Autowired
    private ClienteService clienteService;

    /**
     * Endpoint para importar clientes a partir de um arquivo CSV.
     * @param file O arquivo CSV enviado na requisição.
     * @return Uma resposta com status 200 (OK) e uma mensagem de sucesso ou 500 em caso de erro.
     */
    @PostMapping("/importar/csv")
    public ResponseEntity<String> importarClientes(@RequestParam("file") MultipartFile file) {
        // A lógica de try-catch foi movida para um handler global para erros mais específicos.
        // Mantemos um try-catch geral aqui para a operação de arquivo.
        try {
            long totalImportado = clienteService.importarCsvEmLotes(file);
            return ResponseEntity.ok("Importação realizada com sucesso! Total de registros no sistema: " + totalImportado);
        } catch (Exception e) {
            // Idealmente, criar exceções mais específicas para falhas de importação.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o arquivo CSV: " + e.getMessage());
        }
    }

    /**
     * Redireciona para o Google Maps com base no número da instalação do cliente.
     * Usa @PathVariable para um endpoint RESTful mais limpo.
     * Exemplo de URL: GET /api/v1/clientes/instalacao/12345678/redirecionar-maps
     *
     * @param instalacao O número da instalação do cliente.
     * @return ResponseEntity com status 302 (Found) para redirecionamento.
     */
    @GetMapping("/instalacao/{instalacao}/redirecionar-maps")
    public ResponseEntity<Void> redirecionarPorInstalacao(@PathVariable long instalacao) throws Throwable {
        // A lógica de busca e verificação de "não encontrado" agora está no serviço.
        // Se o cliente não for encontrado, o serviço lançará uma exceção que será
        // tratada pelo GlobalExceptionHandler, retornando um 404.
        Cliente cliente = clienteService.buscarPorInstalacao(instalacao);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Redireciona para o Google Maps com base no número da conta contrato do cliente.
     * Usa @PathVariable para um endpoint RESTful mais limpo.
     * Exemplo de URL: GET /api/v1/clientes/conta-contrato/ABC987/redirecionar-maps
     *
     * @param contaContrato O número da conta contrato do cliente.
     * @return ResponseEntity com status 302 (Found) para redirecionamento.
     */
    @GetMapping("/conta-contrato/{contaContrato}/redirecionar-maps")
    public ResponseEntity<Void> redirecionarPorContaContrato(@PathVariable String contaContrato) {
        Cliente cliente = clienteService.buscarPorContaContrato(contaContrato);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Redireciona para o Google Maps com base no número da conta contrato do cliente.
     * Usa @PathVariable para um endpoint RESTful mais limpo.
     * Exemplo de URL: GET /api/v1/clientes/conta-contrato/ABC987/redirecionar-maps
     *
     * @param numeroSerie O número da conta contrato do cliente.
     * @return ResponseEntity com status 302 (Found) para redirecionamento.
     */
    @GetMapping("/numero-serie/{numeroSerie}/redirecionar-maps")
    public ResponseEntity<Void> redirecionarPorNumeroSerie(@PathVariable String numeroSerie) {
        Cliente cliente = clienteService.buscarPorNumeroSerie(numeroSerie);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Redireciona para o Google Maps com base no número da conta contrato do cliente.
     * Usa @PathVariable para um endpoint RESTful mais limpo.
     * Exemplo de URL: GET /api/v1/clientes/conta-contrato/ABC987/redirecionar-maps
     *
     * @param nomeCliente O número da conta contrato do cliente.
     * @return ResponseEntity com status 302 (Found) para redirecionamento.
     */
    @GetMapping("/nome-cliente/{nomeCliente}/redirecionar-maps")
    public ResponseEntity<Void> redirecionarPorNomeCliente(@PathVariable String nomeCliente) {
        Cliente cliente = clienteService.buscarPorNomeCliente(nomeCliente);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Redireciona para o Google Maps com base no número da conta contrato do cliente.
     * Usa @PathVariable para um endpoint RESTful mais limpo.
     * Exemplo de URL: GET /api/v1/clientes/conta-contrato/ABC987/redirecionar-maps
     *
     * @param numeroPoste O número da conta contrato do cliente.
     * @return ResponseEntity com status 302 (Found) para redirecionamento.
     */
    @GetMapping("/numero-poste/{numeroPoste}/redirecionar-maps")
    public ResponseEntity<Void> redirecionarPorNumeroPoste(@PathVariable String numeroPoste) {
        Cliente cliente = clienteService.buscarPorNumeroPoste(numeroPoste);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Método auxiliar para evitar duplicação de código na criação da resposta de redirecionamento.
     * @param cliente O cliente encontrado com dados de latitude e longitude.
     * @return Um ResponseEntity configurado para redirecionar o usuário.
     */
    private ResponseEntity<Void> criarRespostaDeRedirecionamento(Cliente cliente) {
        String mapsUrl = String.format("https://www.google.com/maps?q=%s,%s", cliente.getLatitude(), cliente.getLongitude());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(mapsUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND); // FOUND (302) é o status correto para redirecionamento.
    }
}
