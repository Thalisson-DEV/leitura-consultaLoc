package br.sipel.leituraconsultaloc.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ImportacaoResponseDTO {
    private int sucesso;
    private int erros;
    private List<String> listaErros;

    public ImportacaoResponseDTO(int sucesso, int erros, List<String> listaErros) {
        this.sucesso = sucesso;
        this.erros = erros;
        this.listaErros = listaErros;
    }
}

