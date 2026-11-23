package com.tallerexpress.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tallerexpress.model.Mecanico;

public interface MecanicoRepository extends MongoRepository<Mecanico, String> {

    List<Mecanico> findByEspecialidadesContaining(String especialidad);
}
