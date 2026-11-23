package com.tallerexpress.model;

/**
 * Nivel de urgencia de una orden con su factor de cotización (HU-14).
 * Valores fijos: Baja ×1.0, Media ×1.15, Alta ×1.30.
 */
public class NivelUrgencia {

    public static final NivelUrgencia BAJA = new NivelUrgencia("BAJA", 1.0);
    public static final NivelUrgencia MEDIA = new NivelUrgencia("MEDIA", 1.15);
    public static final NivelUrgencia ALTA = new NivelUrgencia("ALTA", 1.30);

    private String nombre;
    private double factor;

    public NivelUrgencia() {
    }

    public NivelUrgencia(String nombre, double factor) {
        this.nombre = nombre;
        this.factor = factor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    /** Orden de prioridad: 3 = Alta, 2 = Media, 1 = Baja. */
    public int prioridad() {
        if (ALTA.nombre.equals(nombre)) {
            return 3;
        }
        if (MEDIA.nombre.equals(nombre)) {
            return 2;
        }
        return 1;
    }

    public static NivelUrgencia desdeNombre(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("La urgencia es obligatoria (Baja, Media o Alta)");
        }
        String normal = texto.trim().toUpperCase();
        if (BAJA.nombre.equals(normal)) {
            return new NivelUrgencia(BAJA.nombre, BAJA.factor);
        }
        if (MEDIA.nombre.equals(normal)) {
            return new NivelUrgencia(MEDIA.nombre, MEDIA.factor);
        }
        if (ALTA.nombre.equals(normal)) {
            return new NivelUrgencia(ALTA.nombre, ALTA.factor);
        }
        throw new IllegalArgumentException("Urgencia inválida: " + texto + " (use Baja, Media o Alta)");
    }
}
