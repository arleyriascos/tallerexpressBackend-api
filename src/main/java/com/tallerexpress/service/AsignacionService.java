package com.tallerexpress.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tallerexpress.dto.Peticiones;
import com.tallerexpress.exception.ApiException;
import com.tallerexpress.model.Asignacion;
import com.tallerexpress.model.Especialidad;
import com.tallerexpress.model.EstadoOrden;
import com.tallerexpress.model.Mecanico;
import com.tallerexpress.model.OrdenReparacion;
import com.tallerexpress.model.SolicitudEscalamiento;
import com.tallerexpress.model.Usuario;
import com.tallerexpress.repository.AsignacionRepository;

/** Asignación y traspaso de órdenes entre mecánicos (HU-11, HU-12, HU-13). */
@Service
public class AsignacionService {

    private final OrdenService ordenService;
    private final MecanicoService mecanicoService;
    private final AsignacionRepository asignaciones;

    public AsignacionService(OrdenService ordenService, MecanicoService mecanicoService,
            AsignacionRepository asignaciones) {
        this.ordenService = ordenService;
        this.mecanicoService = mecanicoService;
        this.asignaciones = asignaciones;
    }

    /** El mecánico debe tener la especialidad requerida (R8). Devuelve el mecánico validado. */
    public Mecanico validarEspecialidad(String mecanicoId, String especialidad) {
        String normal = Especialidad.normalizar(especialidad);
        if (normal == null) {
            throw ApiException.solicitudInvalida("Especialidad inválida. Use una de: " + Especialidad.CATALOGO);
        }
        Mecanico m = mecanicoService.buscar(mecanicoId);
        if (!m.tieneEspecialidad(normal)) {
            throw ApiException.conflicto(m.getNombreCompleto() + " no tiene la especialidad requerida: " + normal);
        }
        return m;
    }

    /** Primera asignación: la orden pasa de Recibido a En diagnóstico. */
    public OrdenReparacion asignarMecanico(Usuario usuario, String ordenId, Peticiones.Asignar p) {
        OrdenReparacion orden = ordenService.buscar(ordenId);
        if (orden.getMecanicoId() != null) {
            throw ApiException.conflicto("La orden ya tiene mecánico; use el traspaso");
        }
        if (orden.getEstado() != EstadoOrden.RECIBIDO) {
            throw ApiException.conflicto("Solo se asigna mecánico a una orden en estado Recibido");
        }
        Mecanico m = validarEspecialidad(p.mecanicoId(), p.especialidad());
        String especialidad = Especialidad.normalizar(p.especialidad());
        orden.asignarMecanico(m.getId(), especialidad);
        orden.cambiarEstado(EstadoOrden.EN_DIAGNOSTICO, usuario.getId(), usuario.getNombreCompleto(),
                "Asignada a " + m.getNombreCompleto());
        asignaciones.save(new Asignacion(orden.getId(), m.getId(), especialidad, "Asignación inicial", usuario.getId()));
        return ordenService.guardar(orden);
    }

    /** Cambia el mecánico responsable; exige motivo y deja registro (HU-13). */
    public OrdenReparacion traspasarOrden(Usuario usuario, String ordenId, Peticiones.Traspaso p) {
        OrdenReparacion orden = ordenService.buscar(ordenId);
        if (orden.getEstado().esFinal()) {
            throw ApiException.conflicto("No se puede traspasar una orden " + orden.getEstado().getNombre());
        }
        if (orden.getMecanicoId() == null) {
            throw ApiException.conflicto("La orden aún no tiene mecánico; use la asignación");
        }
        if (orden.getMecanicoId().equals(p.mecanicoId())) {
            throw ApiException.conflicto("La orden ya está asignada a ese mecánico");
        }
        Mecanico nuevo = validarEspecialidad(p.mecanicoId(), p.especialidad());
        String especialidad = Especialidad.normalizar(p.especialidad());
        if (p.solicitudId() != null) {
            SolicitudEscalamiento s = orden.buscarSolicitud(p.solicitudId())
                    .orElseThrow(() -> ApiException.noEncontrado("Solicitud de escalamiento no encontrada"));
            s.asignarEspecialista(nuevo.getId());
        }
        orden.asignarMecanico(nuevo.getId(), especialidad);
        asignaciones.save(new Asignacion(orden.getId(), nuevo.getId(), especialidad, p.motivo(), usuario.getId()));
        return ordenService.guardar(orden);
    }

    public List<Asignacion> consultarPorMecanico(String mecanicoId) {
        return asignaciones.findByMecanicoId(mecanicoId);
    }

    public List<Asignacion> consultarPorOrden(String ordenId) {
        return asignaciones.findByOrdenId(ordenId);
    }
}
