package com.tallerexpress.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tallerexpress.model.EstadoOrden;
import com.tallerexpress.model.OrdenReparacion;

public interface OrdenRepository extends MongoRepository<OrdenReparacion, String> {

    Optional<OrdenReparacion> findByNumero(String numero);

    List<OrdenReparacion> findByEstado(EstadoOrden estado);

    List<OrdenReparacion> findByMecanicoId(String mecanicoId);

    List<OrdenReparacion> findByClienteId(String clienteId);

    List<OrdenReparacion> findByVehiculoId(String vehiculoId);
}
