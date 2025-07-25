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

    @Autowired
    private ClienteService clienteService;

    /**
     * Endpoint para importar clientes a partir de um arquivo CSV.
     * @param file O arquivo CSV enviado na requisição.
     * @return Uma resposta com status 200 (OK) e uma mensagem de sucesso ou 500 em caso de erro.
     */
    @PostMapping("/importar/csv")
    public ResponseEntity<String> importarClientes(@RequestParam("file") MultipartFile file) {
        try {
            long totalRegistros = clienteService.importarCsvEmLotes(file);
            return ResponseEntity.ok("Importação realizada com sucesso! Total de registros no sistema: " + totalRegistros);
        } catch (Exception e) {
            // O GlobalExceptionHandler pode pegar isso, mas um catch aqui é bom para erros de I/O.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o arquivo CSV: " + e.getMessage());
        }
    }

    /**
     * Redireciona para o Google Maps com base no número da instalação do cliente.
     * @param instalacao O número da instalação do cliente.
     * @return ResponseEntity com status 302 (Found) para redirecionamento ou 404 se não encontrado.
     */
    @GetMapping("/instalacao/{instalacao}/redirecionar-maps")
    public ResponseEntity<Void> redirecionarPorInstalacao(@PathVariable long instalacao) {
        // CORREÇÃO: Removido 'throws Throwable'
        Cliente cliente = clienteService.buscarPorInstalacao(instalacao);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Redireciona para o Google Maps com base no número da conta contrato do cliente.
     * @param contaContrato O número da conta contrato do cliente.
     * @return ResponseEntity com status 302 (Found) para redirecionamento ou 404 se não encontrado.
     */
    @GetMapping("/conta-contrato/{contaContrato}/redirecionar-maps")
    public ResponseEntity<Void> redirecionarPorContaContrato(@PathVariable String contaContrato) {
        Cliente cliente = clienteService.buscarPorContaContrato(contaContrato);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Redireciona para o Google Maps com base no número de série do medidor.
     * @param numeroSerie O número de série do medidor.
     * @return ResponseEntity com status 302 (Found) para redirecionamento ou 404 se não encontrado.
     */
    @GetMapping("/numero-serie/{numeroSerie}/redirecionar-maps")
    public ResponseEntity<Void> redirecionarPorNumeroSerie(@PathVariable String numeroSerie) {
        Cliente cliente = clienteService.buscarPorNumeroSerie(numeroSerie);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Redireciona para o Google Maps com base no nome do cliente.
     * @param nomeCliente O nome do cliente a ser buscado.
     * @return ResponseEntity com status 302 (Found) para redirecionamento ou 404 se não encontrado.
     */
    @GetMapping("/nome/{nomeCliente}/redirecionar-maps") // CORREÇÃO: URL padronizada
    public ResponseEntity<Void> redirecionarPorNomeCliente(@PathVariable String nomeCliente) {
        Cliente cliente = clienteService.buscarPorNomeCliente(nomeCliente);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Redireciona para o Google Maps com base no número do poste.
     * @param numeroPoste O número do poste a ser buscado.
     * @return ResponseEntity com status 302 (Found) para redirecionamento ou 404 se não encontrado.
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
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
