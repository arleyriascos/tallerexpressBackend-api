package com.tallerexpress.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tallerexpress.model.Cliente;

public interface ClienteRepository extends MongoRepository<Cliente, String> {

    Optional<Cliente> findByDocumentoIdentidad(String documentoIdentidad);
}
