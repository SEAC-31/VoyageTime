# Sprint 03 – Planning Document

## 1. Sprint Goal

Integrar persistencia de datos con SQLite (Room), implementar autenticación con Firebase (login, registro y recuperación de contraseña), y asegurar que toda la información de viajes y usuarios se almacene de forma persistente y segura.

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estimación (h) | Prioridad |
|----|-------|-------------|----------------|-----------|
| T0.1 | Cambiar los inputs de las fechas para que se escogan por un datepicker. | Sharon | 2h | Alta |
| T0.2 | Implementar el multi-language en todas las pantallas | Sharon | 2h | Alta |
| T0.3 | Escribir Unit Tests para las operaciones CRUD de las pantallas de Trip e Itinerary | Sharon | 1h | Media |
| T0.4 | Simular interacciones de un usuario y registrar los errores o comportamientos inesperados | Joan | 2h | Media |
| T0.5 | Actualizar la documentación con los resultados de los tests y las soluciones implementadas | Joan | 2h | Media |
| T0.6 | Añadir logs(del logcat) y comentarios aplicando buenas practicas | Joan | 1h | Media |
| T1.1 | Crear la clase Room Database | Sharon | 1h | Media |
| T1.2 | Definir Entities para Trip e ItineraryItem (con campos datetime, text e integer) | Sharon | 2h | Alta |
| T1.3 | Crear DAOs para las operaciones de base de datos | Joan | 2h | Alta |
| T1.4 | Implementar operaciones CRUD mediante DAO para viajes e itinerarios | Joan | 3h | Alta |
| T1.5 | Modificar ViewModels para usar Room en lugar del almacenamiento en memoria | Joan | 3h | Alta |
| T1.6 | Asegurar que la UI se actualiza cuando cambia la base de datos | Sharon | 2h | Alta |
| T2.1 | Conectar la app a Firebase | Sharon | 1h | Alta |
| T2.2 | Diseñar la pantalla de Login (formulario) | Sharon | 2h | Alta |
| T2.3 | Implementar las acciones de login con Firebase (email y contraseña) | Sharon | 3h | Alta |
| T2.4 | Crear una acción en la app para cerrar sesión (logout) | Sharon | 1h | Alta |
| T2.5 | Usar Logcat para registrar todas las operaciones y errores de autenticación | Sharon | 1h | Media |
| T3.1 | Diseñar la pantalla de Registro (formulario) | Sharon | 2h | Alta |
| T3.2 | Implementar el registro con Firebase usando el patrón Repository e implementar verificación por email | Joan | 4h | Alta |
| T3.3 | Implementar la vista y lógica para recuperar contraseña | Joan | 2h | Media |
| T4.1 | Persistir información del usuario en la BD local (tabla usuario con login, username, birthdate, address, country, phone, accept emails) y validar username único | Joan | 3h | Alta |
| T4.2 | Modificar la tabla de viajes para soportar múltiples usuarios y filtrar por usuario logueado | Joan | 3h | Alta |
| T4.3 | Actualizar design.md con el esquema de la base de datos y su uso | Joan | 2h | Media |
| T4.4 | Persistir los accesos de usuario (tabla de log con userid y datetime de login/logout) | Joan | 2h | Media |
| T5.1 | Escribir tests unitarios para DAOs e interacciones con la base de datos | Joan | 4h | Media |
| T5.2 | Implementar validación de datos (evitar nombres de viaje duplicados, validar fechas) | Sharon | 2h | Media |
| T5.3 | Usar Logcat para rastrear operaciones y errores de base de datos | Sharon | 1h | Baja |
| T5.4 | Actualizar design.md con el esquema de la BD y su uso (testing) | Sharon | 1h | Baja |

---

## 3. Definition of Done (DoD)

- [ ] La app por completo implementa el multi-language.
- [ ] Todas las fechas se escogen con un datepicker.
- [ ] Existen tests unitarios para las operaciones CRUD de Itinerary y trips, se han verificado y documentado los resultados.
- [ ] La app utiliza Room Database para toda la persistencia de viajes e itinerarios (reemplazando el almacenamiento en memoria)
- [ ] El usuario puede registrarse, iniciar sesión y cerrar sesión mediante Firebase Authentication
- [ ] La recuperación de contraseña y la verificación de email están implementadas
- [ ] La información del usuario se persiste localmente y los viajes están vinculados al usuario logueado
- [ ] Los accesos (login/logout) quedan registrados en una tabla de log
- [ ] Existen tests unitarios para los DAOs y las operaciones principales
- [ ] El design.md está actualizado con el esquema de la base de datos
- [ ] Se ha grabado un vídeo demostrativo y subido a `/docs` o `documentation/evidence/v3.x.x`
- [ ] Se usa HILT como librería de inyección de dependencias y patrón Repository

---

## 4. Riesgos identificados

- Complejidad de integrar Room y Firebase simultáneamente
- Gestión de migraciones de base de datos si el esquema cambia
- Posibles problemas de sincronización entre la BD local y Firebase
- Poca experiencia previa con HILT como librería de inyección de dependencias
- Riesgo de bloqueo si la coordinación entre ViewModels y UI no se acuerda antes de empezar