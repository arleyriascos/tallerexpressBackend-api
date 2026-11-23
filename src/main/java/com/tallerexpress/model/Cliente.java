package com.tallerexpress.model;

public class Cliente extends Usuario {

    private String telefono;
    private String documentoIdentidad;
    private String email;
    private String direccion;

    public Cliente() {
    }

    public Cliente(String nombreUsuario, String contrasena, String nombreCompleto, String telefono, String documentoIdentidad) {
        super(nombreUsuario, contrasena, nombreCompleto, Rol.CLIENTE);
        this.telefono = telefono;
        this.documentoIdentidad = documentoIdentidad;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

}
