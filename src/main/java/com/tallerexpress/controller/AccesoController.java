package com.tallerexpress.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tallerexpress.dto.Peticiones;
import com.tallerexpress.dto.Respuestas;
import com.tallerexpress.security.SesionActual;
import com.tallerexpress.service.AccesoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Acceso", description = "Inicio y cierre de sesión")
@RestController
@RequestMapping("/api/acceso")
public class AccesoController {

    private final AccesoService acceso;

    public AccesoController(AccesoService acceso) {
        this.acceso = acceso;
    }

    @Operation(summary = "Iniciar sesión y obtener el token Bearer")
    @PostMapping("/iniciar-sesion")
    public Respuestas.Sesion iniciarSesion(@Valid @RequestBody Peticiones.InicioSesion p) {
        return acceso.autenticar(p.nombreUsuario(), p.contrasena());
    }

    @Operation(summary = "Cerrar la sesión actual")
    @PostMapping("/cerrar-sesion")
    public Respuestas.Mensaje cerrarSesion(HttpServletRequest request) {
        acceso.cerrarSesion(SesionActual.token(request));
        return new Respuestas.Mensaje("Sesión cerrada");
    }
}
