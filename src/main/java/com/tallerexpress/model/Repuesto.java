package com.tallerexpress.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Repuesto {

    private String descripcion;
    private int cantidad;
    private double costoUnitario;

    public Repuesto() {
    }

    public Repuesto(String descripcion, int cantidad, double costoUnitario) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    /** Costo de la línea: cantidad × costo unitario (HU-08). */
    @JsonProperty("costoLinea")
    public double calcularCosto() {
        return cantidad * costoUnitario;
    }

}
