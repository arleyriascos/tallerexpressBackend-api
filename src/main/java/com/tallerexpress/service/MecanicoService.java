package com.tallerexpress.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tallerexpress.dto.Peticiones;
import com.tallerexpress.exception.ApiException;
import com.tallerexpress.model.Especialidad;
import com.tallerexpress.model.Mecanico;
import com.tallerexpress.model.Usuario;
import com.tallerexpress.repository.MecanicoRepository;
import com.tallerexpress.repository.UsuarioRepository;

/** Mecánicos y sus especialidades (HU-10). */
@Service
public class MecanicoService {

    private final MecanicoRepository mecanicos;
    private final UsuarioRepository usuarios;

    public MecanicoService(MecanicoRepository mecanicos, UsuarioRepository usuarios) {
        this.mecanicos = mecanicos;
        this.usuarios = usuarios;
    }

    public Mecanico registrar(Peticiones.NuevoMecanico p) {
        if (usuarios.existsByNombreUsuario(p.nombreUsuario())) {
            throw ApiException.conflicto("El nombre de usuario ya está en uso");
        }
        List<String> normalizadas = new ArrayList<>();
        for (String e : p.especialidades()) {
            String n = Especialidad.normalizar(e);
            if (n == null) {
                throw ApiException.solicitudInvalida("Especialidad inválida: " + e + ". Use: " + Especialidad.CATALOGO);
            }
            if (!normalizadas.contains(n)) {
                normalizadas.add(n);
            }
        }
        return mecanicos.save(new Mecanico(p.nombreUsuario(), Usuario.cifrar(p.contrasena()), p.nombreCompleto(),
                p.telefono(), p.documentoIdentidad(), normalizadas));
    }

    public Mecanico buscar(String id) {
        return mecanicos.findById(id).orElseThrow(() -> ApiException.noEncontrado("Mecánico no encontrado"));
    }

    public List<Mecanico> listar(String especialidad) {
        if (especialidad == null || especialidad.isBlank()) {
            return mecanicos.findAll();
        }
        String n = Especialidad.normalizar(especialidad);
        if (n == null) {
            throw ApiException.solicitudInvalida("Especialidad inválida: " + especialidad);
        }
        return mecanicos.findByEspecialidadesContaining(n);
    }
}
