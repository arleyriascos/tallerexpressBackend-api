package com.tallerexpress.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tallerexpress.model.Servicio;

public interface ServicioRepository extends MongoRepository<Servicio, String> {

    Optional<Servicio> findByCodigo(String codigo);

    List<Servicio> findByActivoTrue();
}
