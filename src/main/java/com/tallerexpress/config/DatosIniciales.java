package com.tallerexpress.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tallerexpress.model.Administrador;
import com.tallerexpress.model.Cliente;
import com.tallerexpress.model.Mecanico;
import com.tallerexpress.model.Recepcionista;
import com.tallerexpress.model.Servicio;
import com.tallerexpress.model.Usuario;
import com.tallerexpress.model.Vehiculo;
import com.tallerexpress.repository.ClienteRepository;
import com.tallerexpress.repository.MecanicoRepository;
import com.tallerexpress.repository.ServicioRepository;
import com.tallerexpress.repository.UsuarioRepository;
import com.tallerexpress.repository.VehiculoRepository;

/**
 * Carga datos de prueba la primera vez (base vacía): usuarios de cada rol,
 * catálogo de servicios, un cliente y un vehículo. La clave de todos los
 * usuarios de prueba sale de la variable TALLER_CLAVE_INICIAL.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DatosIniciales.class);

    private final UsuarioRepository usuarios;
    private final ServicioRepository servicios;
    private final ClienteRepository clientes;
    private final MecanicoRepository mecanicos;
    private final VehiculoRepository vehiculos;
    private final String claveInicial;

    public DatosIniciales(UsuarioRepository usuarios, ServicioRepository servicios, ClienteRepository clientes,
            MecanicoRepository mecanicos, VehiculoRepository vehiculos,
            @Value("${taller.clave-inicial}") String claveInicial) {
        this.usuarios = usuarios;
        this.servicios = servicios;
        this.clientes = clientes;
        this.mecanicos = mecanicos;
        this.vehiculos = vehiculos;
        this.claveInicial = claveInicial;
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() == 0) {
            String clave = Usuario.cifrar(claveInicial);
            usuarios.save(new Administrador("admin", clave, "Administrador del Taller"));
            usuarios.save(new Recepcionista("recepcion", clave, "Rosa Recepción"));
            mecanicos.save(new Mecanico("mecanico1", clave, "Carlos Motor", "3001112233", "1010101010",
                    List.of("Mecánica general", "Frenos y suspensión")));
            mecanicos.save(new Mecanico("mecanico2", clave, "Elena Voltaje", "3004445566", "2020202020",
                    List.of("Electricidad automotriz", "Aire acondicionado")));
            Cliente cliente = new Cliente("cliente1", clave, "Juan Pérez", "3157778899", "1088000111");
            cliente.setEmail("juan@example.com");
            clientes.save(cliente);
            vehiculos.save(new Vehiculo(cliente.getId(), "ABC123", "Mazda", "3", 2018, "Rojo", null, 62000));
            LOG.info("Usuarios de prueba creados: admin, recepcion, mecanico1, mecanico2, cliente1");
        }
        if (servicios.count() == 0) {
            servicios.save(new Servicio("SRV-001", "Cambio de aceite y filtro", "Aceite sintético y filtro",
                    "Mantenimiento preventivo", 60000));
            servicios.save(new Servicio("SRV-002", "Cambio de pastillas de freno", "Eje delantero o trasero",
                    "Frenos y suspensión", 120000));
            servicios.save(new Servicio("SRV-003", "Diagnóstico eléctrico", "Revisión con escáner",
                    "Electricidad automotriz", 90000));
            servicios.save(new Servicio("SRV-004", "Revisión de aire acondicionado", "Carga y fugas",
                    "Aire acondicionado", 100000));
            servicios.save(new Servicio("SRV-005", "Ajuste de motor", "Afinación completa",
                    "Mecánica general", 150000));
            LOG.info("Catálogo de servicios inicial creado");
        }
    }
}
