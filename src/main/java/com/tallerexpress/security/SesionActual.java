package com.tallerexpress.security;

import com.tallerexpress.model.Usuario;

import jakarta.servlet.http.HttpServletRequest;

/** Acceso al usuario autenticado que dejó el filtro en la petición. */
public final class SesionActual {

    private SesionActual() {
    }

    public static Usuario usuario(HttpServletRequest request) {
        return (Usuario) request.getAttribute(FiltroAcceso.ATRIBUTO_USUARIO);
    }

    public static String token(HttpServletRequest request) {
        return (String) request.getAttribute(FiltroAcceso.ATRIBUTO_TOKEN);
    }
}
