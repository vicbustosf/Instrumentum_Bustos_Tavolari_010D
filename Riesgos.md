Riesgos Críticos (Impacto Alto)

R01 | Incompatibilidad de Versión (Spring Boot 4.0.5)
* Categoría: Técnico
* Probabilidad: Media
* Impacto: Alto
* Mitigación: Realizar una prueba de concepto inicial (PoC) para validar que las dependencias de **Jakarta Persistence** y el driver de **MySQL** operen sin conflictos en la nueva versión de Spring.

R02 | Desconexión del Motor de Base de Datos (MySQL/XAMPP)**
* Categoría: Infraestructura Local
* Probabilidad: Media
* Impacto: Alto
* Mitigación: Configurar un HealthCheck en Spring para monitorear el estado de la conexión y asegurar que el servicio de MySQL en el panel de XAMPP esté iniciado antes del despliegue del .jar.

R03 | Brecha de Seguridad en Aislamiento de Datos
* Categoría: Seguridad
* Probabilidad:** Baja
* Impacto:** Crítico
* Mitigación: Implementar cláusulas WHERE user_id = :current_user en todos los métodos del repositorio de Spring Data JPA para evitar que un usuario visualice el inventario de otro.

----------------------------------------------------------------------

Riesgos Moderados (Impacto Medio)

R04 | Error en Algoritmo de Cálculo de Carga (RF-05)**
* Categoría: Funcional
* Probabilidad: Baja
* Impacto: Medio
* Mitigación: Desarrollar una suite de pruebas unitarias con JUnit 5 (framework de pruebas) que testee casos de borde (ej: pedales sin amperaje definido o sumas que exceden el límite de la fuente).

R05 | Inconsistencia por Eliminación en Cascada (RNF-07)
* Categoría: Integridad de Datos
* Probabilidad: Baja
* Impacto: Alto
* Mitigación: Aplicar la anotación @OnDelete(action = OnDeleteAction.CASCADE) únicamente en los logs de mantenimiento, protegiendo con restricciones de clave foránea los equipos vinculados a canciones.

------------------------------------------------------------------------

Riesgos Operacionales (Impacto Bajo)

R07 | Fallas en Generación de Technical Rider (RF-04)
* Categoría: Funcional
* Probabilidad: Media
* Impacto: Medio
* Mitigación: Validar los campos de texto antes de la exportación para evitar caracteres especiales que corrompan el formato del archivo final (PDF/Texto).
