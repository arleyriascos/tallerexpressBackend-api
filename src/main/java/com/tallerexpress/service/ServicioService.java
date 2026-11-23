package com.tallerexpress.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tallerexpress.dto.Peticiones;
import com.tallerexpress.exception.ApiException;
import com.tallerexpress.model.Especialidad;
import com.tallerexpress.model.Servicio;
import com.tallerexpress.repository.ServicioRepository;

/** Catálogo de servicios y tarifas de mano de obra (HU-21). */
@Service
public class ServicioService {

    private final ServicioRepository servicios;

    public ServicioService(ServicioRepository servicios) {
        this.servicios = servicios;
    }

    public Servicio crear(Peticiones.NuevoServicio p) {
        String categoria = Especialidad.normalizarCategoria(p.categoria());
        if (categoria == null) {
            throw ApiException.solicitudInvalida("Categoría inválida. Use una de: " + Especialidad.CATEGORIAS_SERVICIO);
        }
        if (servicios.findByCodigo(p.codigo()).isPresent()) {
            throw ApiException.conflicto("Ya existe un servicio con el código " + p.codigo());
        }
        return servicios.save(new Servicio(p.codigo(), p.nombre(), p.descripcion(), categoria, p.tarifaManoDeObra()));
    }

    public List<Servicio> listar(boolean soloActivos) {
        return soloActivos ? servicios.findByActivoTrue() : servicios.findAll();
    }

    public Servicio buscar(String id) {
        return servicios.findById(id).orElseThrow(() -> ApiException.noEncontrado("Servicio no encontrado"));
    }

    /** El cambio de tarifa no afecta cotizaciones ya generadas (guardan la tarifa de su momento). */
    public Servicio cambiarTarifa(String id, double tarifa) {
        Servicio s = buscar(id);
        s.cambiarTarifa(tarifa);
        return servicios.save(s);
    }

    public Servicio cambiarActivo(String id, boolean activo) {
        Servicio s = buscar(id);
        if (activo) {
            s.activar();
        } else {
            s.desactivar();
        }
        return servicios.save(s);
    }
}
