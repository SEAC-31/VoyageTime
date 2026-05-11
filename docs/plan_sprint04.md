## 1. Sprint Goal

Integrar Retrofit para conectar la app con la API REST de reservas de hotel, implementar las pantallas de búsqueda, reserva y gestión de reservas, y añadir una galería de imágenes por viaje con persistencia local.

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estimación (h) | Prioridad |
|----|-------|-------------|----------------|-----------|
| **T1. Configuración de Retrofit (3 pts)** | | | | |
| T1.1 | Añadir dependencia Retrofit y configurar el cliente HTTP (OkHttp, base URL, interceptores) | Joan | 1h | Alta |
| T1.2 | Crear modelos de datos (Hotel, Room, Reservation) e interfaces de la API con estructura MVVM | Joan | 2h | Alta |
| T1.3 | Crear la capa Repository para abstraer el uso de la API remota | Sharon | 2h | Alta |
| T1.4 | Escribir unit tests mockeando la conexión remota (MockWebServer o similar) | Sharon | 2h | Media |
| **T2. Pantallas de Búsqueda y Reserva (5 pts)** | | | | |
| T2.1 | Crear pantalla de búsqueda de hoteles (ciudad: London/Paris/Barcelona + fechas con DatePicker) | Sharon | 2h | Alta |
| T2.2 | Mostrar lista de hoteles y habitaciones devueltos por la API (típicamente 3 por hotel) | Sharon | 3h | Alta |
| T2.3 | Permitir reservar habitación y guardar info de reserva localmente en Room (nueva entrada en trip) | Joan | 3h | Alta |
| T2.4 | Mostrar imágenes del hotel y habitaciones en la pantalla de detalle/booking | Joan | 2h | Alta |
| **T3. Galería de Imágenes por Viaje (4 pts)** | | | | |
| T3.1 | Permitir al usuario adjuntar múltiples imágenes a un viaje (desde cámara o galería del dispositivo) | Sharon | 2h | Alta |
| T3.2 | Guardar imágenes localmente en el dispositivo (storage o BD) vinculadas al viaje | Sharon | 2h | Alta |
| T3.3 | Mostrar la galería específica de cada viaje en la pantalla de detalle del viaje | Joan | 2h | Alta |
| **T4. Listado y Cancelación de Reservas (3 pts)** | | | | |
| T4.1 | Crear pantalla que liste todas las reservas locales indicando el viaje relacionado | Joan | 2h | Alta |
| T4.2 | Añadir funcionalidad para eliminar reserva localmente y vía API si es necesario | Joan | 2h | Alta |
| T4.3 | Mostrar imágenes del hotel y habitación en el listado de reservas | Sharon | 2h | Media |
| T4.4 | Actualizar pantalla 'My Trips' para indicar si un viaje tiene reserva y mostrar sus detalles | Sharon | 2h | Media |

**Total estimado — Sharon: 17h  |  Joan: 14h**

---

## 3. Definition of Done (DoD)

- [ ] Retrofit está configurado y conecta correctamente con la API de hoteles.
- [ ] Existen modelos de datos, interfaces API y repositorio siguiendo MVVM.
- [ ] El usuario puede buscar hoteles por ciudad y fechas con DatePicker.
- [ ] El usuario puede ver el listado de hoteles y habitaciones con imágenes.
- [ ] El usuario puede reservar una habitación y la info se persiste localmente en Room.
- [ ] El usuario puede adjuntar imágenes a un viaje y verlas en una galería por viaje.
- [ ] Existe pantalla de listado de reservas con imágenes y opción de cancelar.
- [ ] La pantalla 'My Trips' indica si un viaje tiene reserva de hotel y muestra sus detalles.
- [ ] Hay unit tests que mockean la conexión remota.
- [ ] Se usa HILT como librería de inyección de dependencias.
- [ ] La estructura de carpetas incluye: view, viewmodel, repo, di, data.
- [ ] Todas las fechas se introducen con DatePicker.
- [ ] El release está etiquetado como v4.x.x en GitHub y el Sprint.md está actualizado.
- [ ] Se ha grabado un vídeo demostrativo y subido a `/docs` o `documentation/evidence/v4.x.x`

---

## 4. Riesgos identificados

- Inestabilidad o disponibilidad del servidor de la API (http://15.224.84.148:8090): tener mockups preparados para poder trabajar offline.
- Gestión de errores de red (timeouts, sin conexión) que complica el flujo de búsqueda y reserva.
- Carga y caché de imágenes remotas puede ser lenta o consumir mucho espacio si no se gestiona bien (Glide/Picasso recomendado).
- Sincronización entre la reserva remota y la persistencia local en Room puede generar inconsistencias.
- Permisos de acceso a cámara/galería del dispositivo pueden variar según versión de Android.
- Riesgo de bloqueo: T1.2 (Joan) debe estar definido antes de que Sharon pueda implementar T1.3 y T2.2. Coordinarse al inicio del sprint.
