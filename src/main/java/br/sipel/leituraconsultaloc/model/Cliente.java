package br.sipel.leituraconsultaloc.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "cliente")
@Table(name = "clientes")
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long idInstalacao;
    private String contaContrato;
    private String numeroSerie;
    private String numeroPoste;
    private String nomeCliente;
    private double longitude;
    private double latitude;


    public Cliente(){

    }

    public Cliente(Long idInstalacao, String contaContrato, String numeroSerie, String numeroPoste, String nomeCliente, double longitude, double latitude) {
        this.idInstalacao = idInstalacao;
        this.contaContrato = contaContrato;
        this.numeroSerie = numeroSerie;
        this.numeroPoste = numeroPoste;
        this.nomeCliente = nomeCliente;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Long getIdInstalacao() {
        return idInstalacao;
    }

    public void setIdInstalacao(Long idInstalacao) {
        this.idInstalacao = idInstalacao;
    }

    public String getContaContrato() {
        return contaContrato;
    }

    public void setContaContrato(String contaContrato) {
        this.contaContrato = contaContrato;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getNumeroPoste() {
        return numeroPoste;
    }

    public void setNumeroPoste(String numeroPoste) {
        this.numeroPoste = numeroPoste;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
