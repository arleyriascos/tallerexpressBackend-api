package com.tallerexpress.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Diagnostico {

    private String descripcion;
    private List<String> servicioIds;
    private List<Repuesto> repuestos;
    private LocalDateTime fecha;
    private String mecanicoId;

    public Diagnostico() {
    }

    public Diagnostico(String descripcion, String mecanicoId, LocalDateTime fecha) {
        this.descripcion = descripcion;
        this.mecanicoId = mecanicoId;
        this.fecha = fecha;
        this.servicioIds = new ArrayList<>();
        this.repuestos = new ArrayList<>();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getServicioIds() {
        return servicioIds;
    }

    public void setServicioIds(List<String> servicioIds) {
        this.servicioIds = servicioIds;
    }

    public List<Repuesto> getRepuestos() {
        return repuestos;
    }

    public void setRepuestos(List<Repuesto> repuestos) {
        this.repuestos = repuestos;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMecanicoId() {
        return mecanicoId;
    }

    public void setMecanicoId(String mecanicoId) {
        this.mecanicoId = mecanicoId;
    }

    public void agregarServicio(String servicioId) {
        servicioIds.add(servicioId);
    }

    public void agregarRepuesto(Repuesto repuesto) {
        repuestos.add(repuesto);
    }

}
