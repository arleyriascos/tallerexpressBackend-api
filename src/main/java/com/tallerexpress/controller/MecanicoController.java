package com.tallerexpress.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tallerexpress.dto.Peticiones;
import com.tallerexpress.model.Asignacion;
import com.tallerexpress.model.Mecanico;
import com.tallerexpress.model.Rol;
import com.tallerexpress.security.SesionActual;
import com.tallerexpress.service.AccesoService;
import com.tallerexpress.service.AsignacionService;
import com.tallerexpress.service.MecanicoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Mecánicos")
@RestController
@RequestMapping("/api/mecanicos")
public class MecanicoController {

    private final MecanicoService servicio;
    private final AsignacionService asignaciones;
    private final AccesoService acceso;

    public MecanicoController(MecanicoService servicio, AsignacionService asignaciones, AccesoService acceso) {
        this.servicio = servicio;
        this.asignaciones = asignaciones;
        this.acceso = acceso;
    }

    @Operation(summary = "Registrar un mecánico con sus especialidades (solo administrador)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mecanico registrar(HttpServletRequest req, @Valid @RequestBody Peticiones.NuevoMecanico p) {
        acceso.validarRol(SesionActual.usuario(req), Rol.ADMINISTRADOR);
        return servicio.registrar(p);
    }

    @Operation(summary = "Listar mecánicos, opcionalmente por especialidad")
    @GetMapping
    public List<Mecanico> listar(HttpServletRequest req, @RequestParam(required = false) String especialidad) {
        acceso.validarRol(SesionActual.usuario(req), Rol.ADMINISTRADOR, Rol.RECEPCIONISTA);
        return servicio.listar(especialidad);
    }

    @Operation(summary = "Asignaciones históricas de un mecánico")
    @GetMapping("/{id}/asignaciones")
    public List<Asignacion> asignaciones(HttpServletRequest req, @PathVariable String id) {
        acceso.validarRol(SesionActual.usuario(req), Rol.ADMINISTRADOR, Rol.RECEPCIONISTA);
        return asignaciones.consultarPorMecanico(id);
    }
}
