package br.sipel.leituraconsultaloc.dto;

public record ClienteDto(int idInstalacao, int contaContrato, int numeroSerie, String numeroPoste, String nomeCliente, double longitude, double latitude) {
}
