package br.sipel.leituraconsultaloc.dto;

import java.util.List;

public record ImportacaoResponseDTO(int sucesso, int falhas, List<String> erros) {
}
