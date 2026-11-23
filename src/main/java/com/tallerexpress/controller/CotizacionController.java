package com.tallerexpress.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tallerexpress.dto.Peticiones;
import com.tallerexpress.dto.Respuestas;
import com.tallerexpress.model.*;
import com.tallerexpress.security.SesionActual;
import com.tallerexpress.service.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Cotizaciones")
@RestController
@RequestMapping("/api/ordenes/{ordenId}/cotizaciones")
public class CotizacionController {

    private final CotizacionService servicio;
    private final AccesoService acceso;

    public CotizacionController(CotizacionService servicio, AccesoService acceso) {
        this.servicio = servicio;
        this.acceso = acceso;
    }

    @Operation(summary = "Generar la cotización del diagnóstico: (mano de obra + repuestos) × factor de urgencia")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdenReparacion generarCotizacion(HttpServletRequest req, @PathVariable String ordenId) {
        Usuario u = SesionActual.usuario(req);
        acceso.validarRol(u, Rol.ADMINISTRADOR, Rol.RECEPCIONISTA);
        return servicio.generarCotizacion(u, ordenId);
    }

    @Operation(summary = "Consultar las cotizaciones de la orden (historial)")
    @GetMapping
    public List<Cotizacion> consultar(HttpServletRequest req, @PathVariable String ordenId) {
        return servicio.consultarCotizaciones(SesionActual.usuario(req), ordenId);
    }

    @Operation(summary = "Aprobar la cotización vigente")
    @PostMapping("/aprobar")
    public OrdenReparacion aprobarCotizacion(HttpServletRequest req, @PathVariable String ordenId) {
        Usuario u = SesionActual.usuario(req);
        acceso.validarRol(u, Rol.ADMINISTRADOR, Rol.RECEPCIONISTA, Rol.CLIENTE);
        return servicio.aprobarCotizacion(u, ordenId);
    }

    @Operation(summary = "Rechazar la cotización vigente; la orden queda en Rechazado")
    @PostMapping("/rechazar")
    public OrdenReparacion rechazarCotizacion(HttpServletRequest req, @PathVariable String ordenId,
            @Valid @RequestBody Peticiones.Rechazo p) {
        Usuario u = SesionActual.usuario(req);
        acceso.validarRol(u, Rol.ADMINISTRADOR, Rol.RECEPCIONISTA, Rol.CLIENTE);
        return servicio.rechazarCotizacion(u, ordenId, p.motivo());
    }
}
