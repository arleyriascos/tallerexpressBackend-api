package com.tallerexpress.service;

import org.springframework.stereotype.Service;

import com.tallerexpress.exception.ApiException;
import com.tallerexpress.model.Cliente;
import com.tallerexpress.model.ComprobanteEntrega;
import com.tallerexpress.model.Cotizacion;
import com.tallerexpress.model.Entrega;
import com.tallerexpress.model.EstadoCotizacion;
import com.tallerexpress.model.EstadoOrden;
import com.tallerexpress.model.OrdenReparacion;
import com.tallerexpress.model.Usuario;
import com.tallerexpress.model.Vehiculo;
import com.tallerexpress.repository.EntregaRepository;

/** Entrega del vehículo y comprobante (HU-18, HU-19, HU-20). */
@Service
public class EntregaService {

    private final OrdenService ordenService;
    private final ClienteVehiculoService clienteVehiculo;
    private final EntregaRepository entregas;

    public EntregaService(OrdenService ordenService, ClienteVehiculoService clienteVehiculo,
            EntregaRepository entregas) {
        this.ordenService = ordenService;
        this.clienteVehiculo = clienteVehiculo;
        this.entregas = entregas;
    }

    /** La orden pasa de Listo para entrega a Entregado y queda el comprobante. */
    public Entrega registrarEntrega(Usuario usuario, String ordenId) {
        OrdenReparacion orden = ordenService.buscar(ordenId);
        if (orden.getEstado() != EstadoOrden.LISTO_PARA_ENTREGA) {
            throw ApiException.conflicto("Solo se entrega una orden en estado Listo para entrega");
        }
        if (entregas.findByOrdenId(orden.getId()).isPresent()) {
            throw ApiException.conflicto("La orden ya tiene una entrega registrada");
        }
        Entrega entrega = new Entrega(orden.getId(), usuario.getId());
        emitirComprobante(entrega, orden);
        orden.cambiarEstado(EstadoOrden.ENTREGADO, usuario.getId(), usuario.getNombreCompleto(),
                "Vehículo entregado al cliente");
        ordenService.guardar(orden);
        return entregas.save(entrega);
    }

    /** Devuelve el comprobante de una orden ya entregada. */
    public ComprobanteEntrega generarComprobante(Usuario usuario, String ordenId) {
        OrdenReparacion orden = ordenService.buscar(ordenId);
        ordenService.validarVisibilidad(usuario, orden);
        Entrega e = entregas.findByOrdenId(orden.getId())
                .orElseThrow(() -> ApiException.noEncontrado("La orden aún no ha sido entregada"));
        return e.getComprobante();
    }

    public Entrega consultarEntrega(Usuario usuario, String ordenId) {
        OrdenReparacion orden = ordenService.buscar(ordenId);
        ordenService.validarVisibilidad(usuario, orden);
        return entregas.findByOrdenId(orden.getId())
                .orElseThrow(() -> ApiException.noEncontrado("La orden aún no ha sido entregada"));
    }

    private void emitirComprobante(Entrega entrega, OrdenReparacion orden) {
        Cotizacion aprobada = orden.getCotizaciones().stream()
                .filter(c -> c.getEstado() == EstadoCotizacion.APROBADA)
                .reduce((primera, ultima) -> ultima)
                .orElseThrow(() -> ApiException.conflicto("La orden no tiene cotización aprobada"));
        Cliente cliente = clienteVehiculo.buscarCliente(orden.getClienteId());
        Vehiculo vehiculo = clienteVehiculo.buscarVehiculoPorId(orden.getVehiculoId());
        String diagnostico = orden.getDiagnostico() == null ? null : orden.getDiagnostico().getDescripcion();
        entrega.generarComprobante(orden, cliente, vehiculo, aprobada, diagnostico);
    }
}
