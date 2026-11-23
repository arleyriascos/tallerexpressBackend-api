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

@Tag(name = "Órdenes", description = "Recepción, urgencia, estado y trazabilidad")
@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService servicio;
    private final AccesoService acceso;

    public OrdenController(OrdenService servicio, AccesoService acceso) {
        this.servicio = servicio;
        this.acceso = acceso;
    }

    @Operation(summary = "Crear la orden de reparación al recibir el vehículo")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdenReparacion crearOrden(HttpServletRequest req, @Valid @RequestBody Peticiones.NuevaOrden p) {
        Usuario u = SesionActual.usuario(req);
        acceso.validarRol(u, Rol.ADMINISTRADOR, Rol.RECEPCIONISTA);
        return servicio.crearOrden(u, p);
    }

    @Operation(summary = "Listar órdenes visibles para el usuario, filtrando por estado o urgencia; las más urgentes primero")
    @GetMapping
    public List<OrdenReparacion> listar(HttpServletRequest req, @RequestParam(required = false) String estado,
            @RequestParam(required = false) String urgencia) {
        return servicio.listar(SesionActual.usuario(req), estado, urgencia);
    }

    @Operation(summary = "Consultar una orden completa por id o número (OT-0001)")
    @GetMapping("/{id}")
    public OrdenReparacion consultar(HttpServletRequest req, @PathVariable String id) {
        Usuario u = SesionActual.usuario(req);
        OrdenReparacion o = servicio.buscarPorIdONumero(id);
        servicio.validarVisibilidad(u, o);
        return o;
    }

    @Operation(summary = "Consultar el estado actual y su historial de cambios")
    @GetMapping("/{id}/estado")
    public Respuestas.EstadoActual consultarEstado(HttpServletRequest req, @PathVariable String id) {
        return servicio.consultarEstado(SesionActual.usuario(req), id);
    }

    @Operation(summary = "Cambiar el estado: iniciar reparación, marcar listo para entrega o retroceder a diagnóstico")
    @PatchMapping("/{id}/estado")
    public OrdenReparacion cambiarEstado(HttpServletRequest req, @PathVariable String id,
            @Valid @RequestBody Peticiones.CambioEstado p) {
        return servicio.cambiarEstado(SesionActual.usuario(req), id, p);
    }

    @Operation(summary = "Clasificar o cambiar la urgencia (solo antes de aprobar la cotización)")
    @PatchMapping("/{id}/urgencia")
    public OrdenReparacion clasificarUrgencia(HttpServletRequest req, @PathVariable String id,
            @Valid @RequestBody Peticiones.CambioUrgencia p) {
        Usuario u = SesionActual.usuario(req);
        acceso.validarRol(u, Rol.ADMINISTRADOR, Rol.RECEPCIONISTA);
        return servicio.clasificarUrgencia(u, id, p);
    }
}
