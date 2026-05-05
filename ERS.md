Instrumentum

El sistema Instrumentum opera como una plataforma de gestión integral basada en una arquitectura de capas, diseñada para centralizar la información técnica de músicos y optimizar su rendimiento en vivo y en estudio. Su comportamiento se desglosa en los siguientes núcleos operativos:
------------------------------------------------------------------
Descripción del Comportamiento del Sistema

Gestión de Datos y Persistencia
El sistema opera sobre una base de datos MySQL gestionada mediante XAMPP, donde se centraliza la información de músicos, bandas y equipamiento. La interacción con la lógica de negocio se realiza a través de peticiones HTTP probadas y documentadas en Postman, asegurando que el flujo de datos entre el cliente y el servidor sea íntegro y siga las reglas definidas en el modelo relacional.

Ciclo de Vida del Inventario (Gear Management)
El sistema presenta un comportamiento dinámico en la captura de datos procesados desde el backend:

Segmentación por Tipo: Si se registra un instrumento, el software procesa atributos de maderas y pastillas. Si se registra un equipo electrónico, se validan campos de voltaje y amperaje (mA).

Gestión de Estados: El sistema calcula automáticamente el tiempo transcurrido desde la última intervención técnica registrada en la tabla Mantenimiento. Si el intervalo supera los 6 meses, el estado del equipo se marca internamente para disparar una alerta de mantenimiento preventivo en las respuestas del sistema.

Dinámica del Módulo de Mantenimiento
Este módulo funciona como una bitácora histórica vinculada de forma inmutable a cada activo del inventario mediante el id_equipo.

Integridad de Datos: El sistema aplica reglas de borrado en cascada. Al eliminar un equipo de la base de datos, sus registros de servicio asociados desaparecen automáticamente, manteniendo la integridad referencial.

Inteligencia del Rig Builder (Configuración de Canciones)
El núcleo del sistema permite gestionar la relación compleja entre el repertorio y el equipamiento.

Gestión de Cadena de Señal: Mediante la tabla Equipo_cancion, se define un orden lógico de conexión (Posición 1, 2, 3...) para los equipos dentro de una canción específica.

Persistencia de Contexto: Por cada asignación, el sistema permite persistir notas sobre la técnica de ejecución y el seteo preciso de las perillas (seteo), funcionando como un manual técnico para el músico.

Lógica de Cálculo y Validación Eléctrica
El software integra un algoritmo de validación de carga para configuraciones de pedales y electrónica.

Agregación Automática: Al consultar el setup de una canción, el sistema suma los valores de miliamperios (mA) de todos los dispositivos activos vinculados.

Alertas Preventivas: El total se contrasta con la capacidad de la fuente de poder registrada; si el consumo supera el límite teórico, el sistema genera un mensaje de advertencia para prevenir fallos eléctricos.
--------------------------------------------------------------------
Requisitos Funcionales (RF)

- RF-01: Gestión de Inventario de Equipos: CRUD completo de equipos. Atributos: maderas/pastillas para instrumentos; voltaje, consumo (mA) y circuito para electrónica.
- RF-02: Módulo de Mantenimiento Preventivo: Registro de servicios asociados a equipos. Lógica de alerta para equipos con más de 6 meses sin servicio.
- RF-03: Configuración por Canción (Rig Builder): CRUD de canciones con asignación N:N de equipos, incluyendo orden de cadena de señal y seteo de perillas.
- RF-04: Gestión de Usuarios y Bandas: Registro y administración de los datos de usuarios y sus respectivas agrupaciones musicales.
- RF-05: Calculadora de Carga: Algoritmo para sumar consumo (mA) por canción y comparar contra el límite de la fuente de poder.
- RF-06: Búsqueda y Filtros: Búsqueda por metadatos (nombre, tipo, marca) a través de parámetros de consulta en los endpoints.
---------------------------------------------------------------
Requisitos No Funcionales (RNF)
- RNF-01: Tecnología: Desarrollo en Java 21 con Spring Boot 3.4.16. Persistencia mediante Spring Data JPA sobre MySQL (XAMPP). Entorno de desarrollo en VS Code.
- RNF-02: Interfaz de Pruebas: Validación de endpoints y lógica de negocio mediante Postman.
- RNF-03: Rendimiento: Operaciones CRUD con tiempo de respuesta inferior a 2 segundos.
- RNF-04: Seguridad de Datos: Protección contra inyección SQL nativa mediante el uso de JPA y validación de datos de entrada (Bean Validation).
- RNF-05: Mantenibilidad: Código organizado por capas (Controller, Service, Repository) y uso de Lombok para limpieza de código.
- RNF-06: Integridad de Datos: Configuración de eliminación en cascada para mantenimientos y restricciones para proteger equipos asignados a canciones activas.



