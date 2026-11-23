package com.tallerexpress.model;

import java.time.LocalDateTime;

public class CambioDeEstado {

    private EstadoOrden estadoAnterior;
    private EstadoOrden estadoNuevo;
    private LocalDateTime fechaHora;
    private String motivo;
    private String usuarioId;
    private String usuarioNombre;

    public CambioDeEstado() {
    }

    public CambioDeEstado(EstadoOrden estadoAnterior, EstadoOrden estadoNuevo, String motivo, String usuarioId, String usuarioNombre, LocalDateTime fechaHora) {
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.motivo = motivo;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.fechaHora = fechaHora;
    }

    public EstadoOrden getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoOrden estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoOrden getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoOrden estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

}
