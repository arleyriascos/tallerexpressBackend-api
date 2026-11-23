package com.tallerexpress.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tallerexpress.dto.Peticiones;
import com.tallerexpress.model.Rol;
import com.tallerexpress.model.Servicio;
import com.tallerexpress.security.SesionActual;
import com.tallerexpress.service.AccesoService;
import com.tallerexpress.service.ServicioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Catálogo de servicios")
@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ServicioService servicio;
    private final AccesoService acceso;

    public ServicioController(ServicioService servicio, AccesoService acceso) {
        this.servicio = servicio;
        this.acceso = acceso;
    }

    @Operation(summary = "Crear un servicio del catálogo (solo administrador)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Servicio crear(HttpServletRequest req, @Valid @RequestBody Peticiones.NuevoServicio p) {
        acceso.validarRol(SesionActual.usuario(req), Rol.ADMINISTRADOR);
        return servicio.crear(p);
    }

    @Operation(summary = "Listar servicios")
    @GetMapping
    public List<Servicio> listar(@RequestParam(defaultValue = "true") boolean soloActivos) {
        return servicio.listar(soloActivos);
    }

    @Operation(summary = "Cambiar la tarifa de mano de obra (solo administrador)")
    @PutMapping("/{id}/tarifa")
    public Servicio cambiarTarifa(HttpServletRequest req, @PathVariable String id,
            @Valid @RequestBody Peticiones.CambioTarifa p) {
        acceso.validarRol(SesionActual.usuario(req), Rol.ADMINISTRADOR);
        return servicio.cambiarTarifa(id, p.tarifaManoDeObra());
    }

    @Operation(summary = "Activar o desactivar un servicio (solo administrador)")
    @PatchMapping("/{id}/activo")
    public Servicio cambiarActivo(HttpServletRequest req, @PathVariable String id, @RequestParam boolean valor) {
        acceso.validarRol(SesionActual.usuario(req), Rol.ADMINISTRADOR);
        return servicio.cambiarActivo(id, valor);
    }
}
