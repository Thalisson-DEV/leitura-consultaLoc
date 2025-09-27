package br.sipel.leituraconsultaloc.controller;

import br.sipel.leituraconsultaloc.model.CoordenadasClientes;
import br.sipel.leituraconsultaloc.service.ClienteService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * Redireciona para o Google Maps com base no número da instalação do cliente.
     * @param instalacao O número da instalação do cliente.
     * @return ResponseEntity com status 302 (Found) para redirecionamento ou 404 se não encontrado.
     */
    @GetMapping("/instalacao/{instalacao}/redirecionar-maps")
    public ResponseEntity<Void> redirecionarPorInstalacao(@PathVariable Long instalacao) {
        CoordenadasClientes cliente = clienteService.buscarPorInstalacao(instalacao);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Redireciona para o Google Maps com base no número da conta contrato do cliente.
     * @param contaContrato O número da conta contrato do cliente.
     * @return ResponseEntity com status 302 (Found) para redirecionamento ou 404 se não encontrado.
     */
    @GetMapping("/conta-contrato/{contaContrato}/redirecionar-maps")
    public ResponseEntity<Void> redirecionarPorContaContrato(@PathVariable String contaContrato) {
        CoordenadasClientes cliente = clienteService.buscarPorContaContrato(contaContrato);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Redireciona para o Google Maps com base no número de série do medidor.
     * @param numeroSerie O número de série do medidor.
     * @return ResponseEntity com status 302 (Found) para redirecionamento ou 404 se não encontrado.
     */
    @GetMapping("/numero-serie/{numeroSerie}/redirecionar-maps")
    public ResponseEntity<Void> redirecionarPorNumeroSerie(@PathVariable String numeroSerie) {
        CoordenadasClientes cliente = clienteService.buscarPorNumeroSerie(numeroSerie);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Redireciona para o Google Maps com base no nome do cliente.
     * @param nomeCliente O nome do cliente a ser buscado.
     * @return ResponseEntity com status 302 (Found) para redirecionamento ou 404 se não encontrado.
     */
    @GetMapping("/nome/{nomeCliente}/redirecionar-maps") // CORREÇÃO: URL padronizada
    public ResponseEntity<Void> redirecionarPorNomeCliente(@PathVariable String nomeCliente) {
        CoordenadasClientes cliente = clienteService.buscarPorNomeCliente(nomeCliente);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Redireciona para o Google Maps com base no número do poste.
     * @param numeroPoste O número do poste a ser buscado.
     * @return ResponseEntity com status 302 (Found) para redirecionamento ou 404 se não encontrado.
     */
    @GetMapping("/numero-poste/{numeroPoste}/redirecionar-maps")
    public ResponseEntity<Void> redirecionarPorNumeroPoste(@PathVariable String numeroPoste) {
        CoordenadasClientes cliente = clienteService.buscarPorNumeroPoste(numeroPoste);
        return criarRespostaDeRedirecionamento(cliente);
    }

    /**
     * Método auxiliar para evitar duplicação de código na criação da resposta de redirecionamento.
     * @param cliente O cliente encontrado com dados de latitude e longitude.
     * @return Um ResponseEntity configurado para redirecionar o usuário.
     */
    private ResponseEntity<Void> criarRespostaDeRedirecionamento(CoordenadasClientes cliente) {
        String mapsUrl = String.format("https://www.google.com/maps?q=%f,%f", cliente.getLatitude(), cliente.getLongitude());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(mapsUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
