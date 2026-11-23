package com.tallerexpress.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tallerexpress.model.Entrega;

public interface EntregaRepository extends MongoRepository<Entrega, String> {

    Optional<Entrega> findByOrdenId(String ordenId);
}
