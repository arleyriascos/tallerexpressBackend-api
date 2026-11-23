package com.tallerexpress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.tallerexpress.exception.ApiException;
import com.tallerexpress.model.Cotizacion;
import com.tallerexpress.model.EstadoCotizacion;
import com.tallerexpress.model.EstadoOrden;
import com.tallerexpress.model.LineaServicio;
import com.tallerexpress.model.NivelUrgencia;
import com.tallerexpress.model.OrdenReparacion;
import com.tallerexpress.model.Repuesto;
import com.tallerexpress.util.Reloj;

/** Pruebas unitarias de las reglas del dominio (no necesitan base de datos). */
class ReglasDeNegocioTest {

    private OrdenReparacion nuevaOrden() {
        return new OrdenReparacion("OT-0001", "v1", "c1", "Ruido", "chirrido",
                NivelUrgencia.desdeNombre("Alta"), "u1", "Rosa");
    }

    @Test
    void cotizacionAplicaFactorDeUrgencia() {
        Cotizacion c = new Cotizacion(
                List.of(new LineaServicio("s1", "Frenos", 100000), new LineaServicio("s2", "Aceite", 50000)),
                List.of(new Repuesto("Pastillas", 2, 40000)), 1.30, Reloj.ahora());
        assertEquals(299000.0, c.getTotal());
        assertEquals(EstadoCotizacion.PENDIENTE, c.getEstado());
    }

    @Test
    void factoresDeUrgencia() {
        assertEquals(1.0, NivelUrgencia.desdeNombre("baja").getFactor());
        assertEquals(1.15, NivelUrgencia.desdeNombre("MEDIA").getFactor());
        assertEquals(1.30, NivelUrgencia.desdeNombre("Alta").getFactor());
        assertThrows(IllegalArgumentException.class, () -> NivelUrgencia.desdeNombre("Urgentísima"));
    }

    @Test
    void transicionesPermitidas() {
        assertTrue(EstadoOrden.RECIBIDO.puedePasarA(EstadoOrden.EN_DIAGNOSTICO));
        assertFalse(EstadoOrden.RECIBIDO.puedePasarA(EstadoOrden.ENTREGADO));
        assertTrue(EstadoOrden.EN_REPARACION.puedePasarA(EstadoOrden.EN_DIAGNOSTICO));
        assertTrue(EstadoOrden.ENTREGADO.esFinal());
        assertTrue(EstadoOrden.RECHAZADO.siguientesPermitidos().isEmpty());
    }

    @Test
    void estadosAceptanTildesYGuiones() {
        assertEquals(EstadoOrden.EN_REPARACION, EstadoOrden.desdeTexto("En reparación"));
        assertEquals(EstadoOrden.LISTO_PARA_ENTREGA, EstadoOrden.desdeTexto("LISTO_PARA_ENTREGA"));
    }

    @Test
    void ordenNoSaltaEstadosYRegistraHistorial() {
        OrdenReparacion o = nuevaOrden();
        ApiException e = assertThrows(ApiException.class,
                () -> o.cambiarEstado(EstadoOrden.ENTREGADO, "u1", "Rosa", null));
        assertEquals(409, e.getEstado());
        o.cambiarEstado(EstadoOrden.EN_DIAGNOSTICO, "u1", "Rosa", "Asignada");
        assertEquals(2, o.getHistorial().size());
    }

    @Test
    void retrocesoExigeMotivo() {
        OrdenReparacion o = nuevaOrden();
        o.cambiarEstado(EstadoOrden.EN_DIAGNOSTICO, "u", "n", null);
        o.cambiarEstado(EstadoOrden.COTIZADO, "u", "n", null);
        assertThrows(ApiException.class, () -> o.cambiarEstado(EstadoOrden.EN_DIAGNOSTICO, "u", "n", " "));
        o.cambiarEstado(EstadoOrden.EN_DIAGNOSTICO, "u", "n", "Cliente pide revisar");
        assertEquals(EstadoOrden.EN_DIAGNOSTICO, o.getEstado());
    }

    @Test
    void urgenciaNoCambiaTrasAprobar() {
        OrdenReparacion o = nuevaOrden();
        o.cambiarEstado(EstadoOrden.EN_DIAGNOSTICO, "u", "n", null);
        o.cambiarEstado(EstadoOrden.COTIZADO, "u", "n", null);
        o.cambiarEstado(EstadoOrden.APROBADO, "u", "n", null);
        assertThrows(ApiException.class, () -> o.cambiarUrgencia(NivelUrgencia.desdeNombre("Baja"), "x"));
    }
}
