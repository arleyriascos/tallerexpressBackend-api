package com.tallerexpress.model;

import java.time.LocalDateTime;
import java.util.List;

public class ComprobanteEntrega {

    private LocalDateTime fechaEmision;
    private String taller;
    private String numeroOrden;
    private String nombreCliente;
    private String telefonoCliente;
    private String emailCliente;
    private String placa;
    private String marca;
    private String modelo;
    private int anio;
    private LocalDateTime fechaRecepcion;
    private String motivoIngreso;
    private String diagnostico;
    private List<LineaServicio> servicios;
    private List<Repuesto> repuestos;
    private double manoDeObra;
    private double costoRepuestos;
    private double factorUrgencia;
    private double total;

    public ComprobanteEntrega() {
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getTaller() {
        return taller;
    }

    public void setTaller(String taller) {
        this.taller = taller;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(LocalDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public String getMotivoIngreso() {
        return motivoIngreso;
    }

    public void setMotivoIngreso(String motivoIngreso) {
        this.motivoIngreso = motivoIngreso;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
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

}
