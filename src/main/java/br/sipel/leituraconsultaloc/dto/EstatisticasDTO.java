package br.sipel.leituraconsultaloc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para estatísticas exibidas no dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstatisticasDTO {
    private long totalClientes;
    private long buscasRecentes;
    private long postesMapeados;
    private long clientesImportados;
}
