package com.tallerexpress.model;

import com.tallerexpress.util.Reloj;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "entregas")
public class Entrega {

    @Id
    private String id;
    @Indexed(unique = true)
    private String ordenId;
    private LocalDateTime fechaHora;
    private String entregadoPorId;
    private ComprobanteEntrega comprobante;

    public Entrega() {
    }

    public Entrega(String ordenId, String entregadoPorId) {
        this.ordenId = ordenId;
        this.entregadoPorId = entregadoPorId;
        this.fechaHora = Reloj.ahora();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrdenId() {
        return ordenId;
    }

    public void setOrdenId(String ordenId) {
        this.ordenId = ordenId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEntregadoPorId() {
        return entregadoPorId;
    }

    public void setEntregadoPorId(String entregadoPorId) {
        this.entregadoPorId = entregadoPorId;
    }

    public ComprobanteEntrega getComprobante() {
        return comprobante;
    }

    public void setComprobante(ComprobanteEntrega comprobante) {
        this.comprobante = comprobante;
    }

    /** Arma el comprobante con la información de la orden y su cotización aprobada (HU-20). */
    public ComprobanteEntrega generarComprobante(OrdenReparacion orden, Cliente cliente, Vehiculo vehiculo,
            Cotizacion cotizacion, String diagnostico) {
        ComprobanteEntrega c = new ComprobanteEntrega();
        c.setFechaEmision(fechaHora);
        c.setTaller("TallerExpress");
        c.setNumeroOrden(orden.getNumero());
        c.setNombreCliente(cliente.getNombreCompleto());
        c.setTelefonoCliente(cliente.getTelefono());
        c.setEmailCliente(cliente.getEmail());
        c.setPlaca(vehiculo.getPlaca());
        c.setMarca(vehiculo.getMarca());
        c.setModelo(vehiculo.getModelo());
        c.setAnio(vehiculo.getAnio());
        c.setFechaRecepcion(orden.getFechaRecepcion());
        c.setMotivoIngreso(orden.getMotivoIngreso());
        c.setDiagnostico(diagnostico);
        c.setServicios(cotizacion.getServicios());
        c.setRepuestos(cotizacion.getRepuestos());
        c.setManoDeObra(cotizacion.getManoDeObra());
        c.setCostoRepuestos(cotizacion.getCostoRepuestos());
        c.setFactorUrgencia(cotizacion.getFactorUrgencia());
        c.setTotal(cotizacion.getTotal());
        this.comprobante = c;
        return c;
    }

}
