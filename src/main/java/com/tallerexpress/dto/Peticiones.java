package com.tallerexpress.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Cuerpos de las peticiones de la API, agrupados para mantener el proyecto compacto. */
public final class Peticiones {

    private Peticiones() {
    }

    public record InicioSesion(@NotBlank String nombreUsuario, @NotBlank String contrasena) {
    }

    public record NuevoCliente(@NotBlank @Size(max = 120) String nombreCompleto,
            @NotBlank @Size(max = 20) String documentoIdentidad,
            @NotBlank @Size(max = 20) String telefono, @Email @Size(max = 120) String email,
            @Size(max = 200) String direccion,
            @NotBlank @Size(min = 3, max = 40) String nombreUsuario,
            @NotBlank @Size(min = 8, max = 72) String contrasena) {
    }

    public record NuevoVehiculo(@NotBlank String clienteId, @NotBlank @Size(max = 10) String placa,
            @NotBlank @Size(max = 40) String marca,
            @NotBlank @Size(max = 40) String modelo, @Min(1950) int anio, @Size(max = 30) String color,
            @Size(max = 17) String vin,
            @PositiveOrZero int kilometraje) {
    }

    public record NuevoMecanico(@NotBlank @Size(max = 120) String nombreCompleto,
            @NotBlank @Size(max = 20) String documentoIdentidad,
            @NotBlank @Size(max = 20) String telefono, @NotEmpty List<String> especialidades,
            @NotBlank @Size(min = 3, max = 40) String nombreUsuario,
            @NotBlank @Size(min = 8, max = 72) String contrasena) {
    }

    public record NuevoServicio(@NotBlank String codigo, @NotBlank String nombre, String descripcion,
            @NotBlank String categoria, @PositiveOrZero double tarifaManoDeObra) {
    }

    public record CambioTarifa(@PositiveOrZero double tarifaManoDeObra) {
    }

    public record NuevaOrden(@NotBlank String placa, @NotBlank String motivoIngreso, String sintomas,
            @NotBlank String urgencia) {
    }

    public record CambioEstado(@NotBlank String estado, String motivo) {
    }

    public record CambioUrgencia(@NotBlank String urgencia, String justificacion) {
    }

    public record RepuestoDto(@NotBlank String descripcion, @Min(1) int cantidad, @PositiveOrZero double costoUnitario) {
    }

    public record NuevoDiagnostico(@NotBlank String descripcion, @NotEmpty List<String> servicioIds,
            @Valid List<RepuestoDto> repuestos) {
    }

    public record Rechazo(@NotBlank String motivo) {
    }

    public record Asignar(@NotBlank String mecanicoId, @NotBlank String especialidad) {
    }

    public record Traspaso(@NotBlank String mecanicoId, @NotBlank String especialidad, @NotBlank String motivo,
            String solicitudId) {
    }

    public record NuevoEscalamiento(@NotBlank String especialidad, @NotBlank String motivo) {
    }

}
