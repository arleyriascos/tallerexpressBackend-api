package com.tallerexpress.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tallerexpress.dto.Peticiones;
import com.tallerexpress.exception.ApiException;
import com.tallerexpress.model.Cliente;
import com.tallerexpress.model.OrdenReparacion;
import com.tallerexpress.model.Vehiculo;
import com.tallerexpress.repository.ClienteRepository;
import com.tallerexpress.repository.OrdenRepository;
import com.tallerexpress.repository.UsuarioRepository;
import com.tallerexpress.repository.VehiculoRepository;

/** Clientes, vehículos e historial de reparaciones (HU-01, HU-02, HU-03). */
@Service
public class ClienteVehiculoService {

    private final ClienteRepository clientes;
    private final VehiculoRepository vehiculos;
    private final UsuarioRepository usuarios;
    private final OrdenRepository ordenes;

    public ClienteVehiculoService(ClienteRepository clientes, VehiculoRepository vehiculos,
            UsuarioRepository usuarios, OrdenRepository ordenes) {
        this.clientes = clientes;
        this.vehiculos = vehiculos;
        this.usuarios = usuarios;
        this.ordenes = ordenes;
    }

    public Cliente registrarCliente(Peticiones.NuevoCliente p) {
        if (clientes.findByDocumentoIdentidad(p.documentoIdentidad()).isPresent()) {
            throw ApiException.conflicto("Ya existe un cliente con el documento " + p.documentoIdentidad());
        }
        if (usuarios.existsByNombreUsuario(p.nombreUsuario())) {
            throw ApiException.conflicto("El nombre de usuario ya está en uso");
        }
        Cliente c = new Cliente(p.nombreUsuario(), com.tallerexpress.model.Usuario.cifrar(p.contrasena()),
                p.nombreCompleto(), p.telefono(), p.documentoIdentidad());
        c.setEmail(p.email());
        c.setDireccion(p.direccion());
        return clientes.save(c);
    }

    public Cliente buscarCliente(String id) {
        return clientes.findById(id).orElseThrow(() -> ApiException.noEncontrado("Cliente no encontrado"));
    }

    public Cliente buscarPorDocumento(String documento) {
        return clientes.findByDocumentoIdentidad(documento)
                .orElseThrow(() -> ApiException.noEncontrado("No hay cliente con el documento " + documento));
    }

    public List<Cliente> listarClientes() {
        return clientes.findAll();
    }

    public Vehiculo registrarVehiculo(Peticiones.NuevoVehiculo p) {
        buscarCliente(p.clienteId());
        String placa = normalizarPlaca(p.placa());
        if (vehiculos.findByPlaca(placa).isPresent()) {
            throw ApiException.conflicto("Ya existe un vehículo con la placa " + placa);
        }
        return vehiculos.save(new Vehiculo(p.clienteId(), placa, p.marca(), p.modelo(), p.anio(),
                p.color(), p.vin(), p.kilometraje()));
    }

    public Vehiculo buscarVehiculoPorPlaca(String placa) {
        return vehiculos.findByPlaca(normalizarPlaca(placa))
                .orElseThrow(() -> ApiException.noEncontrado("No hay vehículo con la placa " + placa));
    }

    public Vehiculo buscarVehiculoPorId(String id) {
        return vehiculos.findById(id).orElseThrow(() -> ApiException.noEncontrado("Vehículo no encontrado"));
    }

    public List<Vehiculo> listarVehiculos(String clienteId) {
        return clienteId == null ? vehiculos.findAll() : vehiculos.findByClienteId(clienteId);
    }

    /** Historial de órdenes del vehículo, de la más reciente a la más antigua. */
    public List<OrdenReparacion> consultarHistorial(String placa) {
        Vehiculo v = buscarVehiculoPorPlaca(placa);
        List<OrdenReparacion> lista = ordenes.findByVehiculoId(v.getId());
        lista.sort((a, b) -> b.getFechaRecepcion().compareTo(a.getFechaRecepcion()));
        return lista;
    }

    public static String normalizarPlaca(String placa) {
        return placa == null ? null : placa.trim().toUpperCase().replace(" ", "").replace("-", "");
    }
}
