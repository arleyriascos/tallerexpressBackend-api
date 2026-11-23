package com.tallerexpress.model;

import com.tallerexpress.util.Reloj;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "asignaciones")
public class Asignacion {

    @Id
    private String id;
    @Indexed
    private String ordenId;
    private String mecanicoId;
    private LocalDateTime fecha;
    private String especialidad;
    private String motivo;
    private String asignadoPorId;

    public Asignacion() {
    }

    public Asignacion(String ordenId, String mecanicoId, String especialidad, String motivo, String asignadoPorId) {
        this.ordenId = ordenId;
        this.mecanicoId = mecanicoId;
        this.especialidad = especialidad;
        this.motivo = motivo;
        this.asignadoPorId = asignadoPorId;
        this.fecha = Reloj.ahora();
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

    public String getMecanicoId() {
        return mecanicoId;
    }

    public void setMecanicoId(String mecanicoId) {
        this.mecanicoId = mecanicoId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getAsignadoPorId() {
        return asignadoPorId;
    }

    public void setAsignadoPorId(String asignadoPorId) {
        this.asignadoPorId = asignadoPorId;
    }

}
