package com.tallerexpress.model;

public class Administrador extends Usuario {


    public Administrador() {
    }

    public Administrador(String nombreUsuario, String contrasena, String nombreCompleto) {
        super(nombreUsuario, contrasena, nombreCompleto, Rol.ADMINISTRADOR);
    }

}
