package com.tallerexpress.util;

import java.util.List;

import com.tallerexpress.model.Repuesto;

/** Fórmula de cotización (RF-07): (mano de obra + repuestos) × factor de urgencia. */
public final class CalculadoraCotizacion {

    private CalculadoraCotizacion() {
    }

    public static double costoRepuestos(List<Repuesto> repuestos) {
        double suma = 0;
        if (repuestos != null) {
            for (Repuesto r : repuestos) {
                suma += r.calcularCosto();
            }
        }
        return suma;
    }

    /** Total en pesos enteros (no se definió otra regla de redondeo). */
    public static double calcularTotal(double manoDeObra, double repuestos, double factorUrgencia) {
        return Math.round((manoDeObra + repuestos) * factorUrgencia);
    }
}
