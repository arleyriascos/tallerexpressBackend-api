package com.tallerexpress.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.tallerexpress.dto.Respuestas;
import com.tallerexpress.exception.ApiException;
import com.tallerexpress.model.Rol;
import com.tallerexpress.model.Usuario;
import com.tallerexpress.repository.UsuarioRepository;
import com.tallerexpress.util.Reloj;

/**
 * Autenticación y control de acceso por rol (HU-23).
 * Las sesiones viven en memoria con un token aleatorio y vencen a las 8 horas.
 */
@Service
public class AccesoService {

    private static final Duration DURACION = Duration.ofHours(8);

    private record SesionInterna(String usuarioId, LocalDateTime expira) {
    }

    private final UsuarioRepository usuarios;
    private final Map<String, SesionInterna> sesiones = new ConcurrentHashMap<>();
    private final SecureRandom azar = new SecureRandom();

    public AccesoService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public Respuestas.Sesion autenticar(String nombreUsuario, String contrasena) {
        Usuario usuario = usuarios.findByNombreUsuario(nombreUsuario)
                .filter(u -> u.iniciarSesion(nombreUsuario, contrasena))
                .orElseThrow(() -> ApiException.noAutenticado("Usuario o contraseña incorrectos"));
        byte[] bytes = new byte[32];
        azar.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        LocalDateTime expira = Reloj.ahora().plus(DURACION);
        sesiones.put(token, new SesionInterna(usuario.getId(), expira));
        return new Respuestas.Sesion(token, "Bearer", expira, usuario.getNombreUsuario(),
                usuario.getNombreCompleto(), usuario.getRol());
    }

    /** Devuelve el usuario dueño del token o lanza 401 si no existe o venció. */
    public Usuario usuarioDeToken(String token) {
        SesionInterna sesion = token == null ? null : sesiones.get(token);
        if (sesion == null || sesion.expira().isBefore(Reloj.ahora())) {
            if (sesion != null) {
                sesiones.remove(token);
            }
            throw ApiException.noAutenticado("Sesión inválida o vencida");
        }
        return usuarios.findById(sesion.usuarioId())
                .orElseThrow(() -> ApiException.noAutenticado("El usuario de la sesión ya no existe"));
    }

    public void cerrarSesion(String token) {
        if (token != null) {
            sesiones.remove(token);
        }
    }

    /** Lanza 403 si el rol del usuario no está entre los permitidos. */
    public void validarRol(Usuario usuario, Rol... permitidos) {
        for (Rol r : permitidos) {
            if (usuario.getRol() == r) {
                return;
            }
        }
        throw ApiException.prohibido("Su rol (" + usuario.getRol() + ") no tiene permiso para esta operación");
    }
}
