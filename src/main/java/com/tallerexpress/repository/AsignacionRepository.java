package com.tallerexpress.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tallerexpress.model.Asignacion;

public interface AsignacionRepository extends MongoRepository<Asignacion, String> {

    List<Asignacion> findByMecanicoId(String mecanicoId);

    List<Asignacion> findByOrdenId(String ordenId);
}
