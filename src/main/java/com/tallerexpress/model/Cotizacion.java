package com.tallerexpress.model;

import com.tallerexpress.exception.ApiException;
import com.tallerexpress.util.CalculadoraCotizacion;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cotizacion {

    private String id;
    private LocalDateTime fechaGeneracion;
    private EstadoCotizacion estado;
    private List<LineaServicio> servicios;
    private List<Repuesto> repuestos;
    private double manoDeObra;
    private double costoRepuestos;
    private double factorUrgencia;
    private double total;
    private LocalDateTime fechaDecision;

    public Cotizacion() {
    }

    public Cotizacion(List<LineaServicio> servicios, List<Repuesto> repuestos, double factorUrgencia, LocalDateTime fechaGeneracion) {
        this.id = UUID.randomUUID().toString();
        this.fechaGeneracion = fechaGeneracion;
        this.estado = EstadoCotizacion.PENDIENTE;
        this.servicios = new ArrayList<>(servicios);
        this.repuestos = new ArrayList<>(repuestos);
        this.factorUrgencia = factorUrgencia;
        calcularTotal();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public EstadoCotizacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoCotizacion estado) {
        this.estado = estado;
    }

    public List<LineaServicio> getServicios() {
        return servicios;
    }

    public void setServicios(List<LineaServicio> servicios) {
        this.servicios = servicios;
    }

    public List<Repuesto> getRepuestos() {
        return repuestos;
    }

    public void setRepuestos(List<Repuesto> repuestos) {
        this.repuestos = repuestos;
    }

    public double getManoDeObra() {
        return manoDeObra;
    }

    public void setManoDeObra(double manoDeObra) {
        this.manoDeObra = manoDeObra;
    }

    public double getCostoRepuestos() {
        return costoRepuestos;
    }

    public void setCostoRepuestos(double costoRepuestos) {
        this.costoRepuestos = costoRepuestos;
    }

    public double getFactorUrgencia() {
        return factorUrgencia;
    }

    public void setFactorUrgencia(double factorUrgencia) {
        this.factorUrgencia = factorUrgencia;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDateTime getFechaDecision() {
        return fechaDecision;
    }

    public void setFechaDecision(LocalDateTime fechaDecision) {
        this.fechaDecision = fechaDecision;
    }

    /** Mano de obra = suma de tarifas; total = (mano de obra + repuestos) × factor (HU-07). */
    public double calcularTotal() {
        double mano = 0;
        for (LineaServicio linea : servicios) {
            mano += linea.getTarifa();
        }
        this.manoDeObra = mano;
        this.costoRepuestos = CalculadoraCotizacion.costoRepuestos(repuestos);
        this.total = CalculadoraCotizacion.calcularTotal(manoDeObra, costoRepuestos, factorUrgencia);
        return total;
    }

    public boolean estaPendiente() {
        return estado == EstadoCotizacion.PENDIENTE;
    }

    public void aprobar(LocalDateTime cuando) {
        exigirPendiente();
        this.estado = EstadoCotizacion.APROBADA;
        this.fechaDecision = cuando;
    }

    public void rechazar(LocalDateTime cuando) {
        exigirPendiente();
        this.estado = EstadoCotizacion.RECHAZADA;
        this.fechaDecision = cuando;
    }

    /** Una cotización pendiente se reemplaza por otra nueva y se conserva en el historial. */
    public void reemplazar() {
        if (estaPendiente()) {
            this.estado = EstadoCotizacion.REEMPLAZADA;
        }
    }

    private void exigirPendiente() {
        if (!estaPendiente()) {
            throw ApiException.conflicto("La cotización no está pendiente de aprobación");
        }
    }

}
