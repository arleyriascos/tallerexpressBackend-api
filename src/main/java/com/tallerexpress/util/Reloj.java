package com.tallerexpress.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

/** Fecha y hora del taller (Colombia), sin depender de la zona horaria del servidor. */
public final class Reloj {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private Reloj() {
    }

    public static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA);
    }
}
