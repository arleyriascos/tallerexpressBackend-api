package com.tallerexpress.exception;

/**
 * Error de negocio o de acceso con el código HTTP que debe devolver la API.
 * Se mantiene independiente de Spring para que el dominio pueda lanzarlo.
 */
public class ApiException extends RuntimeException {

    private final int estado;

    public ApiException(int estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public int getEstado() {
        return estado;
    }

    public static ApiException solicitudInvalida(String mensaje) {
        return new ApiException(400, mensaje);
    }

    public static ApiException noAutenticado(String mensaje) {
        return new ApiException(401, mensaje);
    }

    public static ApiException prohibido(String mensaje) {
        return new ApiException(403, mensaje);
    }

    public static ApiException noEncontrado(String mensaje) {
        return new ApiException(404, mensaje);
    }

    public static ApiException conflicto(String mensaje) {
        return new ApiException(409, mensaje);
    }
}
