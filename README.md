# Instrumentum

Plataforma de gestión integral de equipamiento técnico para músicos y bandas.

Instrumentum centraliza el inventario, el mantenimiento técnico, la configuración de equipamiento, la planificación de giras y eventos, las finanzas y el merchandising de una banda, optimizando el rendimiento en vivo y en estudio mediante una **arquitectura de microservicios** basada en Java Spring Boot, Spring Cloud Gateway y MySQL.

---

## Colaboradores

- Victoria Bustos
- Oscar Tavolari

---

## Índice

- [Descripción del Sistema](#descripción-del-sistema)
- [Arquitectura de Microservicios](#arquitectura-de-microservicios)
- [Funcionalidades](#funcionalidades)
- [Requisitos Funcionales](#requisitos-funcionales)
- [Requisitos No Funcionales](#requisitos-no-funcionales)
- [Bibliotecas Utilizadas](#bibliotecas-utilizadas)
- [Herramientas de Instalación](#herramientas-de-instalación)
- [Autenticación y Seguridad](#autenticación-y-seguridad)
- [API Endpoints](#api-endpoints)
- [Documentación Interactiva (Swagger)](#documentación-interactiva-swagger)
- [Modelo Entidad-Relación](#modelo-entidad-relación)
- [Stack Tecnológico](#stack-tecnológico)
- [Gestión de Riesgos](#gestión-de-riesgos)

---

## Descripción del Sistema

El sistema fue migrado de una arquitectura monolítica en capas (Controller → Service → Repository) a una **arquitectura de microservicios**, donde cada dominio de negocio se ejecuta como una aplicación Spring Boot independiente, con su propia base de datos MySQL y su propio puerto. Todo el tráfico externo entra a través de un **API Gateway** (Spring Cloud Gateway) que centraliza el enrutamiento, el CORS y la validación de sesión mediante JWT antes de reenviar la petición al microservicio correspondiente.

### Gestión de Datos y Persistencia
Cada microservicio gestiona su propia base de datos MySQL (patrón *Database per Service*), administrada mediante XAMPP. La comunicación entre microservicios para validaciones cruzadas (por ejemplo, que una Banda exista antes de crear un Evento) se realiza de forma síncrona mediante `WebClient` (Spring WebFlux). La interacción con la lógica de negocio desde el cliente se realiza a través de peticiones HTTP documentadas en Postman.

### Ciclo de Vida del Inventario (Gear Management)
- **Segmentación por tipo:** Los instrumentos registran atributos de maderas y pastillas; los equipos electrónicos validan campos de voltaje y amperaje (mA).
- **Gestión de estados:** El sistema calcula automáticamente el tiempo transcurrido desde la última intervención técnica. Si el intervalo supera los **6 meses**, se dispara una alerta de mantenimiento preventivo.

### Módulo de Mantenimiento
Funciona como una bitácora histórica vinculada a cada activo mediante `equipoId`. Al eliminar un equipo desde Inventario, el propio servicio de Inventario coordina la limpieza de los registros de servicio asociados en el microservicio de Mantenimiento, manteniendo la integridad referencial entre servicios.

### Inteligencia del Rig Builder
Permite gestionar la relación compleja entre el repertorio y el equipamiento:
- Define un **orden lógico de conexión** (cadena de señal) para los equipos dentro de cada canción mediante la tabla `Equipo_cancion`.
- Persiste **notas de ejecución** y **seteo de perillas** por canción, funcionando como un manual técnico personalizado.

### Gestión de Giras, Eventos y Logística
- **Giras:** Una banda puede planificar una gira completa con un itinerario de paradas (`Parada_gira`), cada una con ciudad, alojamiento y transporte.
- **Eventos:** Registro de shows o conciertos puntuales, asociados a una banda y a un repertorio de canciones.
- **Logística:** Permite organizar qué equipos del inventario viajan dentro de qué contenedores/cajas (`Contenedor`, `Contenedor_equipo`) para cada show o gira.

### Finanzas y Merchandising
- **Finanzas:** Registro de ingresos y egresos (`Transaccion`) asociados a cada banda.
- **Merchandising:** Inventario de productos de la banda (poleras, discos, parches, gorros) y registro de ventas a fanáticos, descontando stock automáticamente y, opcionalmente, vinculando la venta a un evento específico.

### Autenticación y Autorización
Un microservicio dedicado (`service-auth`) administra el registro de usuarios, el cifrado de contraseñas (BCrypt) y la emisión de tokens JWT. El API Gateway valida ese token en cada petición antes de reenviarla a los demás microservicios, los cuales no implementan seguridad propia: el perímetro de seguridad se concentra en el Gateway.

### Lógica de Cálculo Eléctrico
Al consultar el setup de una canción, el sistema **suma automáticamente los miliamperios (mA)** de todos los dispositivos activos vinculados, validando la compatibilidad con la fuente de poder.

---

## Arquitectura de Microservicios

Todas las peticiones del cliente (Postman, frontend, etc.) ingresan únicamente por el **API Gateway**, en el puerto `8181`. El Gateway resuelve internamente a qué microservicio reenviar la petición según el `Path` configurado, y agrega un filtro de autenticación (`AuthFilter`) a todas las rutas excepto `/auth/**`.

| Microservicio | Puerto | Base de Datos | Responsabilidad |
|---|---|---|---|
| **api-gateway** | 8181 | – | Puerta de entrada única, enrutamiento, CORS y validación JWT |
| **service-auth** | 8089 | `db_auth` | Registro, login y emisión de tokens JWT |
| **service-usuario** | 8081 | `db_usuarios` | Gestión de Usuarios y Bandas |
| **service-inventario** | 8082 | `db_inventario` | Gestión de Equipos, Marcas y Categorías |
| **service-specs** | 8083 | `db_specs` | Especificaciones técnicas (instrumento / electrónica) |
| **service-mantenimiento** | 8084 | `db_mantenimiento` | Bitácora de mantenimientos y alertas preventivas |
| **service-rig** | 8085 | `db_rig` | Canciones y Rig Builder (`Equipo_cancion`) |
| **service-evento** | 8086 | `db_eventos` | Gestión de eventos / conciertos |
| **service-finanza** | 8087 | `db_finanza` | Transacciones financieras por banda |
| **service-gira** | 8088 | `db_gira` | Giras e itinerarios (`Parada_gira`) |
| **service-logistica** | 8090 | `db_logistica` | Contenedores y empaque de equipos para shows |
| **service-merchandising** | 8091 | `db_merchandising` | Productos y ventas de merchandising |

> **Nota:** el Gateway incluye la dependencia de Eureka, pero el *service discovery* está deshabilitado (`register-with-eureka: false`); el enrutamiento es **estático**, apuntando directamente a `http://localhost:{puerto}` de cada microservicio.

---

## Funcionalidades

### A. Inventario con Especificaciones Técnicas
Registro detallado de equipos con atributos específicos:
- **Instrumentos:** Marca, modelo, año, tipo de madera, configuración de pastillas (pickups) y calibre de cuerdas.
- **Amplificación y pedales:** Especificaciones de voltaje (9V/12V), consumo de corriente (mA) y tipo de circuito.

### B. Módulo de Mantenimiento Preventivo
Control del ciclo de vida del equipo para evitar fallos técnicos:
- **Bitácora de servicio:** Registro de calibraciones, cambios de cuerdas y limpieza de electrónica.
- **Alertas de tiempo:** Verificación de equipos sin mantención en los últimos 6 meses.

### C. Configuración por Canción — The "Rig" Builder
Asignación de equipamiento según el repertorio:
- **Asignación de gear:** Bajo, pedalera y amplificador por tema.
- **Notas de ejecución:** Técnica (uñeta, dedos) y seteo específico de perillas por canción.

### D. Giras, Eventos y Logística *(nuevo)*
- **Planificación de giras:** Itinerario de ciudades, alojamiento y transporte por parada.
- **Eventos/Shows:** Registro de conciertos con repertorio asociado.
- **Empaque para shows:** Organización de qué equipos van en qué contenedor o caja de transporte.

### E. Finanzas de la Banda *(nuevo)*
- **Movimientos financieros:** Registro de ingresos y egresos por banda, con descripción y monto.

### F. Merchandising *(nuevo)*
- **Inventario comercial:** Poleras, discos, parches y gorros con precio y stock.
- **Ventas:** Descuento automático de stock y cálculo del monto total recaudado, con vínculo opcional al evento de origen.

### G. Autenticación y Seguridad *(nuevo)*
- **Registro y login:** Creación de cuentas con contraseña cifrada (BCrypt) y emisión de un token JWT válido por 2 horas.
- **Gateway protegido:** Todas las rutas de negocio exigen un token `Bearer` válido, validado centralizadamente en el API Gateway.

### H. Funciones Adicionales
- **Exportación de Technical Rider:** Lista de equipo automática para enviar a sonidistas o productores.
- **Calculadora de carga:** Suma el consumo (mA) de los pedales seleccionados y verifica compatibilidad con la fuente de poder.

---

## Requisitos Funcionales

- **RF-01** Gestión de Inventario de Equipos: CRUD completo. Atributos de maderas/pastillas para instrumentos; voltaje, consumo (mA) y circuito para electrónica.
- **RF-02** Módulo de Mantenimiento Preventivo: Registro de servicios por equipo. Lógica de verificación para equipos con más de 6 meses sin servicio.
- **RF-03** Configuración por Canción (Rig Builder): CRUD de canciones con asignación N:N de equipos, incluyendo orden de cadena de señal y seteo de perillas.
- **RF-04** Gestión de Usuarios y Bandas: Registro y administración de usuarios y sus agrupaciones musicales.
- **RF-05** Calculadora de Carga: Algoritmo para sumar consumo (mA) por canción y comparar contra el límite de la fuente de poder.
- **RF-06** Búsqueda y Filtros: Búsqueda de equipos por nombre, marca o categoría mediante *query params*.
- **RF-07** Gestión de Eventos: CRUD de eventos/conciertos asociados a una banda y a un repertorio de canciones.
- **RF-08** Gestión de Giras: CRUD de giras y de sus paradas logísticas (ciudad, alojamiento, transporte).
- **RF-09** Gestión de Logística: Organización de equipos dentro de contenedores de transporte para shows y giras.
- **RF-10** Gestión Financiera: Registro de transacciones (ingresos/egresos) por banda.
- **RF-11** Gestión de Merchandising: CRUD de productos comerciales y registro de ventas con descuento automático de stock.
- **RF-12** Autenticación y Autorización: Registro de usuarios, login con generación de JWT y protección de los endpoints de negocio mediante el API Gateway.

---

## Requisitos No Funcionales

- **RNF-01** Tecnología: Java 21 con Spring Boot 3.5.15. Persistencia con Spring Data JPA sobre MySQL (XAMPP). Entorno de desarrollo en VS Code.
- **RNF-02** Interfaz de Pruebas: Validación de endpoints mediante Postman.
- **RNF-03** Rendimiento: Operaciones CRUD con tiempo de respuesta inferior a 2 segundos.
- **RNF-04** Seguridad de Datos: Protección contra inyección SQL nativa mediante JPA, validación de datos de entrada (Bean Validation) y autenticación basada en JWT centralizada en el API Gateway.
- **RNF-05** Mantenibilidad: Código organizado por microservicios independientes (cada uno en capas Controller, Service, Repository) y uso de Lombok para limpieza de código.
- **RNF-06** Integridad de Datos: Eliminación en cascada para mantenimientos y restricciones de clave foránea para equipos asignados a canciones activas.
- **RNF-07** Comunicación entre Servicios: Las validaciones cruzadas entre microservicios (ej. banda, equipo o evento existente) se realizan mediante `WebClient` de forma síncrona.
- **RNF-08** Documentación: Cada microservicio expone su propio contrato OpenAPI, agregado en un único Swagger UI servido desde el API Gateway.

---

## Bibliotecas Utilizadas

Listado de las dependencias (Maven) presentes en el proyecto, agrupadas por categoría. Los microservicios de dominio (`service-usuario`, `service-inventario`, `service-specs`, `service-mantenimiento`, `service-rig`, `service-evento`, `service-finanza`, `service-gira`, `service-logistica`, `service-merchandising`) comparten prácticamente el mismo set de librerías; `service-auth` y `api-gateway` agregan dependencias específicas de seguridad y enrutamiento.

### Núcleo y persistencia
- **spring-boot-starter-web** — Exposición de los endpoints REST.
- **spring-boot-starter-data-jpa** — Persistencia con Spring Data JPA / Hibernate.
- **mysql-connector-j** — Driver JDBC para MySQL.
- **spring-boot-starter-validation** — Validaciones declarativas (Jakarta Bean Validation: `@NotNull`, `@NotBlank`, `@Email`, `@Pattern`, etc.).

### Comunicación entre microservicios
- **spring-boot-starter-webflux** — Provee `WebClient`, usado por cada microservicio para validar datos contra otros servicios (ej. que la banda o el equipo existan).
- **spring-cloud-starter-gateway** *(solo api-gateway)* — Motor de enrutamiento reactivo del API Gateway.
- **spring-cloud-starter-netflix-eureka-client** *(solo api-gateway, presente pero deshabilitado)* — Cliente de *service discovery*, no utilizado en el enrutamiento actual (estático).

### Seguridad y autenticación
- **spring-boot-starter-security** *(solo service-auth)* — Filtros de seguridad y `PasswordEncoder` (BCrypt).
- **io.jsonwebtoken: jjwt-api / jjwt-impl / jjwt-jackson** (v0.11.5) — Generación (en `service-auth`) y validación (en `api-gateway`) de tokens JWT.

### Documentación de API
- **springdoc-openapi-starter-webmvc-ui** *(microservicios de dominio)* — Genera el contrato OpenAPI y la UI de Swagger de cada microservicio.
- **springdoc-openapi-starter-webflux-ui** *(api-gateway)* — Agrega en una sola UI de Swagger los contratos OpenAPI de todos los microservicios registrados.

### Utilidades y testing
- **lombok** — Reduce código repetitivo (getters, setters, constructores) mediante anotaciones como `@Data`.
- **spring-boot-starter-test** / **spring-security-test** / **reactor-test** — JUnit 5, Mockito y utilidades de testing para WebClient y seguridad.

---

## Herramientas de Instalación

Para levantar el proyecto completo en un entorno local se necesita:

1. **JDK 21** — Requisito obligatorio para compilar y ejecutar los 12 microservicios (`java -version` debe mostrar 21.x).
2. **Apache Maven** — Gestión de dependencias y construcción (`mvn clean install`). Cada microservicio incluye su propio `pom.xml`; no existe un POM padre multi-módulo, por lo que cada carpeta se construye y ejecuta de forma independiente.
3. **XAMPP (MySQL/MariaDB + phpMyAdmin)** — Motor de base de datos. No es necesario crear las bases manualmente: cada microservicio usa `createDatabaseIfNotExist=true`, por lo que las crea automáticamente al levantarse (`db_auth`, `db_usuarios`, `db_inventario`, `db_specs`, `db_mantenimiento`, `db_rig`, `db_eventos`, `db_finanza`, `db_gira`, `db_logistica`, `db_merchandising`).
4. **Postman** — Cliente recomendado para probar los endpoints, incluyendo el flujo de login y el envío del header `Authorization: Bearer <token>`.
5. **Visual Studio Code** (con extension pack de Java/Spring Boot) — IDE de desarrollo utilizado en el proyecto. También es compatible con IntelliJ IDEA o Eclipse STS.
6. **Git** — Control de versiones para clonar/descargar el repositorio.

### Pasos sugeridos de ejecución

```bash
# 1. Iniciar el servicio MySQL desde el panel de control de XAMPP

# 2. Levantar cada microservicio (en terminales o pestañas separadas), por ejemplo:
cd service-auth
mvn spring-boot:run

cd service-usuario
mvn spring-boot:run

# ...repetir con cada carpeta service-*...

# 3. Levantar el API Gateway al final, una vez los microservicios estén arriba
cd api-gateway
mvn spring-boot:run
```

> Cada microservicio puede compilarse también con `mvn clean package` para generar su `.jar` ejecutable (`java -jar target/nombre-del-servicio.jar`).

---

## Autenticación y Seguridad

El acceso a la API está protegido mediante **JSON Web Tokens (JWT)**. Todas las rutas pasan por el API Gateway (`http://localhost:8181`), el cual exige un token válido en el header `Authorization` para cualquier ruta que **no** sea `/auth/**`.

### Flujo de autenticación

1. **Registrar un usuario** (no requiere token):
   ```
   POST http://localhost:8181/auth/registrar
   ```
   ```json
   {
     "nombreUsuario": "carlos_gtr",
     "contrasena": "claveSegura123",
     "correo": "carlos@instrumentum.cl"
   }
   ```

2. **Iniciar sesión** (no requiere token) y obtener el JWT:
   ```
   POST http://localhost:8181/auth/login
   ```
   ```json
   {
     "nombreUsuario": "carlos_gtr",
     "contrasena": "claveSegura123"
   }
   ```
   La respuesta es el token JWT en texto plano (válido por **2 horas**).

3. **Usar el token** en cada petición a los demás microservicios, agregando el header:
   ```
   Authorization: Bearer <token_recibido_en_el_login>
   ```
   Si el header está ausente, mal formado, o el token es inválido/expiró, el Gateway responde `401 Unauthorized` antes de reenviar la petición al microservicio correspondiente.

---

## API Endpoints

**Base URL (única entrada al sistema):** `http://localhost:8181`

Todas las rutas de negocio usan el prefijo `/api/v2/...` y requieren el header `Authorization: Bearer <token>` (ver sección anterior). El único microservicio público es `service-auth`, expuesto bajo `/auth/**`.

### Autenticación (`service-auth` — público)

| Método | Endpoint | Descripción | Cuerpo (ejemplo) |
|--------|----------|-------------|------------------|
| POST | `/auth/registrar` | Registra un nuevo usuario | `{"nombreUsuario":"carlos_gtr","contrasena":"claveSegura123","correo":"carlos@instrumentum.cl"}` |
| POST | `/auth/login` | Autentica y devuelve un token JWT | `{"nombreUsuario":"carlos_gtr","contrasena":"claveSegura123"}` |

### Bandas (`service-usuario`)

| Método | Endpoint | Descripción | Cuerpo (ejemplo) |
|--------|----------|-------------|------------------|
| GET | `/api/v2/bandas` | Lista todas las bandas | – |
| GET | `/api/v2/bandas/{id}` | Obtener banda por ID | – |
| POST | `/api/v2/bandas` | Crear nueva banda | `{"nombre":"Los Solos","fechaRegistro":"2026-01-01"}` |
| PUT | `/api/v2/bandas/{id}` | Actualizar banda | `{"nombre":"Los Solos Reunion","fechaRegistro":"2026-06-01"}` |
| DELETE | `/api/v2/bandas/{id}` | Eliminar banda | – |

### Usuarios (`service-usuario`)

| Método | Endpoint | Descripción | Cuerpo (ejemplo) |
|--------|----------|-------------|------------------|
| GET | `/api/v2/usuarios` | Listar todos los usuarios | – |
| GET | `/api/v2/usuarios/{id}` | Obtener usuario por ID | – |
| GET | `/api/v2/usuarios/banda/{idBanda}` | Usuarios pertenecientes a una banda | – |
| POST | `/api/v2/usuarios` | Crear usuario (rol: `Musico` o `Tech`) | `{"username":"carlos_gtr","email":"carlos@mail.com","rol":"Musico","banda":{"idBanda":1}}` |
| PUT | `/api/v2/usuarios/{id}` | Actualizar usuario | `{"username":"carlos_v2","email":"carlos_v2@mail.com","rol":"Tech","banda":{"idBanda":1}}` |
| DELETE | `/api/v2/usuarios/{id}` | Eliminar usuario | – |

### Marcas y Categorías (`service-inventario`)

| Método | Endpoint | Descripción | Cuerpo (ejemplo) |
|--------|----------|-------------|------------------|
| GET | `/api/v2/marcas` | Lista marcas | – |
| GET | `/api/v2/marcas/{id}` | Obtener marca por ID | – |
| POST | `/api/v2/marcas` | Crear marca | `{"nombre":"PRS"}` |
| PUT | `/api/v2/marcas/{id}` | Actualizar marca | `{"nombre":"PRS Guitars"}` |
| GET | `/api/v2/categorias` | Lista categorías | – |
| GET | `/api/v2/categorias/{id}` | Obtener categoría por ID | – |
| POST | `/api/v2/categorias` | Crear categoría | `{"nombre":"Bajo"}` |
| PUT | `/api/v2/categorias/{id}` | Actualizar categoría | `{"nombre":"Bajo Eléctrico"}` |

### Equipos / Inventario (`service-inventario`)

| Método | Endpoint | Descripción | Cuerpo (ejemplo) |
|--------|----------|-------------|------------------|
| GET | `/api/v2/equipos` | Lista todos los equipos | – |
| GET | `/api/v2/equipos/propietario/{propietarioId}` | Equipos de un usuario o banda | – |
| GET | `/api/v2/equipos/buscar?nombre=&marca=&categoria=` | Búsqueda combinada por *query params* (todos opcionales) | – |
| GET | `/api/v2/equipos/{id}` | Obtener equipo por ID | – |
| POST | `/api/v2/equipos` | Crear equipo. `tipoEquipo`: `INSTRUMENTO`/`ELECTRONICO`. `tipoPropietario`: `USUARIO`/`BANDA` | `{"nombre":"Telecaster","modelo":"Player Series","marca":{"id":1},"categoria":{"id":1},"propietarioId":1,"tipoPropietario":"USUARIO","tipoEquipo":"INSTRUMENTO"}` |
| PUT | `/api/v2/equipos/{id}` | Actualizar equipo | Mismo cuerpo que POST |
| DELETE | `/api/v2/equipos/{id}` | Eliminar equipo y limpiar referencias en Specs/Mantenimiento/Logística | – |
| GET | `/api/v2/equipos/en-cancion/{equipoId}` | Verifica si el equipo está asignado a alguna canción (`true`/`false`) | – |

### Especificaciones Técnicas (`service-specs`)

| Método | Endpoint | Descripción | Cuerpo (ejemplo) |
|--------|----------|-------------|------------------|
| GET | `/api/v2/especs/instrumento/{equipoId}` | Obtener especificación de instrumento | – |
| POST | `/api/v2/especs/instrumento/{equipoId}` | Agregar especificaciones de instrumento | `{"tipoMadera":"Fresno","configPastillas":"SS","calibreCuerdas":"009"}` |
| PUT | `/api/v2/especs/instrumento/{equipoId}` | Actualizar especificaciones de instrumento | `{"tipoMadera":"Aliso","configPastillas":"SSS","calibreCuerdas":"010"}` |
| GET | `/api/v2/especs/electronica/{equipoId}` | Obtener especificación electrónica | – |
| POST | `/api/v2/especs/electronica/{equipoId}` | Agregar especificaciones electrónicas | `{"voltaje":"9V","consumo":20.0,"tipoCircuito":"Overdrive"}` |
| PUT | `/api/v2/especs/electronica/{equipoId}` | Actualizar especificaciones electrónicas | `{"voltaje":"18V","consumo":25.0,"tipoCircuito":"Boost"}` |
| DELETE | `/api/v2/especs/equipo/{equipoId}` | Eliminar la especificación de un equipo | – |

### Mantenimiento (`service-mantenimiento`)

| Método | Endpoint | Descripción | Cuerpo (ejemplo) |
|--------|----------|-------------|------------------|
| GET | `/api/v2/mantenimientos/{id}` | Obtener mantenimiento por ID | – |
| GET | `/api/v2/mantenimientos/equipo/{equipoId}` | Historial de mantenimientos de un equipo | – |
| GET | `/api/v2/mantenimientos/equipo/{equipoId}/requiere` | Retorna `true`/`false` si necesita mantenimiento (>6 meses sin servicio) | – |
| POST | `/api/v2/mantenimientos` | Registrar nuevo mantenimiento | `{"equipoId":1,"fecha":"2026-05-01","description":"Ajuste y limpieza","costo":35.0}` |
| PUT | `/api/v2/mantenimientos/{id}` | Actualizar un mantenimiento existente | `{"equipoId":1,"fecha":"2026-05-15","description":"Nueva descripción","costo":55.0}` |
| DELETE | `/api/v2/mantenimientos/{id}` | Eliminar un mantenimiento puntual | – |
| DELETE | `/api/v2/mantenimientos/equipo/{equipoId}` | Eliminar todos los mantenimientos de un equipo | – |

### Canciones y Rig Builder (`service-rig`)

| Método | Endpoint | Descripción | Cuerpo (ejemplo) |
|--------|----------|-------------|------------------|
| GET | `/api/v2/canciones/banda/{bandaId}` | Canciones de una banda | – |
| GET | `/api/v2/canciones/{id}` | Obtener canción por ID | – |
| POST | `/api/v2/canciones` | Crear canción | `{"nombre":"Solo","bandaId":1,"duracionSegundos":310}` |
| PUT | `/api/v2/canciones/{id}` | Actualizar canción | `{"nombre":"Nuevo nombre","bandaId":1,"duracionSegundos":340}` |
| DELETE | `/api/v2/canciones/{id}` | Eliminar canción (borra en cascada las asignaciones de equipos) | – |
| POST | `/api/v2/canciones/{cancionId}/equipos` | Asignar equipo a una canción con posición y seteo | `{"equipoId":1,"posicion":1,"seteoPerillas":"Volumen 7, Gain 5"}` |
| PUT | `/api/v2/canciones/{cancionId}/equipos/{equipoId}` | Actualizar posición/seteo de un equipo en una canción | `{"posicion":3,"seteoPerillas":"Volumen 8, Treble 7"}` |
| DELETE | `/api/v2/canciones/{cancionId}/equipos/{equipoId}` | Remover equipo de una canción | – |
| GET | `/api/v2/canciones/{cancionId}/setup-completo` | Obtener el setup completo de la canción (equipos con orden y seteo) | – |

### Eventos *(nuevo, `service-evento`)*

| Método | Endpoint | Descripción | Cuerpo (ejemplo) |
|--------|----------|-------------|------------------|
| GET | `/api/v2/eventos` | Listar todos los eventos | – |
| GET | `/api/v2/eventos/{id}` | Buscar evento por ID | – |
| GET | `/api/v2/eventos/banda/{idBanda}` | Eventos de una banda específica | – |
| POST | `/api/v2/eventos` | Crear evento (valida que la banda exista) | `{"idBanda":1,"nombre":"Toca en el Parque","fecha":"2026-08-15","canciones":"1,2,3"}` |
| PUT | `/api/v2/eventos/{id}` | Actualizar evento | `{"idBanda":1,"nombre":"Toca en el Parque (reprogramado)","fecha":"2026-08-22","canciones":"1,2,3"}` |
| DELETE | `/api/v2/eventos/{id}` | Eliminar evento | – |

### Giras y Paradas *(nuevo, `service-gira`)*

| Método | Endpoint | Descripción | Cuerpo (ejemplo) |
|--------|----------|-------------|------------------|
| GET | `/api/v2/giras` | Listar todas las giras | – |
| GET | `/api/v2/giras/{id}` | Buscar gira por ID | – |
| GET | `/api/v2/giras/banda/{idBanda}` | Giras de una banda | – |
| POST | `/api/v2/giras` | Crear gira (valida que la banda exista) | `{"idBanda":1,"nombreGira":"Gira Sudamérica 2026","fechaInicio":"2026-09-01","fechaFin":"2026-10-15"}` |
| PUT | `/api/v2/giras/{id}` | Actualizar gira | `{"idBanda":1,"nombreGira":"Gira Sudamérica 2026 v2","fechaInicio":"2026-09-05","fechaFin":"2026-10-20"}` |
| DELETE | `/api/v2/giras/{id}` | Eliminar gira y sus paradas en cascada | – |
| GET | `/api/v2/paradas/gira/{idGira}` | Listar paradas de una gira | – |
| GET | `/api/v2/paradas/{id}` | Buscar parada por ID | – |
| POST | `/api/v2/paradas` | Crear parada en el itinerario | `{"gira":{"idGira":1},"idEvento":5,"ciudad":"Santiago","alojamiento":"Hotel Centro","transporte":"Bus"}` |
| PUT | `/api/v2/paradas/{id}` | Actualizar parada | `{"gira":{"idGira":1},"idEvento":5,"ciudad":"Valparaíso","alojamiento":"Hostal Cerro","transporte":"Van"}` |
| DELETE | `/api/v2/paradas/{id}` | Eliminar una parada puntual | – |

### Logística / Contenedores *(nuevo, `service-logistica`)*

| Método | Endpoint | Descripción | Cuerpo (ejemplo) |
|--------|----------|-------------|------------------|
| GET | `/api/v2/logistica/todos` | Listar todos los contenedores | – |
| GET | `/api/v2/logistica/{id}` | Obtener contenedor por ID | – |
| GET | `/api/v2/logistica/banda/{idBanda}` | Contenedores de una banda | – |
| POST | `/api/v2/logistica` | Crear contenedor | `{"idBanda":1,"nombreCaja":"Rack de pedales","peso":12.5}` |
| PUT | `/api/v2/logistica/{id}` | Actualizar contenedor | `{"nombreCaja":"Rack de pedales v2","peso":13.0}` |
| DELETE | `/api/v2/logistica/{id}` | Eliminar contenedor y desasociar sus equipos | – |
| POST | `/api/v2/logistica/{id}/equipos` | Agregar equipo a un contenedor (valida contra Inventario) | `{"idEquipo":3}` |
| GET | `/api/v2/logistica/{id}/equipos` | Listar equipos de un contenedor | – |
| GET | `/api/v2/logistica/todosEquipos` | Listar todas las relaciones equipo-contenedor | – |
| DELETE | `/api/v2/logistica/{id}/equipos/{idEquipo}` | Remover un equipo de un contenedor | – |
| DELETE | `/api/v2/logistica/equipos/{idEquipo}` | Remover un equipo de todos los contenedores (uso en cascada) | – |

### Finanzas *(nuevo, `service-finanza`)*

| Método | Endpoint | Descripción | Cuerpo (ejemplo) |
|--------|----------|-------------|------------------|
| GET | `/api/v2/finanzas` | Listar todas las transacciones | – |
| GET | `/api/v2/finanzas/{id}` | Buscar transacción por ID | – |
| GET | `/api/v2/finanzas/banda/{idBanda}` | Transacciones de una banda | – |
| POST | `/api/v2/finanzas` | Registrar transacción (valida que la banda exista) | `{"idBanda":1,"tipoMovimiento":"Ingreso","monto":250000.0,"fecha":"2026-06-01","descripcion":"Pago por show en bar X"}` |
| PUT | `/api/v2/finanzas/{id}` | Actualizar transacción | `{"idBanda":1,"tipoMovimiento":"Egreso","monto":80000.0,"fecha":"2026-06-05","descripcion":"Arriendo de equipo"}` |
| DELETE | `/api/v2/finanzas/{id}` | Eliminar transacción | – |

### Merchandising *(nuevo, `service-merchandising`)*

| Método | Endpoint | Descripción | Cuerpo (ejemplo) |
|--------|----------|-------------|------------------|
| GET | `/api/v2/merchandising/productos/todos` | Listar todos los productos | – |
| GET | `/api/v2/merchandising/productos/{id}` | Obtener producto por ID | – |
| GET | `/api/v2/merchandising/productos/banda/{idBanda}` | Productos de una banda | – |
| POST | `/api/v2/merchandising/productos` | Crear producto (`tipo`: Polera/Disco/Parche/Gorro) | `{"idBanda":1,"nombre":"Polera Tour 2026","tipo":"Polera","precio":15000.0,"stock":50}` |
| PUT | `/api/v2/merchandising/productos/{id}` | Actualizar producto | `{"nombre":"Polera Tour 2026 Edición Limitada","tipo":"Polera","precio":18000.0,"stock":30}` |
| DELETE | `/api/v2/merchandising/productos/{id}` | Eliminar producto (solo si no tiene ventas) | – |
| POST | `/api/v2/merchandising/ventas` | Registrar venta (descuenta stock automáticamente) | `{"producto":{"idProducto":1},"cantidad":2,"idEventoOrigen":5}` |
| GET | `/api/v2/merchandising/ventas/todas` | Listar todas las ventas | – |
| GET | `/api/v2/merchandising/ventas/{id}` | Obtener venta por ID | – |
| GET | `/api/v2/merchandising/ventas/producto/{idProducto}` | Ventas de un producto específico | – |
| GET | `/api/v2/merchandising/ventas/evento/{idEventoOrigen}` | Ventas realizadas en un evento específico | – |
| PUT | `/api/v2/merchandising/ventas/{id}` | Actualizar el evento de origen de una venta | `{"idEventoOrigen":6}` |
| DELETE | `/api/v2/merchandising/ventas/{id}` | Eliminar venta y reintegrar el stock | – |

### Ejemplo completo de ejecución (REST)

```bash
# 1. Obtener token
curl -X POST http://localhost:8181/auth/login \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuario":"carlos_gtr","contrasena":"claveSegura123"}'

# 2. Usar el token para consultar el inventario de una banda
curl -X GET http://localhost:8181/api/v2/equipos/propietario/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## Documentación Interactiva (Swagger)

Cada microservicio expone su propio contrato OpenAPI (`springdoc-openapi`) y un Swagger UI local. El **API Gateway agrega todos esos contratos en una sola interfaz**, de modo que no es necesario visitar cada microservicio por separado durante el desarrollo.

### Ruta principal (recomendada)

```
http://localhost:8181/swagger-ui.html
```

Desde el selector superior de la interfaz se puede cambiar entre los documentos agregados de cada microservicio (Usuarios, Inventario, Specs, Mantenimiento, Rig, Eventos, Finanza, Gira, Logística, Merchandising).

### Rutas individuales por microservicio (uso en desarrollo)

Si se necesita inspeccionar un microservicio de forma aislada (sin pasar por el Gateway), cada uno expone su propio Swagger en su puerto local, por ejemplo:

| Microservicio | Swagger UI local | Documento OpenAPI (vía Gateway) |
|---|---|---|
| service-usuario | `http://localhost:8081/swagger-ui.html` | `http://localhost:8181/v2/api-docs/usuario` |
| service-inventario | `http://localhost:8082/swagger-ui.html` | `http://localhost:8181/v2/api-docs/inventario` |
| service-specs | `http://localhost:8083/swagger-ui.html` | `http://localhost:8181/v2/api-docs/specs` |
| service-mantenimiento | `http://localhost:8084/swagger-ui.html` | `http://localhost:8181/v2/api-docs/mantenimiento` |
| service-rig | `http://localhost:8085/swagger-ui.html` | `http://localhost:8181/v2/api-docs/rig` |
| service-evento | `http://localhost:8086/swagger-ui.html` | `http://localhost:8181/v2/api-docs/eventos` |
| service-finanza | `http://localhost:8087/swagger-ui.html` | `http://localhost:8181/v2/api-docs/finanza` |
| service-gira | `http://localhost:8088/swagger-ui.html` | `http://localhost:8181/v2/api-docs/gira` |
| service-logistica | `http://localhost:8090/swagger-ui.html` | `http://localhost:8181/v2/api-docs/logistica` |
| service-merchandising | `http://localhost:8091/swagger-ui.html` | `http://localhost:8181/v2/api-docs/merchandising` |

> El endpoint `/auth/login` debe ejecutarse primero (fuera de Swagger o desde Postman) para obtener el token, ya que la UI de Swagger no inyecta automáticamente el header `Authorization` salvo que se configure manualmente con el botón **Authorize**.

---

## Modelo Entidad-Relación

El siguiente diagrama representa el modelo de datos distribuido entre los distintos microservicios, normalizado en Tercera Forma Normal (3FN):

![Modelo Entidad-Relación — Instrumentum](images/MER_Instrumentum_3FN_v3.png)

### Descripción de Entidades Principales

**Núcleo (service-usuario / service-inventario / service-specs / service-mantenimiento / service-rig):**
- **Banda**: Agrupación musical (`id_banda`, `nombre`, `fecha_registro`)
- **Usuario**: Músico o técnico vinculado a una banda (`username`, `email`, `rol`)
- **Equipo**: Activo del inventario vinculado a un propietario (Usuario o Banda), categoría y marca
- **Categoria**: Clasificación del equipo (bajo, guitarra, pedal, etc.)
- **Marca**: Fabricante del equipo (Fender, Boss, Vox, etc.)
- **Especificacion_instrumento**: Atributos de instrumentos: `tipo_madera`, `config_pastillas`, `calibre_cuerdas`
- **Especificacion_electronica**: Atributos eléctricos: `voltaje`, `consumo` (mA), `tipo_circuito`
- **Mantenimiento**: Bitácora de servicios técnicos: `fec_servicio`, `descripcion`, `costo`
- **Cancion**: Tema del repertorio con `titulo`, `bpm`, `tonalidad`, `duracion`
- **Equipo_cancion**: Tabla pivote N:M que define `posicion_cadena` y `seteo` de cada equipo en una canción

**Giras, eventos y logística (service-evento / service-gira / service-logistica):**
- **Evento**: Show o concierto puntual de una banda, con su repertorio (`canciones`)
- **Gira**: Itinerario general de una banda (`nombre_gira`, `fecha_inicio`, `fecha_fin`)
- **Parada_gira**: Cada ciudad/fecha dentro de una gira, con `alojamiento` y `transporte`
- **Contenedor**: Caja o flightcase de transporte de una banda (`nombre_caja`, `peso`)
- **Contenedor_equipo**: Tabla pivote N:M entre Contenedor y Equipo

**Finanzas y merchandising (service-finanza / service-merchandising):**
- **Transaccion**: Movimiento financiero de una banda (`tipo_movimiento`, `monto`, `fecha`, `descripcion`)
- **Producto_merch**: Producto comercial de la banda (`tipo`, `precio`, `stock`)
- **Venta_merch**: Venta de un producto, con `cantidad`, `monto_total` y `id_evento_origen` opcional

---

## Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.5.15 |
| Enrutamiento / Gateway | Spring Cloud Gateway |
| Persistencia | Spring Data JPA |
| Base de Datos | MySQL (XAMPP), una instancia/esquema por microservicio |
| Comunicación entre servicios | Spring WebFlux (`WebClient`) |
| Seguridad | Spring Security + JJWT (JSON Web Tokens) |
| Documentación de API | springdoc-openapi (Swagger UI agregado en el Gateway) |
| Validación | Bean Validation (Jakarta) |
| Utilidades | Lombok |
| Testing de API | Postman |
| Build | Apache Maven |
| IDE | Visual Studio Code |

---

## Gestión de Riesgos

### Riesgos Críticos (Impacto Alto)

**R01 — Incompatibilidad de Versión (Spring Boot)**
- **Categoría:** Técnico
- **Probabilidad:** Media | **Impacto:** Alto
- **Mitigación:** Realizar una prueba de concepto (PoC) para validar que las dependencias de Jakarta Persistence y el driver de MySQL operen sin conflictos en la versión de Spring utilizada.

**R02 — Desconexión del Motor de Base de Datos (MySQL/XAMPP)**
- **Categoría:** Infraestructura Local
- **Probabilidad:** Media | **Impacto:** Alto
- **Mitigación:** Configurar un `HealthCheck` en cada microservicio para monitorear el estado de su conexión y asegurar que el servicio MySQL en XAMPP esté activo antes del despliegue.

**R03 — Brecha de Seguridad en Aislamiento de Datos**
- **Categoría:** Seguridad
- **Probabilidad:** Baja | **Impacto:** Crítico
- **Mitigación:** Implementar cláusulas `WHERE user_id = :current_user` en todos los métodos del repositorio JPA para evitar que un usuario acceda al inventario de otro.

**R06 — Caída del API Gateway (Punto Único de Entrada)**
- **Categoría:** Arquitectura Distribuida
- **Probabilidad:** Baja | **Impacto:** Crítico
- **Mitigación:** Como toda petición externa pasa por el Gateway, su caída deja inaccesible al sistema completo aunque los microservicios sigan operativos. Se recomienda monitoreo activo y, a futuro, una segunda instancia con balanceo de carga.

### Riesgos Moderados (Impacto Medio)

**R04 — Error en Algoritmo de Cálculo de Carga (RF-05)**
- **Categoría:** Funcional
- **Probabilidad:** Baja | **Impacto:** Medio
- **Mitigación:** Desarrollar una suite de pruebas unitarias con JUnit 5 que cubra casos borde (pedales sin amperaje definido, sumas que exceden el límite de la fuente).

**R05 — Inconsistencia por Eliminación en Cascada**
- **Categoría:** Integridad de Datos
- **Probabilidad:** Baja | **Impacto:** Alto
- **Mitigación:** Aplicar `@OnDelete(action = OnDeleteAction.CASCADE)` únicamente en los logs de mantenimiento, protegiendo con restricciones de clave foránea los equipos vinculados a canciones activas.

**R08 — Inconsistencia entre Microservicios por Llamadas Síncronas**
- **Categoría:** Arquitectura Distribuida
- **Probabilidad:** Media | **Impacto:** Medio
- **Mitigación:** Si un microservicio dependiente (ej. `service-usuario`) está caído, las validaciones vía `WebClient` desde otros servicios (ej. `service-evento`) fallarán. Se mitiga capturando la excepción y devolviendo un mensaje claro (`503`/`500` controlado) en lugar de un error genérico.

### Riesgos Operacionales (Impacto Bajo)

**R07 — Fallas en Generación de Technical Rider**
- **Categoría:** Funcional
- **Probabilidad:** Media | **Impacto:** Medio
- **Mitigación:** Validar los campos de texto antes de la exportación para evitar caracteres especiales que corrompan el formato del archivo final (PDF/Texto).

**R09 — Expiración de Token Durante una Sesión Larga**
- **Categoría:** Seguridad / Usabilidad
- **Probabilidad:** Media | **Impacto:** Bajo
- **Mitigación:** El JWT expira a las 2 horas; se recomienda comunicar este límite en el cliente y/o implementar un endpoint de *refresh token* a futuro.

---

*Instrumentum — Gestión técnica de equipamiento musical*
