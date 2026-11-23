package com.tallerexpress.model;

public class Recepcionista extends Usuario {


    public Recepcionista() {
    }

    public Recepcionista(String nombreUsuario, String contrasena, String nombreCompleto) {
        super(nombreUsuario, contrasena, nombreCompleto, Rol.RECEPCIONISTA);
    }

    /** Crea la orden de trabajo de una recepción (HU-04). */
    public OrdenReparacion registrarRecepcion(Vehiculo vehiculo, String numero, String motivoIngreso,
            String sintomas, NivelUrgencia urgencia) {
        return new OrdenReparacion(numero, vehiculo.getId(), vehiculo.getClienteId(), motivoIngreso, sintomas,
                urgencia, getId(), getNombreCompleto());
    }

}
