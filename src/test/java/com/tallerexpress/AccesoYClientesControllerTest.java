package com.tallerexpress;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerexpress.controller.AccesoController;
import com.tallerexpress.controller.ClienteVehiculoController;
import com.tallerexpress.exception.ApiException;
import com.tallerexpress.model.Administrador;
import com.tallerexpress.model.Cliente;
import com.tallerexpress.model.Recepcionista;
import com.tallerexpress.model.Usuario;
import com.tallerexpress.repository.UsuarioRepository;
import com.tallerexpress.service.AccesoService;
import com.tallerexpress.service.ClienteVehiculoService;

/**
 * Pruebas de la capa web: filtro de acceso, roles, validaciones y códigos HTTP.
 * Usan el AccesoService real con el repositorio simulado, sin base de datos.
 */
@WebMvcTest(controllers = { AccesoController.class, ClienteVehiculoController.class })
@Import(AccesoService.class)
class AccesoYClientesControllerTest {

    private static final String CLAVE = "Clave-de-prueba-1";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;

    @MockBean
    UsuarioRepository usuarios;
    @MockBean
    ClienteVehiculoService clientes;

    @BeforeEach
    void usuariosDePrueba() {
        Usuario admin = new Administrador("admin", Usuario.cifrar(CLAVE), "Administrador");
        admin.setId("u-admin");
        Usuario recep = new Recepcionista("recepcion", Usuario.cifrar(CLAVE), "Recepción");
        recep.setId("u-recep");
        Usuario cliente = new Cliente("cliente1", Usuario.cifrar(CLAVE), "Cliente Uno", "3000000000", "100");
        cliente.setId("u-cliente");
        for (Usuario u : new Usuario[] { admin, recep, cliente }) {
            when(usuarios.findByNombreUsuario(u.getNombreUsuario())).thenReturn(Optional.of(u));
            when(usuarios.findById(u.getId())).thenReturn(Optional.of(u));
        }
    }

    private String token(String usuario) throws Exception {
        String cuerpo = "{\"nombreUsuario\":\"" + usuario + "\",\"contrasena\":\"" + CLAVE + "\"}";
        String resp = mvc.perform(post("/api/acceso/iniciar-sesion").contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode nodo = json.readTree(resp);
        return nodo.get("token").asText();
    }

    private static String clienteJson(String email, String clave) {
        return "{\"nombreCompleto\":\"Ana Pérez\",\"documentoIdentidad\":\"123\",\"telefono\":\"3001234567\","
                + "\"email\":\"" + email + "\",\"nombreUsuario\":\"ana\",\"contrasena\":\"" + clave + "\"}";
    }

    @Test
    void inicioDeSesionCorrectoDevuelveToken() throws Exception {
        mvc.perform(post("/api/acceso/iniciar-sesion").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombreUsuario\":\"admin\",\"contrasena\":\"" + CLAVE + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    void claveIncorrectaDevuelve401() throws Exception {
        mvc.perform(post("/api/acceso/iniciar-sesion").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombreUsuario\":\"admin\",\"contrasena\":\"otra\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void inicioDeSesionSinCamposDevuelve400() throws Exception {
        mvc.perform(post("/api/acceso/iniciar-sesion").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sinTokenDevuelve401() throws Exception {
        mvc.perform(get("/api/clientes")).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenFalsoDevuelve401() throws Exception {
        mvc.perform(get("/api/clientes").header("Authorization", "Bearer inventado"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void clienteNoPuedeRegistrarClientes() throws Exception {
        mvc.perform(post("/api/clientes").header("Authorization", "Bearer " + token("cliente1"))
                .contentType(MediaType.APPLICATION_JSON).content(clienteJson("ana@correo.com", CLAVE)))
                .andExpect(status().isForbidden());
    }

    @Test
    void recepcionistaRegistraClienteYRecibe201() throws Exception {
        Cliente guardado = new Cliente("ana", "x", "Ana Pérez", "3001234567", "123");
        guardado.setId("c1");
        when(clientes.registrarCliente(any())).thenReturn(guardado);
        mvc.perform(post("/api/clientes").header("Authorization", "Bearer " + token("recepcion"))
                .contentType(MediaType.APPLICATION_JSON).content(clienteJson("ana@correo.com", CLAVE)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("c1"));
    }

    @Test
    void correoInvalidoDevuelve400() throws Exception {
        mvc.perform(post("/api/clientes").header("Authorization", "Bearer " + token("recepcion"))
                .contentType(MediaType.APPLICATION_JSON).content(clienteJson("no-es-un-correo", CLAVE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void contrasenaCortaDevuelve400() throws Exception {
        mvc.perform(post("/api/clientes").header("Authorization", "Bearer " + token("recepcion"))
                .contentType(MediaType.APPLICATION_JSON).content(clienteJson("ana@correo.com", "123")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void documentoRepetidoDevuelve409() throws Exception {
        when(clientes.registrarCliente(any())).thenThrow(ApiException.conflicto("Ya existe un cliente con el documento 123"));
        mvc.perform(post("/api/clientes").header("Authorization", "Bearer " + token("admin"))
                .contentType(MediaType.APPLICATION_JSON).content(clienteJson("ana@correo.com", CLAVE)))
                .andExpect(status().isConflict());
    }

    @Test
    void cierreDeSesionInvalidaElToken() throws Exception {
        String t = token("admin");
        mvc.perform(post("/api/acceso/cerrar-sesion").header("Authorization", "Bearer " + t))
                .andExpect(status().isOk());
        mvc.perform(get("/api/clientes").header("Authorization", "Bearer " + t))
                .andExpect(status().isUnauthorized());
    }
}
