package com.tallerexpress.model;

public class LineaServicio {

    private String servicioId;
    private String nombre;
    private double tarifa;

    public LineaServicio() {
    }

    public LineaServicio(String servicioId, String nombre, double tarifa) {
        this.servicioId = servicioId;
        this.nombre = nombre;
        this.tarifa = tarifa;
    }

    public String getServicioId() {
        return servicioId;
    }

    public void setServicioId(String servicioId) {
        this.servicioId = servicioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getTarifa() {
        return tarifa;
    }

    public void setTarifa(double tarifa) {
        this.tarifa = tarifa;
    }

}
