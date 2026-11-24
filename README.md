# TallerExpress API

Backend REST (Java 21 + Spring Boot 3.5 + MongoDB) del proyecto final de Diseño de Software.
Implementa la arquitectura de las vistas de Desarrollo y Lógica: capas
**Controller → Service → Repository → Modelo**, más la capa transversal de seguridad
(`AccesoController`, `FiltroAcceso`, `AccesoService`, `UsuarioRepository`).

## Requisitos
- JDK 21 y Maven 3.9+ (IntelliJ ya trae Maven; basta abrir la carpeta con *File → Open*).
- Una base MongoDB (Atlas gratis).

## Configuración
La cadena de conexión **no está en el código**: se lee de la variable de entorno `MONGODB_URI`.

```powershell
setx MONGODB_URI "mongodb+srv://USUARIO:CLAVE@CLUSTER.mongodb.net/tallerexpress?retryWrites=true&w=majority"
```
Cierre y abra la terminal / IntelliJ después. Variables opcionales:

| Variable | Para qué | Por defecto |
|---|---|---|
| `MONGODB_URI` | conexión a MongoDB | `mongodb://localhost:27017/tallerexpress` |
| `TALLER_CLAVE_INICIAL` | clave de los usuarios de prueba | `Taller2026*` |
| `PORT` | puerto HTTP | `8080` |

## Ejecutar
```powershell
mvn spring-boot:run          # o el botón Run de IntelliJ sobre TallerExpressApplication
mvn test                     # pruebas unitarias de las reglas de negocio
```
Swagger: http://localhost:8080/swagger-ui.html (botón **Authorize** → pegar el token).
Con la base vacía se crean usuarios de prueba: `admin`, `recepcion`, `mecanico1`, `mecanico2`,
`cliente1` (clave = `TALLER_CLAVE_INICIAL`), 5 servicios y un vehículo `ABC123`.

## Seguridad
`POST /api/acceso/iniciar-sesion` devuelve un token. Todo `/api/**` exige `Authorization: Bearer <token>`.
Las contraseñas se guardan con BCrypt. Las sesiones viven en memoria (8 h): al reiniciar el servidor hay que iniciar sesión otra vez.

| Rol | Puede |
|---|---|
| ADMINISTRADOR | todo, incluido catálogo y mecánicos |
| RECEPCIONISTA | clientes, vehículos, órdenes, urgencia, asignación, cotización, entrega |
| MECANICO | ver sus órdenes, diagnosticar, escalar, iniciar reparación, marcar listo |
| CLIENTE | ver sus órdenes, aprobar/rechazar su cotización |

## Flujo de estados
Recibido → En diagnóstico → Cotizado → Aprobado → En reparación → Listo para entrega → Entregado.
Desde Cotizado también a Rechazado (final) o de vuelta a En diagnóstico; desde En reparación se puede retroceder a En diagnóstico (con motivo).
Cada cambio queda en el historial (quién, cuándo, motivo).

Cotización: `(mano de obra + repuestos) × factor` con factor Baja 1.0 / Media 1.15 / Alta 1.30.

## Endpoints principales
| Operación | Endpoint |
|---|---|
| Crear orden | `POST /api/ordenes` |
| Asignar mecánico | `POST /api/ordenes/{id}/asignacion` |
| Diagnóstico | `POST /api/ordenes/{id}/diagnostico` |
| Cotizar / aprobar / rechazar | `POST /api/ordenes/{id}/cotizaciones[/aprobar|/rechazar]` |
| Cambiar estado | `PATCH /api/ordenes/{id}/estado` |
| Urgencia | `PATCH /api/ordenes/{id}/urgencia` |
| Escalar / traspasar | `POST /api/ordenes/{id}/escalamientos` · `/traspaso` |
| Entrega y comprobante | `POST /api/ordenes/{id}/entrega` · `GET .../entrega/comprobante` |
| Estado e historial | `GET /api/ordenes/{id}/estado` |

## Pruebas en Postman
La carpeta [`postman/`](postman) trae la colección con una carpeta por módulo de la API y dos entornos:

| Archivo | Para qué |
|---|---|
| `postman/TallerExpress.postman_collection.json` | 83 requests por módulo, con pruebas automáticas del código HTTP |
| `postman/TallerExpress-Produccion.postman_environment.json` | apunta a `https://tallerexpress-api.onrender.com` |
| `postman/TallerExpress-Local.postman_environment.json` | apunta a `http://localhost:8080` |

Importar los tres archivos, elegir el entorno y usar *Run collection* en orden. Los tokens se piden solos; en Render gratuito la primera petición puede tardar cerca de un minuto. La colección anterior (`TallerExpress.postman_collection.json`, en la raíz) sigue disponible como flujo simple.

## Despliegue (Render + MongoDB Atlas)
1. Subir esta carpeta a un repositorio de GitHub.
2. En Render: *New → Web Service* → repositorio → *Runtime: Docker* (usa el `Dockerfile`).
3. En *Environment* agregar `MONGODB_URI` (la cadena de Atlas) y `TALLER_CLAVE_INICIAL`.
4. En Atlas → *Network Access* permitir `0.0.0.0/0` (Render no tiene IP fija).
5. La URL pública queda como `https://<servicio>.onrender.com/swagger-ui.html`.
