package com.tallerexpress.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tallerexpress.dto.Peticiones;
import com.tallerexpress.exception.ApiException;
import com.tallerexpress.model.Diagnostico;
import com.tallerexpress.model.Especialidad;
import com.tallerexpress.model.EstadoOrden;
import com.tallerexpress.model.Mecanico;
import com.tallerexpress.model.OrdenReparacion;
import com.tallerexpress.model.Repuesto;
import com.tallerexpress.model.Servicio;
import com.tallerexpress.model.SolicitudEscalamiento;
import com.tallerexpress.model.Usuario;
import com.tallerexpress.repository.ServicioRepository;
import com.tallerexpress.util.Reloj;

/** Diagnóstico técnico y escalamiento a especialistas (HU-07, HU-08, HU-12). */
@Service
public class DiagnosticoService {

    private final OrdenService ordenService;
    private final ServicioRepository servicios;
    private final MecanicoService mecanicoService;

    public DiagnosticoService(OrdenService ordenService, ServicioRepository servicios,
            MecanicoService mecanicoService) {
        this.ordenService = ordenService;
        this.servicios = servicios;
        this.mecanicoService = mecanicoService;
    }

    public OrdenReparacion registrarDiagnostico(Usuario usuario, String ordenId, Peticiones.NuevoDiagnostico p) {
        OrdenReparacion orden = ordenService.buscar(ordenId);
        ordenService.validarMecanicoResponsable(usuario, orden);
        if (orden.getEstado() != EstadoOrden.EN_DIAGNOSTICO) {
            throw ApiException.conflicto("Solo se registra diagnóstico en una orden En diagnóstico");
        }
        Diagnostico d = new Diagnostico(p.descripcion(), usuario.getId(), Reloj.ahora());
        for (String servicioId : p.servicioIds()) {
            Servicio s = servicios.findById(servicioId)
                    .orElseThrow(() -> ApiException.solicitudInvalida("Servicio inexistente: " + servicioId));
            if (!s.isActivo()) {
                throw ApiException.conflicto("El servicio " + s.getNombre() + " está inactivo");
            }
            if (!d.getServicioIds().contains(servicioId)) {
                d.agregarServicio(servicioId);
            }
        }
        if (p.repuestos() != null) {
            for (Peticiones.RepuestoDto r : p.repuestos()) {
                d.agregarRepuesto(new Repuesto(r.descripcion(), r.cantidad(), r.costoUnitario()));
            }
        }
        orden.setDiagnostico(d);
        return ordenService.guardar(orden);
    }

    public Diagnostico consultarDiagnostico(Usuario usuario, String ordenId) {
        OrdenReparacion orden = ordenService.buscar(ordenId);
        ordenService.validarVisibilidad(usuario, orden);
        if (orden.getDiagnostico() == null) {
            throw ApiException.noEncontrado("La orden aún no tiene diagnóstico");
        }
        return orden.getDiagnostico();
    }

    /** El mecánico pide que la orden pase a otro especialista; luego se atiende con el traspaso. */
    public SolicitudEscalamiento escalarSolicitud(Usuario usuario, String ordenId, Peticiones.NuevoEscalamiento p) {
        OrdenReparacion orden = ordenService.buscar(ordenId);
        ordenService.validarMecanicoResponsable(usuario, orden);
        if (orden.getEstado().esFinal()) {
            throw ApiException.conflicto("La orden ya está cerrada");
        }
        String especialidad = Especialidad.normalizar(p.especialidad());
        if (especialidad == null) {
            throw ApiException.solicitudInvalida("Especialidad inválida. Use una de: " + Especialidad.CATALOGO);
        }
        SolicitudEscalamiento solicitud;
        if (usuario instanceof Mecanico mecanico) {
            solicitud = mecanico.solicitarEscalamiento(orden, especialidad, p.motivo());
        } else {
            solicitud = new SolicitudEscalamiento(especialidad, p.motivo(), usuario.getId());
            orden.agregarSolicitudEscalamiento(solicitud);
        }
        ordenService.guardar(orden);
        return solicitud;
    }

    public List<SolicitudEscalamiento> consultarSolicitudes(Usuario usuario, String ordenId) {
        OrdenReparacion orden = ordenService.buscar(ordenId);
        ordenService.validarVisibilidad(usuario, orden);
        return new ArrayList<>(orden.getSolicitudesEscalamiento());
    }
}
