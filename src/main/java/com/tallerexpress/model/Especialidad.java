package com.tallerexpress.model;

import java.util.List;

/** Especialidad de un mecánico. El catálogo es fijo (R8). */
public class Especialidad {

    public static final List<String> CATALOGO = List.of(
            "Mecánica general",
            "Electricidad automotriz",
            "Frenos y suspensión",
            "Transmisión",
            "Aire acondicionado",
            "Pintura y carrocería");

    /** Categorías de servicios: las 6 especialidades más mantenimiento preventivo (HU-21). */
    public static final List<String> CATEGORIAS_SERVICIO = List.of(
            "Mecánica general",
            "Electricidad automotriz",
            "Frenos y suspensión",
            "Transmisión",
            "Aire acondicionado",
            "Pintura y carrocería",
            "Mantenimiento preventivo");

    private String nombre;

    public Especialidad() {
    }

    public Especialidad(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** Devuelve el nombre canónico del catálogo (sin importar mayúsculas) o null si no existe. */
    public static String normalizar(String texto) {
        if (texto == null) {
            return null;
        }
        for (String e : CATALOGO) {
            if (e.equalsIgnoreCase(texto.trim())) {
                return e;
            }
        }
        return null;
    }

    public static String normalizarCategoria(String texto) {
        if (texto == null) {
            return null;
        }
        for (String c : CATEGORIAS_SERVICIO) {
            if (c.equalsIgnoreCase(texto.trim())) {
                return c;
            }
        }
        return null;
    }
}
