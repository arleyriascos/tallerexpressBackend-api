package com.tallerexpress.model;

import java.util.ArrayList;
import java.util.List;

public class Mecanico extends Usuario {

    private String telefono;
    private String documentoIdentidad;
    private List<String> especialidades;

    public Mecanico() {
    }

    public Mecanico(String nombreUsuario, String contrasena, String nombreCompleto, String telefono, String documentoIdentidad, List<String> especialidades) {
        super(nombreUsuario, contrasena, nombreCompleto, Rol.MECANICO);
        this.telefono = telefono;
        this.documentoIdentidad = documentoIdentidad;
        this.especialidades = new ArrayList<>(especialidades);
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public List<String> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<String> especialidades) {
        this.especialidades = especialidades;
    }

    public boolean tieneEspecialidad(String especialidad) {
        return especialidades != null && especialidades.contains(especialidad);
    }

    /** El mecánico solicita escalar su orden a otro especialista (HU-12). */
    public SolicitudEscalamiento solicitarEscalamiento(OrdenReparacion orden, String especialidad, String motivo) {
        SolicitudEscalamiento solicitud = new SolicitudEscalamiento(especialidad, motivo, getId());
        orden.agregarSolicitudEscalamiento(solicitud);
        return solicitud;
    }

}
