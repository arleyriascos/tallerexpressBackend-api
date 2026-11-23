package com.tallerexpress.service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tallerexpress.dto.Peticiones;
import com.tallerexpress.dto.Respuestas;
import com.tallerexpress.exception.ApiException;
import com.tallerexpress.model.Cliente;
import com.tallerexpress.model.Cotizacion;
import com.tallerexpress.model.EstadoCotizacion;
import com.tallerexpress.model.EstadoOrden;
import com.tallerexpress.model.NivelUrgencia;
import com.tallerexpress.model.OrdenReparacion;
import com.tallerexpress.model.Recepcionista;
import com.tallerexpress.model.Rol;
import com.tallerexpress.model.Usuario;
import com.tallerexpress.model.Vehiculo;
import com.tallerexpress.repository.MecanicoRepository;
import com.tallerexpress.repository.OrdenRepository;

/** Ciclo de vida de las órdenes de reparación (HU-04, HU-05, HU-14, HU-15, HU-16, HU-17). */
@Service
public class OrdenService {

    /** Estados que se pueden pedir directamente; el resto se alcanzan por su propio flujo. */
    private static final Set<EstadoOrden> DIRECTOS =
            EnumSet.of(EstadoOrden.EN_REPARACION, EstadoOrden.LISTO_PARA_ENTREGA, EstadoOrden.EN_DIAGNOSTICO);

    private final OrdenRepository ordenes;
    private final ClienteVehiculoService clienteVehiculo;
    private final NumeracionService numeracion;
    private final MecanicoRepository mecanicos;

    public OrdenService(OrdenRepository ordenes, ClienteVehiculoService clienteVehiculo,
            NumeracionService numeracion, MecanicoRepository mecanicos) {
        this.ordenes = ordenes;
        this.clienteVehiculo = clienteVehiculo;
        this.numeracion = numeracion;
        this.mecanicos = mecanicos;
    }

    public OrdenReparacion crearOrden(Usuario usuario, Peticiones.NuevaOrden p) {
        Vehiculo vehiculo = clienteVehiculo.buscarVehiculoPorPlaca(p.placa());
        Cliente cliente = clienteVehiculo.buscarCliente(vehiculo.getClienteId());
        NivelUrgencia urgencia = NivelUrgencia.desdeNombre(p.urgencia());
        String numero = numeracion.siguienteNumeroOrden();
        OrdenReparacion orden;
        if (usuario instanceof Recepcionista recepcionista) {
            orden = recepcionista.registrarRecepcion(vehiculo, numero, p.motivoIngreso(), p.sintomas(), urgencia);
        } else {
            orden = new OrdenReparacion(numero, vehiculo.getId(), cliente.getId(), p.motivoIngreso(), p.sintomas(),
                    urgencia, usuario.getId(), usuario.getNombreCompleto());
        }
        return ordenes.save(orden);
    }

    public OrdenReparacion buscar(String id) {
        return ordenes.findById(id).orElseThrow(() -> ApiException.noEncontrado("Orden no encontrada"));
    }

    /** Busca por id o por número (OT-0001). */
    public OrdenReparacion buscarPorIdONumero(String clave) {
        return ordenes.findById(clave).or(() -> ordenes.findByNumero(clave.toUpperCase()))
                .orElseThrow(() -> ApiException.noEncontrado("Orden no encontrada"));
    }

    /** Un cliente solo ve sus órdenes y un mecánico solo las que tiene asignadas. */
    public void validarVisibilidad(Usuario usuario, OrdenReparacion orden) {
        if (usuario.getRol() == Rol.CLIENTE && !usuario.getId().equals(orden.getClienteId())) {
            throw ApiException.prohibido("Esta orden no pertenece a su cuenta");
        }
        if (usuario.getRol() == Rol.MECANICO && !usuario.getId().equals(orden.getMecanicoId())) {
            throw ApiException.prohibido("Esta orden no está asignada a usted");
        }
    }

    /** El mecánico solo opera sobre su orden; administración puede intervenir siempre. */
    public void validarMecanicoResponsable(Usuario usuario, OrdenReparacion orden) {
        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            return;
        }
        if (usuario.getRol() != Rol.MECANICO || !usuario.getId().equals(orden.getMecanicoId())) {
            throw ApiException.prohibido("Solo el mecánico asignado a la orden puede realizar esta operación");
        }
    }

    /** Lista visible para el usuario, con filtros opcionales, ordenada por urgencia y luego por antigüedad. */
    public List<OrdenReparacion> listar(Usuario usuario, String estado, String urgencia) {
        List<OrdenReparacion> base;
        if (usuario.getRol() == Rol.CLIENTE) {
            base = ordenes.findByClienteId(usuario.getId());
        } else if (usuario.getRol() == Rol.MECANICO) {
            base = ordenes.findByMecanicoId(usuario.getId());
        } else if (estado != null && !estado.isBlank()) {
            base = ordenes.findByEstado(EstadoOrden.desdeTexto(estado));
        } else {
            base = ordenes.findAll();
        }
        List<OrdenReparacion> resultado = new ArrayList<>();
        EstadoOrden filtroEstado = (estado == null || estado.isBlank()) ? null : EstadoOrden.desdeTexto(estado);
        String filtroUrgencia = (urgencia == null || urgencia.isBlank())
                ? null : NivelUrgencia.desdeNombre(urgencia).getNombre();
        for (OrdenReparacion o : base) {
            if (filtroEstado != null && o.getEstado() != filtroEstado) {
                continue;
            }
            if (filtroUrgencia != null && !filtroUrgencia.equals(o.getUrgencia().getNombre())) {
                continue;
            }
            resultado.add(o);
        }
        resultado.sort((a, b) -> {
            int porUrgencia = Integer.compare(b.getUrgencia().prioridad(), a.getUrgencia().prioridad());
            return porUrgencia != 0 ? porUrgencia : a.getFechaRecepcion().compareTo(b.getFechaRecepcion());
        });
        return resultado;
    }

    public Respuestas.EstadoActual consultarEstado(Usuario usuario, String clave) {
        OrdenReparacion o = buscarPorIdONumero(clave);
        validarVisibilidad(usuario, o);
        Vehiculo v = clienteVehiculo.buscarVehiculoPorId(o.getVehiculoId());
        String mecanico = o.getMecanicoId() == null ? null
                : mecanicos.findById(o.getMecanicoId()).map(Usuario::getNombreCompleto).orElse(null);
        return new Respuestas.EstadoActual(o.getNumero(), o.getEstado().getNombre(), o.getUrgencia(),
                v.getPlaca(), mecanico, o.getFechaRecepcion(), o.getHistorial());
    }

    /**
     * Cambio de estado solicitado directamente: iniciar reparación, marcar listo
     * o retroceder a diagnóstico. Las demás transiciones pasan por asignación,
     * cotización o entrega.
     */
    public OrdenReparacion cambiarEstado(Usuario usuario, String id, Peticiones.CambioEstado p) {
        OrdenReparacion orden = buscar(id);
        EstadoOrden destino = EstadoOrden.desdeTexto(p.estado());
        if (!DIRECTOS.contains(destino)) {
            throw ApiException.conflicto("Ese estado se alcanza con su propia operación: asignar mecánico, "
                    + "generar/aprobar/rechazar cotización o registrar entrega");
        }
        if (usuario.getRol() == Rol.MECANICO || usuario.getRol() == Rol.ADMINISTRADOR) {
            validarMecanicoResponsable(usuario, orden);
        } else if (usuario.getRol() != Rol.RECEPCIONISTA) {
            throw ApiException.prohibido("Su rol no puede cambiar el estado de una orden");
        }
        if (destino == EstadoOrden.EN_DIAGNOSTICO && orden.getEstado() == EstadoOrden.COTIZADO) {
            orden.cotizacionVigente().ifPresent(Cotizacion::reemplazar);
        }
        orden.cambiarEstado(destino, usuario.getId(), usuario.getNombreCompleto(), p.motivo());
        return ordenes.save(orden);
    }

    /** Solo se puede cambiar antes de aprobar la cotización (HU-14). */
    public OrdenReparacion clasificarUrgencia(Usuario usuario, String id, Peticiones.CambioUrgencia p) {
        OrdenReparacion orden = buscar(id);
        NivelUrgencia nueva = NivelUrgencia.desdeNombre(p.urgencia());
        boolean cambia = !nueva.getNombre().equals(orden.getUrgencia().getNombre());
        if (cambia && (p.justificacion() == null || p.justificacion().isBlank())) {
            throw ApiException.solicitudInvalida("Debe justificar el cambio de urgencia");
        }
        orden.cambiarUrgencia(nueva, p.justificacion());
        // La cotización pendiente quedó calculada con el factor anterior: debe regenerarse.
        orden.cotizacionVigente().filter(c -> c.getEstado() == EstadoCotizacion.PENDIENTE)
                .ifPresent(Cotizacion::reemplazar);
        if (orden.getEstado() == EstadoOrden.COTIZADO && cambia) {
            orden.cambiarEstado(EstadoOrden.EN_DIAGNOSTICO, usuario.getId(), usuario.getNombreCompleto(),
                    "Cambio de urgencia: se debe generar una nueva cotización");
        }
        return ordenes.save(orden);
    }

    public OrdenReparacion guardar(OrdenReparacion orden) {
        return ordenes.save(orden);
    }
}
