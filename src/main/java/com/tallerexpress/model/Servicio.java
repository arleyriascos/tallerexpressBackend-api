package com.tallerexpress.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "servicios")
public class Servicio {

    @Id
    private String id;
    @Indexed(unique = true)
    private String codigo;
    private String nombre;
    private String descripcion;
    private String categoria;
    private double tarifaManoDeObra;
    private boolean activo;

    public Servicio() {
    }

    public Servicio(String codigo, String nombre, String descripcion, String categoria, double tarifaManoDeObra) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.tarifaManoDeObra = tarifaManoDeObra;
        this.activo = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getTarifaManoDeObra() {
        return tarifaManoDeObra;
    }

    public void setTarifaManoDeObra(double tarifaManoDeObra) {
        this.tarifaManoDeObra = tarifaManoDeObra;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void cambiarTarifa(double nuevaTarifa) {
        this.tarifaManoDeObra = nuevaTarifa;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }

}
