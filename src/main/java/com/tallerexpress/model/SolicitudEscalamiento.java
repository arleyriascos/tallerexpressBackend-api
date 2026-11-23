package com.tallerexpress.model;

import com.tallerexpress.util.Reloj;
import java.time.LocalDateTime;
import java.util.UUID;

public class SolicitudEscalamiento {

    private String id;
    private LocalDateTime fecha;
    private String motivo;
    private String especialidad;
    private EstadoSolicitud estado;
    private String solicitanteId;
    private String especialistaId;

    public SolicitudEscalamiento() {
    }

    public SolicitudEscalamiento(String especialidad, String motivo, String solicitanteId) {
        this.id = UUID.randomUUID().toString();
        this.fecha = Reloj.ahora();
        this.especialidad = especialidad;
        this.motivo = motivo;
        this.solicitanteId = solicitanteId;
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public String getSolicitanteId() {
        return solicitanteId;
    }

    public void setSolicitanteId(String solicitanteId) {
        this.solicitanteId = solicitanteId;
    }

    public String getEspecialistaId() {
        return especialistaId;
    }

    public void setEspecialistaId(String especialistaId) {
        this.especialistaId = especialistaId;
    }

    public void asignarEspecialista(String especialistaId) {
        this.especialistaId = especialistaId;
        this.estado = EstadoSolicitud.ASIGNADA;
    }

}
