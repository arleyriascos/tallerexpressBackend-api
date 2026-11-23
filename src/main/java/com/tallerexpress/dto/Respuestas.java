package com.tallerexpress.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.tallerexpress.model.CambioDeEstado;
import com.tallerexpress.model.NivelUrgencia;
import com.tallerexpress.model.Rol;

/** Cuerpos de respuesta que no son entidades del dominio. */
public final class Respuestas {

    private Respuestas() {
    }

    public record Sesion(String token, String tipo, LocalDateTime expira, String nombreUsuario, String nombreCompleto, Rol rol) {
    }

    public record EstadoActual(String numero, String estado, NivelUrgencia urgencia, String placa, String mecanico,
            LocalDateTime fechaRecepcion, List<CambioDeEstado> historial) {
    }

    public record Mensaje(String mensaje) {
    }
}
