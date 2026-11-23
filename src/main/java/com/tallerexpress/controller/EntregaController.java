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

@Tag(name = "Entrega")
@RestController
@RequestMapping("/api/ordenes/{ordenId}/entrega")
public class EntregaController {

    private final EntregaService servicio;
    private final AccesoService acceso;

    public EntregaController(EntregaService servicio, AccesoService acceso) {
        this.servicio = servicio;
        this.acceso = acceso;
    }

    @Operation(summary = "Registrar la entrega del vehículo y emitir el comprobante")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Entrega registrarEntrega(HttpServletRequest req, @PathVariable String ordenId) {
        Usuario u = SesionActual.usuario(req);
        acceso.validarRol(u, Rol.ADMINISTRADOR, Rol.RECEPCIONISTA);
        return servicio.registrarEntrega(u, ordenId);
    }

    @Operation(summary = "Consultar la entrega registrada")
    @GetMapping
    public Entrega consultarEntrega(HttpServletRequest req, @PathVariable String ordenId) {
        return servicio.consultarEntrega(SesionActual.usuario(req), ordenId);
    }

    @Operation(summary = "Obtener el comprobante de entrega")
    @GetMapping("/comprobante")
    public ComprobanteEntrega generarComprobante(HttpServletRequest req, @PathVariable String ordenId) {
        return servicio.generarComprobante(SesionActual.usuario(req), ordenId);
    }
}
