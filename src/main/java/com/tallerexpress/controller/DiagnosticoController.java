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

@Tag(name = "Diagnóstico y escalamiento")
@RestController
@RequestMapping("/api/ordenes/{ordenId}")
public class DiagnosticoController {

    private final DiagnosticoService servicio;

    public DiagnosticoController(DiagnosticoService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Registrar el diagnóstico (mecánico asignado)")
    @PostMapping("/diagnostico")
    @ResponseStatus(HttpStatus.CREATED)
    public OrdenReparacion registrarDiagnostico(HttpServletRequest req, @PathVariable String ordenId,
            @Valid @RequestBody Peticiones.NuevoDiagnostico p) {
        return servicio.registrarDiagnostico(SesionActual.usuario(req), ordenId, p);
    }

    @Operation(summary = "Consultar el diagnóstico")
    @GetMapping("/diagnostico")
    public Diagnostico consultarDiagnostico(HttpServletRequest req, @PathVariable String ordenId) {
        return servicio.consultarDiagnostico(SesionActual.usuario(req), ordenId);
    }

    @Operation(summary = "El mecánico solicita escalar la orden a otro especialista")
    @PostMapping("/escalamientos")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudEscalamiento escalarSolicitud(HttpServletRequest req, @PathVariable String ordenId,
            @Valid @RequestBody Peticiones.NuevoEscalamiento p) {
        return servicio.escalarSolicitud(SesionActual.usuario(req), ordenId, p);
    }

    @Operation(summary = "Consultar las solicitudes de escalamiento de la orden")
    @GetMapping("/escalamientos")
    public List<SolicitudEscalamiento> consultarSolicitudes(HttpServletRequest req, @PathVariable String ordenId) {
        return servicio.consultarSolicitudes(SesionActual.usuario(req), ordenId);
    }
}
