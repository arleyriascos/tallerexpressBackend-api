package com.tallerexpress.model;

import com.tallerexpress.exception.ApiException;
import com.tallerexpress.util.Reloj;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ordenes")
public class OrdenReparacion {

    @Id
    private String id;
    @Indexed(unique = true)
    private String numero;
    @Indexed
    private String vehiculoId;
    @Indexed
    private String clienteId;
    private LocalDateTime fechaRecepcion;
    private String motivoIngreso;
    private String sintomas;
    private EstadoOrden estado;
    private NivelUrgencia urgencia;
    private String justificacionUrgencia;
    private String mecanicoId;
    private String especialidadRequerida;
    private Diagnostico diagnostico;
    private List<Cotizacion> cotizaciones;
    private List<CambioDeEstado> historial;
    private List<SolicitudEscalamiento> solicitudesEscalamiento;
    private String registradoPorId;

    public OrdenReparacion() {
    }

    public OrdenReparacion(String numero, String vehiculoId, String clienteId, String motivoIngreso, String sintomas, NivelUrgencia urgencia, String registradoPorId, String registradoPorNombre) {
        this.numero = numero;
        this.vehiculoId = vehiculoId;
        this.clienteId = clienteId;
        this.motivoIngreso = motivoIngreso;
        this.sintomas = sintomas;
        this.urgencia = urgencia;
        this.registradoPorId = registradoPorId;
        this.fechaRecepcion = Reloj.ahora();
        this.estado = EstadoOrden.RECIBIDO;
        this.cotizaciones = new ArrayList<>();
        this.historial = new ArrayList<>();
        this.solicitudesEscalamiento = new ArrayList<>();
        this.historial.add(new CambioDeEstado(null, EstadoOrden.RECIBIDO, "Recepción del vehículo", registradoPorId, registradoPorNombre, fechaRecepcion));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(String vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(LocalDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public String getMotivoIngreso() {
        return motivoIngreso;
    }

    public void setMotivoIngreso(String motivoIngreso) {
        this.motivoIngreso = motivoIngreso;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public NivelUrgencia getUrgencia() {
        return urgencia;
    }

    public void setUrgencia(NivelUrgencia urgencia) {
        this.urgencia = urgencia;
    }

    public String getJustificacionUrgencia() {
        return justificacionUrgencia;
    }

    public void setJustificacionUrgencia(String justificacionUrgencia) {
        this.justificacionUrgencia = justificacionUrgencia;
    }

    public String getMecanicoId() {
        return mecanicoId;
    }

    public void setMecanicoId(String mecanicoId) {
        this.mecanicoId = mecanicoId;
    }

    public String getEspecialidadRequerida() {
        return especialidadRequerida;
    }

    public void setEspecialidadRequerida(String especialidadRequerida) {
        this.especialidadRequerida = especialidadRequerida;
    }

    public Diagnostico getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(Diagnostico diagnostico) {
        this.diagnostico = diagnostico;
    }

    public List<Cotizacion> getCotizaciones() {
        return cotizaciones;
    }

    public void setCotizaciones(List<Cotizacion> cotizaciones) {
        this.cotizaciones = cotizaciones;
    }

    public List<CambioDeEstado> getHistorial() {
        return historial;
    }

    public void setHistorial(List<CambioDeEstado> historial) {
        this.historial = historial;
    }

    public List<SolicitudEscalamiento> getSolicitudesEscalamiento() {
        return solicitudesEscalamiento;
    }

    public void setSolicitudesEscalamiento(List<SolicitudEscalamiento> solicitudesEscalamiento) {
        this.solicitudesEscalamiento = solicitudesEscalamiento;
    }

    public String getRegistradoPorId() {
        return registradoPorId;
    }

    public void setRegistradoPorId(String registradoPorId) {
        this.registradoPorId = registradoPorId;
    }

    /**
     * Cambia el estado validando la tabla de transiciones (HU-16) y registra
     * el cambio en la trazabilidad, que solo crece (HU-17).
     */
    public void cambiarEstado(EstadoOrden nuevoEstado, String usuarioId, String usuarioNombre, String motivo) {
        if (!estado.puedePasarA(nuevoEstado)) {
            throw ApiException.conflicto("Transición no permitida: " + estado.getNombre() + " → " + nuevoEstado.getNombre());
        }
        boolean retroceso = estado == EstadoOrden.EN_REPARACION && nuevoEstado == EstadoOrden.EN_DIAGNOSTICO;
        boolean cierre = estado == EstadoOrden.COTIZADO && nuevoEstado == EstadoOrden.RECHAZADO;
        if ((retroceso || cierre) && (motivo == null || motivo.isBlank())) {
            throw ApiException.solicitudInvalida("El motivo es obligatorio en retrocesos y cierres por rechazo");
        }
        historial.add(new CambioDeEstado(estado, nuevoEstado, motivo, usuarioId, usuarioNombre, Reloj.ahora()));
        this.estado = nuevoEstado;
    }

    /** La urgencia solo cambia mientras la cotización no esté aprobada (HU-14). */
    public void cambiarUrgencia(NivelUrgencia nueva, String justificacion) {
        if (estado.esAprobadoOPosterior() || estado == EstadoOrden.RECHAZADO) {
            throw ApiException.conflicto("La urgencia no puede modificarse después de aprobar la cotización");
        }
        this.urgencia = nueva;
        this.justificacionUrgencia = justificacion;
    }

    /** Asigna al mecánico responsable; el cambio de estado lo hace el servicio (HU-11). */
    public void asignarMecanico(String mecanicoId, String especialidad) {
        this.mecanicoId = mecanicoId;
        this.especialidadRequerida = especialidad;
    }

    public Optional<Cotizacion> cotizacionVigente() {
        if (cotizaciones == null || cotizaciones.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(cotizaciones.get(cotizaciones.size() - 1));
    }

    /** Agrega una cotización nueva; la pendiente anterior queda como reemplazada (HU-06). */
    public void agregarCotizacion(Cotizacion nueva) {
        for (Cotizacion c : cotizaciones) {
            c.reemplazar();
        }
        cotizaciones.add(nueva);
    }

    public void agregarSolicitudEscalamiento(SolicitudEscalamiento solicitud) {
        solicitudesEscalamiento.add(solicitud);
    }

    public Optional<SolicitudEscalamiento> buscarSolicitud(String solicitudId) {
        return solicitudesEscalamiento.stream().filter(s -> s.getId().equals(solicitudId)).findFirst();
    }

}
