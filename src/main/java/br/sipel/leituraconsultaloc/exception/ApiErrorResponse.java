package br.sipel.leituraconsultaloc.exception;

/**
 * Uma classe simples (POJO) para padronizar as respostas de erro em JSON.
 * O nome foi alterado para ApiErrorResponse para evitar conflito com a interface
 * ErrorResponse do próprio Spring Framework.
 */
public class ApiErrorResponse {
    private String error;
    private String message;

    // Construtor padrão exigido por alguns frameworks.
    public ApiErrorResponse() {
    }

    // Construtor para criar a resposta de erro.
    public ApiErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    // Getters e Setters
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
