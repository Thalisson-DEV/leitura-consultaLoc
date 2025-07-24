package br.sipel.leituraconsultaloc.exception;

/**
 * Exceção customizada para ser lançada quando um recurso (como um Cliente)
 * não é encontrado no banco de dados.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}