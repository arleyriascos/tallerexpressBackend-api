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

@Tag(name = "Asignación de mecánicos")
@RestController
@RequestMapping("/api/ordenes/{ordenId}")
public class AsignacionController {

    private final AsignacionService servicio;
    private final AccesoService acceso;

    public AsignacionController(AsignacionService servicio, AccesoService acceso) {
        this.servicio = servicio;
        this.acceso = acceso;
    }

    @Operation(summary = "Asignar mecánico a una orden recibida; valida la especialidad")
    @PostMapping("/asignacion")
    public OrdenReparacion asignarMecanico(HttpServletRequest req, @PathVariable String ordenId,
            @Valid @RequestBody Peticiones.Asignar p) {
        Usuario u = SesionActual.usuario(req);
        acceso.validarRol(u, Rol.ADMINISTRADOR, Rol.RECEPCIONISTA);
        return servicio.asignarMecanico(u, ordenId, p);
    }

    @Operation(summary = "Traspasar la orden a otro mecánico con motivo")
    @PostMapping("/traspaso")
    public OrdenReparacion traspasarOrden(HttpServletRequest req, @PathVariable String ordenId,
            @Valid @RequestBody Peticiones.Traspaso p) {
        Usuario u = SesionActual.usuario(req);
        acceso.validarRol(u, Rol.ADMINISTRADOR, Rol.RECEPCIONISTA);
        return servicio.traspasarOrden(u, ordenId, p);
    }

    @Operation(summary = "Historial de asignaciones de la orden")
    @GetMapping("/asignaciones")
    public List<Asignacion> asignaciones(HttpServletRequest req, @PathVariable String ordenId) {
        acceso.validarRol(SesionActual.usuario(req), Rol.ADMINISTRADOR, Rol.RECEPCIONISTA);
        return servicio.consultarPorOrden(ordenId);
    }
}
