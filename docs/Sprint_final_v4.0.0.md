# Sprint 04 — Travel Planner Remote Persistence

**Project:** VoyageTime  
**Course:** 105025-2526 — Applications for Mobile Devices  
**Release:** `v4.0.0`  
**Team:** Sharon / Joan  
**API base URL:** `http://15.224.84.148:8090/`

---

## 1. Sprint Goal

Integrar Retrofit en VoyageTime para conectar la aplicación Android con la API REST de reservas de hotel, implementar el flujo completo de búsqueda, detalle, reserva, listado y cancelación de reservas, y añadir una galería de imágenes por viaje con persistencia local mediante Room y almacenamiento del dispositivo.

El objetivo funcional del sprint es que el usuario pueda buscar hoteles disponibles en London, Paris o Barcelona usando fechas seleccionadas con DatePicker, consultar hoteles y habitaciones con sus imágenes, reservar una habitación, guardar la reserva localmente como parte de un viaje, consultar/cancelar reservas y gestionar imágenes propias asociadas a cada viaje.

---

## 2. Sprint Backlog y responsabilidades

| ID | Tarea | Responsable | Estimación | Prioridad | Estado |
|----|-------|-------------|------------|-----------|--------|
| **T1. Configuración de Retrofit (3 pts)** | | | | | |
| T1.1 | Añadir dependencia Retrofit y configurar el cliente HTTP con OkHttp, Gson, logging interceptor y base URL de la API. | Joan | 1h | Alta | Completado |
| T1.2 | Crear modelos de datos para hoteles, habitaciones y reservas, junto con la interfaz de API siguiendo MVVM. | Joan | 2h | Alta | Completado |
| T1.3 | Crear capa Repository para abstraer el uso de la API remota y evitar llamadas directas desde la UI. | Sharon | 2h | Alta | Completado |
| T1.4 | Crear unit test mockeando la conexión remota con MockWebServer. | Sharon | 2h | Media | Completado |
| **T2. Pantallas de búsqueda y reserva (5 pts)** | | | | | |
| T2.1 | Crear pantalla de búsqueda de hoteles con ciudad London/Paris/Barcelona y fechas con DatePicker. | Sharon | 2h | Alta | Completado |
| T2.2 | Mostrar listado de hoteles y habitaciones devueltos por la API, incluyendo datos principales e imágenes. | Sharon | 3h | Alta | Completado |
| T2.3 | Permitir reservar una habitación y guardar la información localmente en Room creando un nuevo viaje asociado. | Joan | 3h | Alta | Completado |
| T2.4 | Mostrar imágenes del hotel y de las habitaciones en la pantalla de detalle/booking. | Joan | 2h | Alta | Completado |
| **T3. Galería de imágenes por viaje (4 pts)** | | | | | |
| T3.1 | Permitir adjuntar múltiples imágenes a un viaje desde la galería del dispositivo. | Sharon | 2h | Alta | Completado |
| T3.2 | Guardar las imágenes localmente en el dispositivo y vincularlas al viaje en Room. | Sharon | 2h | Alta | Completado |
| T3.3 | Mostrar una galería específica por viaje en la pantalla de galería del viaje. | Joan | 2h | Alta | Completado |
| **T4. Listado y cancelación de reservas (3 pts)** | | | | | |
| T4.1 | Crear pantalla que liste todas las reservas locales indicando el viaje relacionado. | Joan | 2h | Alta | Completado |
| T4.2 | Añadir funcionalidad para eliminar/cancelar una reserva localmente y mediante API cuando sea posible. | Joan | 2h | Alta | Completado |
| T4.3 | Mostrar imágenes de hotel/habitación en el listado de reservas. | Sharon | 2h | Media | Completado |
| T4.4 | Actualizar la pantalla My Trips para indicar si un viaje tiene reserva de hotel y mostrar sus detalles. | Sharon | 2h | Media | Completado |

**Total estimado:** Sharon — 17h / Joan — 14h

---

## 3. Implementación realizada

### 3.1 Retrofit y API remota

Se ha configurado Retrofit con OkHttp y Gson para consumir la API de hoteles del profesor. La configuración se encuentra en la capa de inyección de dependencias y la interfaz remota define los endpoints necesarios para disponibilidad, reserva y cancelación.

Ficheros principales:

- `app/src/main/java/com/example/voyagetime/di/NetworkModule.kt`
- `app/src/main/java/com/example/voyagetime/data/remote/HotelApiService.kt`
- `app/src/main/java/com/example/voyagetime/data/remote/HotelModels.kt`

### 3.2 Repository y arquitectura MVVM

La comunicación con la API está encapsulada en un Repository. Los ViewModels consumen el Repository y no llaman directamente a Retrofit. La aplicación mantiene la arquitectura View/ViewModel/Repository/DB.

Ficheros principales:

- `app/src/main/java/com/example/voyagetime/domain/repository/HotelRepository.kt`
- `app/src/main/java/com/example/voyagetime/data/repository/HotelRepositoryImpl.kt`
- `app/src/main/java/com/example/voyagetime/di/RepositoryModule.kt`
- `app/src/main/java/com/example/voyagetime/ui/viewmodels/HotelBookingViewModel.kt`
- `app/src/main/java/com/example/voyagetime/ui/viewmodels/ReservationsViewModel.kt`

### 3.3 Búsqueda y reserva de hoteles

Se ha añadido una pantalla de búsqueda de hoteles con selección de ciudad y fechas mediante DatePicker. Desde el listado se puede abrir el detalle de un hotel, ver habitaciones e imágenes, y realizar una reserva.

Al reservar, la app crea un viaje local asociado a la reserva y guarda la información de la reserva en Room.

Ficheros principales:

- `app/src/main/java/com/example/voyagetime/ui/screens/HotelSearchScreen.kt`
- `app/src/main/java/com/example/voyagetime/ui/screens/HotelDetailScreen.kt`
- `app/src/main/java/com/example/voyagetime/ui/screens/ReservationsScreen.kt`
- `app/src/main/java/com/example/voyagetime/MainActivity.kt`

### 3.4 Persistencia local con Room

La app usa Room para persistir viajes, reservas, usuarios, elementos de itinerario e imágenes asociadas a viajes. La base de datos usa `fallbackToDestructiveMigration(true)` para evitar errores de migraciones durante el desarrollo del sprint cuando el esquema instalado en el emulador no coincide con el esquema actual.

Ficheros principales:

- `app/src/main/java/com/example/voyagetime/data/local/database/VoyageTimeDatabase.kt`
- `app/src/main/java/com/example/voyagetime/data/local/entity/TripEntity.kt`
- `app/src/main/java/com/example/voyagetime/data/local/entity/ReservationEntity.kt`
- `app/src/main/java/com/example/voyagetime/data/local/entity/TripImageEntity.kt`
- `app/src/main/java/com/example/voyagetime/data/local/dao/TripDao.kt`
- `app/src/main/java/com/example/voyagetime/data/local/dao/ReservationDao.kt`
- `app/src/main/java/com/example/voyagetime/data/local/dao/TripImageDao.kt`

### 3.5 Galería de imágenes por viaje

Se ha implementado una galería específica por viaje. Las imágenes seleccionadas se copian localmente en una carpeta asociada al viaje dentro de `Pictures/VoyageTime/`, y la URI de la copia se guarda en Room vinculada al `tripId`.

También se permite cambiar la imagen principal o de portada de la tarjeta del viaje una vez creado.

Ficheros principales:

- `app/src/main/java/com/example/voyagetime/ui/screens/TripGallery.kt`
- `app/src/main/java/com/example/voyagetime/ui/viewmodels/TripGalleryViewModel.kt`
- `app/src/main/java/com/example/voyagetime/utils/TripImageStorage.kt`
- `app/src/main/java/com/example/voyagetime/ui/screens/Trips.kt`

### 3.6 Listado y cancelación de reservas

Se ha añadido una pantalla de reservas en la navegación principal. La pantalla muestra reservas locales, datos del hotel/habitación, fechas, precio, viaje asociado e imágenes. Permite cancelar/eliminar reservas localmente y llamar a la API cuando procede.

Ficheros principales:

- `app/src/main/java/com/example/voyagetime/ui/screens/ReservationsScreen.kt`
- `app/src/main/java/com/example/voyagetime/ui/viewmodels/ReservationsViewModel.kt`
- `app/src/main/java/com/example/voyagetime/data/repository/HotelRepositoryImpl.kt`

---

## 4. Definition of Done

| Criterio | Estado |
|----------|--------|
| Retrofit configurado y conectado con la API de hoteles. | Completado |
| Modelos de datos, interfaz API y Repository siguiendo MVVM. | Completado |
| Búsqueda de hoteles por ciudad y fechas con DatePicker. | Completado |
| Listado de hoteles y habitaciones con imágenes. | Completado |
| Reserva de habitación con persistencia local en Room. | Completado |
| Creación automática de viaje asociado a la reserva. | Completado |
| Galería de imágenes por viaje con imágenes locales vinculadas al viaje. | Completado |
| Listado de reservas con imágenes y opción de cancelar. | Completado |
| My Trips indica si el viaje tiene reserva y muestra detalles. | Completado |
| Cambio/edición de imagen principal de la tarjeta del viaje. | Completado |
| Unit test mockeando conexión remota. | Completado |
| Uso de Hilt como librería de inyección de dependencias. | Completado |
| Uso de Room como sistema de persistencia. | Completado |
| Estructura con carpetas `view`, `viewmodel`, `repo`, `di` y `data`. | Completado |
| Fechas y horas introducidas con pickers. | Completado |
| Release preparado como `v4.0.0`. | Completado |
| Vídeo demostrativo en `documentation/evidence/v4.0.0/`. | Entregado por campus, tamaño muy grande incluso comprimido |

---

## 5. Testing

Se incluye un test unitario para comprobar el consumo de la API remota mediante MockWebServer, sin depender del servidor real durante la prueba.

Fichero principal:

- `app/src/test/java/com/example/voyagetime/data/repository/HotelRepositoryImplTest.kt`

También se mantienen tests instrumentados de DAOs existentes para comprobar la persistencia local con Room:

- `app/src/androidTest/java/com/example/voyagetime/dao/Accesslogdaotest.kt`
- `app/src/androidTest/java/com/example/voyagetime/dao/Itineraryitemdaotest.kt`
- `app/src/androidTest/java/com/example/voyagetime/dao/Tripdaotest.kt`
- `app/src/androidTest/java/com/example/voyagetime/dao/Userdaotest.kt`

---

## 6. Estructura del proyecto

La estructura del proyecto mantiene las capas requeridas:

- `data`: entidades, DAOs, base de datos Room, modelos remotos y repositorios de datos.
- `di`: módulos Hilt para base de datos, red y repositorios.
- `repo`: carpeta de compatibilidad requerida por el enunciado.
- `view`: carpeta de compatibilidad requerida por el enunciado.
- `viewmodel`: carpeta de compatibilidad requerida por el enunciado.
- `ui/screens`: pantallas Compose.
- `ui/viewmodels`: ViewModels reales utilizados por las pantallas.
- `domain/repository`: contratos de repositorio.

---

## 7. Evidencia de entrega

La versión del proyecto está configurada como:

- `versionCode = 4`
- `versionName = "4.0.0"`

La carpeta preparada para el vídeo demostrativo es:

- `documentation/evidence/v4.0.0/`

Antes de entregar, el equipo debe guardar dentro de esa carpeta el vídeo demostrativo de la aplicación funcionando en emulador o dispositivo físico.

---

## 8. Riesgos y decisiones técnicas

| Riesgo | Decisión aplicada |
|--------|-------------------|
| El servidor remoto puede estar caído o lento. | Se añadió test con MockWebServer para validar el parsing de la API sin depender del servidor real. |
| Posibles inconsistencias entre reserva remota y persistencia local. | La reserva se guarda localmente en Room asociada a un viaje creado automáticamente. |
| Conflictos de migraciones durante el desarrollo. | Se usa `fallbackToDestructiveMigration(true)` para evitar bloqueos por esquemas antiguos en el emulador. |
| Imágenes externas pueden dejar de estar disponibles. | Las imágenes añadidas por el usuario se copian localmente al dispositivo. |
| La app puede ejecutarse sin usuario Firebase activo. | Se usa un usuario local de respaldo para persistencia local cuando no hay sesión activa. |
| La navegación hacia detalle de hotel podía perder el objeto seleccionado. | El detalle de hotel se pasa codificado por ruta para evitar pantalla negra al recomponer. |


