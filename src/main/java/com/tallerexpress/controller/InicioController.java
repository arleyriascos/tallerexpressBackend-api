package com.tallerexpress.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

/** Ruta pública para comprobar que el servicio está vivo (Render la usa como health check). */
@Hidden
@RestController
public class InicioController {

    @GetMapping("/")
    public Map<String, String> inicio() {
        return Map.of("servicio", "TallerExpress API", "estado", "activo", "documentacion", "/swagger-ui.html");
    }
}
