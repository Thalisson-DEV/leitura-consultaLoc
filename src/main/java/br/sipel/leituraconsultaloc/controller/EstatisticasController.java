package br.sipel.leituraconsultaloc.controller;

import br.sipel.leituraconsultaloc.dto.EstatisticasDTO;
import br.sipel.leituraconsultaloc.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para fornecer estatísticas para o dashboard
 */
@RestController
@RequestMapping("/api/v1/clientes")
@CrossOrigin(origins = "*")
public class EstatisticasController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Retorna estatísticas gerais sobre clientes para o dashboard
     * @return DTO com estatísticas
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<EstatisticasDTO> obterEstatisticas() {
        EstatisticasDTO estatisticas = clienteService.obterEstatisticas();
        return ResponseEntity.ok(estatisticas);
    }
}
