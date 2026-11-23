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
import com.tallerexpress.model.Cliente;
import com.tallerexpress.model.OrdenReparacion;
import com.tallerexpress.model.Rol;
import com.tallerexpress.model.Usuario;
import com.tallerexpress.model.Vehiculo;
import com.tallerexpress.security.SesionActual;
import com.tallerexpress.service.AccesoService;
import com.tallerexpress.service.ClienteVehiculoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Clientes y vehículos")
@RestController
@RequestMapping("/api")
public class ClienteVehiculoController {

    private final ClienteVehiculoService servicio;
    private final AccesoService acceso;

    public ClienteVehiculoController(ClienteVehiculoService servicio, AccesoService acceso) {
        this.servicio = servicio;
        this.acceso = acceso;
    }

    @Operation(summary = "Registrar un cliente")
    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente registrarCliente(HttpServletRequest req, @Valid @RequestBody Peticiones.NuevoCliente p) {
        acceso.validarRol(SesionActual.usuario(req), Rol.ADMINISTRADOR, Rol.RECEPCIONISTA);
        return servicio.registrarCliente(p);
    }

    @Operation(summary = "Listar clientes o buscar por documento")
    @GetMapping("/clientes")
    public List<Cliente> listarClientes(HttpServletRequest req, @RequestParam(required = false) String documento) {
        acceso.validarRol(SesionActual.usuario(req), Rol.ADMINISTRADOR, Rol.RECEPCIONISTA);
        return documento == null ? servicio.listarClientes() : List.of(servicio.buscarPorDocumento(documento));
    }

    @Operation(summary = "Consultar un cliente")
    @GetMapping("/clientes/{id}")
    public Cliente consultarCliente(HttpServletRequest req, @PathVariable String id) {
        Usuario u = SesionActual.usuario(req);
        acceso.validarRol(u, Rol.ADMINISTRADOR, Rol.RECEPCIONISTA, Rol.CLIENTE);
        if (u.getRol() == Rol.CLIENTE && !u.getId().equals(id)) {
            throw com.tallerexpress.exception.ApiException.prohibido("Solo puede consultar su propio perfil");
        }
        return servicio.buscarCliente(id);
    }

    @Operation(summary = "Registrar un vehículo (la placa es única)")
    @PostMapping("/vehiculos")
    @ResponseStatus(HttpStatus.CREATED)
    public Vehiculo registrarVehiculo(HttpServletRequest req, @Valid @RequestBody Peticiones.NuevoVehiculo p) {
        acceso.validarRol(SesionActual.usuario(req), Rol.ADMINISTRADOR, Rol.RECEPCIONISTA);
        return servicio.registrarVehiculo(p);
    }

    @Operation(summary = "Listar vehículos, opcionalmente de un cliente")
    @GetMapping("/vehiculos")
    public List<Vehiculo> listarVehiculos(HttpServletRequest req, @RequestParam(required = false) String clienteId) {
        Usuario u = SesionActual.usuario(req);
        acceso.validarRol(u, Rol.ADMINISTRADOR, Rol.RECEPCIONISTA, Rol.CLIENTE);
        return servicio.listarVehiculos(u.getRol() == Rol.CLIENTE ? u.getId() : clienteId);
    }

    @Operation(summary = "Consultar un vehículo por placa")
    @GetMapping("/vehiculos/{placa}")
    public Vehiculo consultarVehiculo(HttpServletRequest req, @PathVariable String placa) {
        Usuario u = SesionActual.usuario(req);
        acceso.validarRol(u, Rol.ADMINISTRADOR, Rol.RECEPCIONISTA, Rol.CLIENTE);
        Vehiculo v = servicio.buscarVehiculoPorPlaca(placa);
        if (u.getRol() == Rol.CLIENTE && !u.getId().equals(v.getClienteId())) {
            throw com.tallerexpress.exception.ApiException.prohibido("Este vehículo no pertenece a su cuenta");
        }
        return v;
    }

    @Operation(summary = "Historial de reparaciones de un vehículo")
    @GetMapping("/vehiculos/{placa}/historial")
    public List<OrdenReparacion> consultarHistorial(HttpServletRequest req, @PathVariable String placa) {
        Usuario u = SesionActual.usuario(req);
        acceso.validarRol(u, Rol.ADMINISTRADOR, Rol.RECEPCIONISTA, Rol.CLIENTE);
        Vehiculo v = servicio.buscarVehiculoPorPlaca(placa);
        if (u.getRol() == Rol.CLIENTE && !u.getId().equals(v.getClienteId())) {
            throw com.tallerexpress.exception.ApiException.prohibido("Este vehículo no pertenece a su cuenta");
        }
        return servicio.consultarHistorial(placa);
    }
}
