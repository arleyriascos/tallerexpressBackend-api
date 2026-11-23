package com.tallerexpress.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Document(collection = "usuarios")
public abstract class Usuario {

    @Id
    private String id;
    @Indexed(unique = true)
    private String nombreUsuario;
    @JsonIgnore
    private String contrasena;
    private String nombreCompleto;
    private Rol rol;

    public Usuario() {
    }

    protected Usuario(String nombreUsuario, String contrasena, String nombreCompleto, Rol rol) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    private static final BCryptPasswordEncoder CODIFICADOR = new BCryptPasswordEncoder();

    /** Verifica las credenciales contra la contraseña guardada (cifrada con BCrypt). */
    public boolean iniciarSesion(String nombreUsuario, String contrasena) {
        return this.nombreUsuario != null && this.nombreUsuario.equals(nombreUsuario)
                && contrasena != null && CODIFICADOR.matches(contrasena, this.contrasena);
    }

    /** Cifra una contraseña en texto plano con BCrypt antes de guardarla. */
    public static String cifrar(String contrasenaPlana) {
        return CODIFICADOR.encode(contrasenaPlana);
    }

    /** La sesión vive en AccesoService; el usuario no guarda estado de sesión. */
    public void cerrarSesion() {
    }

}
