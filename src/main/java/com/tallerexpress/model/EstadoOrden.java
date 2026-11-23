package com.tallerexpress.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Estados de una orden y transiciones permitidas (RF-16 / HU-16).
 * Lista cerrada; no se saltan estados y el único retroceso es
 * En reparación → En diagnóstico. Entregado y Rechazado son finales.
 */
public enum EstadoOrden {
    RECIBIDO("Recibido"),
    EN_DIAGNOSTICO("En diagnóstico"),
    COTIZADO("Cotizado"),
    APROBADO("Aprobado"),
    EN_REPARACION("En reparación"),
    LISTO_PARA_ENTREGA("Listo para entrega"),
    ENTREGADO("Entregado"),
    RECHAZADO("Rechazado");

    private final String nombre;

    EstadoOrden(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    /** Estados a los que se puede pasar desde este. */
    public Set<EstadoOrden> siguientesPermitidos() {
        switch (this) {
            case RECIBIDO:
                return EnumSet.of(EN_DIAGNOSTICO);
            case EN_DIAGNOSTICO:
                return EnumSet.of(COTIZADO);
            case COTIZADO:
                return EnumSet.of(APROBADO, RECHAZADO, EN_DIAGNOSTICO);
            case APROBADO:
                return EnumSet.of(EN_REPARACION);
            case EN_REPARACION:
                return EnumSet.of(LISTO_PARA_ENTREGA, EN_DIAGNOSTICO);
            case LISTO_PARA_ENTREGA:
                return EnumSet.of(ENTREGADO);
            default:
                return EnumSet.noneOf(EstadoOrden.class);
        }
    }

    public boolean puedePasarA(EstadoOrden destino) {
        return siguientesPermitidos().contains(destino);
    }

    public boolean esFinal() {
        return this == ENTREGADO || this == RECHAZADO;
    }

    /** Verdadero si la cotización de la orden ya fue aprobada (Aprobado o posterior). */
    public boolean esAprobadoOPosterior() {
        return this == APROBADO || this == EN_REPARACION || this == LISTO_PARA_ENTREGA || this == ENTREGADO;
    }

    public static EstadoOrden desdeTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        String normal = java.text.Normalizer.normalize(texto.trim(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toUpperCase().replace(' ', '_');
        for (EstadoOrden e : values()) {
            if (e.name().equals(normal)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado desconocido: " + texto);
    }
}
