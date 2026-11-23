package com.tallerexpress.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tallerexpress.exception.ApiException;
import com.tallerexpress.model.Cotizacion;
import com.tallerexpress.model.Diagnostico;
import com.tallerexpress.model.EstadoCotizacion;
import com.tallerexpress.model.EstadoOrden;
import com.tallerexpress.model.LineaServicio;
import com.tallerexpress.model.OrdenReparacion;
import com.tallerexpress.model.Rol;
import com.tallerexpress.model.Servicio;
import com.tallerexpress.model.Usuario;
import com.tallerexpress.repository.ServicioRepository;
import com.tallerexpress.util.Reloj;

/** Generación, aprobación y rechazo de cotizaciones (HU-06, HU-09). */
@Service
public class CotizacionService {

    private final OrdenService ordenService;
    private final ServicioRepository servicios;

    public CotizacionService(OrdenService ordenService, ServicioRepository servicios) {
        this.ordenService = ordenService;
        this.servicios = servicios;
    }

    /**
     * Cotiza el diagnóstico: (mano de obra + repuestos) × factor de urgencia.
     * La orden pasa de En diagnóstico a Cotizado.
     */
    public OrdenReparacion generarCotizacion(Usuario usuario, String ordenId) {
        OrdenReparacion orden = ordenService.buscar(ordenId);
        if (orden.getEstado() != EstadoOrden.EN_DIAGNOSTICO) {
            throw ApiException.conflicto("Solo se cotiza una orden En diagnóstico");
        }
        Diagnostico d = orden.getDiagnostico();
        if (d == null || d.getServicioIds().isEmpty()) {
            throw ApiException.conflicto("La orden necesita un diagnóstico con al menos un servicio");
        }
        List<LineaServicio> lineas = new ArrayList<>();
        for (String servicioId : d.getServicioIds()) {
            Servicio s = servicios.findById(servicioId)
                    .orElseThrow(() -> ApiException.conflicto("Un servicio del diagnóstico ya no existe"));
            lineas.add(new LineaServicio(s.getId(), s.getNombre(), s.getTarifaManoDeObra()));
        }
        Cotizacion c = new Cotizacion(lineas, d.getRepuestos(), orden.getUrgencia().getFactor(), Reloj.ahora());
        orden.agregarCotizacion(c);
        orden.cambiarEstado(EstadoOrden.COTIZADO, usuario.getId(), usuario.getNombreCompleto(),
                "Cotización generada por $" + Math.round(c.getTotal()));
        return ordenService.guardar(orden);
    }

    public OrdenReparacion aprobarCotizacion(Usuario usuario, String ordenId) {
        OrdenReparacion orden = cargarCotizada(usuario, ordenId);
        orden.cotizacionVigente().orElseThrow().aprobar(Reloj.ahora());
        orden.cambiarEstado(EstadoOrden.APROBADO, usuario.getId(), usuario.getNombreCompleto(),
                "Cotización aprobada");
        return ordenService.guardar(orden);
    }

    /** El rechazo cierra la orden en estado Rechazado y exige motivo. */
    public OrdenReparacion rechazarCotizacion(Usuario usuario, String ordenId, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw ApiException.solicitudInvalida("El motivo del rechazo es obligatorio");
        }
        OrdenReparacion orden = cargarCotizada(usuario, ordenId);
        orden.cotizacionVigente().orElseThrow().rechazar(Reloj.ahora());
        orden.cambiarEstado(EstadoOrden.RECHAZADO, usuario.getId(), usuario.getNombreCompleto(), motivo);
        return ordenService.guardar(orden);
    }

    public List<Cotizacion> consultarCotizaciones(Usuario usuario, String ordenId) {
        OrdenReparacion orden = ordenService.buscar(ordenId);
        ordenService.validarVisibilidad(usuario, orden);
        return orden.getCotizaciones();
    }

    private OrdenReparacion cargarCotizada(Usuario usuario, String ordenId) {
        OrdenReparacion orden = ordenService.buscar(ordenId);
        if (usuario.getRol() == Rol.CLIENTE && !usuario.getId().equals(orden.getClienteId())) {
            throw ApiException.prohibido("Esta orden no pertenece a su cuenta");
        }
        if (orden.getEstado() != EstadoOrden.COTIZADO) {
            throw ApiException.conflicto("La orden no está en estado Cotizado");
        }
        Cotizacion vigente = orden.cotizacionVigente()
                .orElseThrow(() -> ApiException.conflicto("La orden no tiene cotización"));
        if (vigente.getEstado() != EstadoCotizacion.PENDIENTE) {
            throw ApiException.conflicto("La cotización vigente no está pendiente");
        }
        return orden;
    }
}
