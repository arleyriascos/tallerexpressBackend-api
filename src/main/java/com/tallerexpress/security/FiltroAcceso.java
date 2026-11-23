package com.tallerexpress.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tallerexpress.exception.ApiException;
import com.tallerexpress.model.Usuario;
import com.tallerexpress.service.AccesoService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de la capa de seguridad: toda petición a /api (salvo el inicio de sesión)
 * debe traer "Authorization: Bearer &lt;token&gt;". El usuario autenticado queda en
 * un atributo de la petición para que los controladores validen su rol.
 */
@Component
public class FiltroAcceso extends OncePerRequestFilter {

    public static final String ATRIBUTO_USUARIO = "tallerexpress.usuario";
    public static final String ATRIBUTO_TOKEN = "tallerexpress.token";

    private final AccesoService acceso;

    public FiltroAcceso(AccesoService acceso) {
        this.acceso = acceso;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String ruta = request.getRequestURI();
        return !ruta.startsWith("/api/")
                || ruta.equals("/api/acceso/iniciar-sesion")
                || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String cabecera = request.getHeader("Authorization");
        if (cabecera == null || !cabecera.startsWith("Bearer ")) {
            rechazar(response, 401, "Falta el encabezado Authorization: Bearer <token>");
            return;
        }
        String token = cabecera.substring(7).trim();
        try {
            Usuario usuario = acceso.usuarioDeToken(token);
            request.setAttribute(ATRIBUTO_USUARIO, usuario);
            request.setAttribute(ATRIBUTO_TOKEN, token);
        } catch (ApiException e) {
            rechazar(response, e.getEstado(), e.getMessage());
            return;
        }
        chain.doFilter(request, response);
    }

    private void rechazar(HttpServletResponse response, int estado, String mensaje) throws IOException {
        response.setStatus(estado);
        response.setContentType("application/json;charset=UTF-8");
        String limpio = mensaje.replace("\\", "\\\\").replace("\"", "\\\"");
        response.getWriter().write("{\"estado\":" + estado + ",\"error\":\"" + limpio + "\"}");
    }
}
