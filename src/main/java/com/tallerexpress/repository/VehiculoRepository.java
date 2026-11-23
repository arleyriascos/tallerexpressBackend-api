package com.tallerexpress.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tallerexpress.model.Vehiculo;

public interface VehiculoRepository extends MongoRepository<Vehiculo, String> {

    Optional<Vehiculo> findByPlaca(String placa);

    List<Vehiculo> findByClienteId(String clienteId);
}
