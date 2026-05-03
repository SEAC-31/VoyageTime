# 📐 Diseño Arquitectónico de VoyageTime

## 🏛️ Arquitectura General

VoyageTime sigue una arquitectura **single-module con separación por capas UI** basada en Jetpack Compose y Material Design 3, con navegación adaptativa mediante `NavigationSuiteScaffold`.

## 📊 Modelo de Datos: Diseñado y expandido para futuros Sprints

![Modelo de dominio](domain_model.png)

---

## 🗄️ Esquema de Base de Datos (Room v2)

La base de datos local se gestiona con **Room 2.7.0**. La versión 2 introduce soporte multi-usuario mediante las tablas `users` y `access_log`, y añade la FK `user_id` a `trips`.

### Tabla: `users`

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `firebase_uid` | TEXT | PK, UNIQUE | UID de Firebase Auth |
| `username` | TEXT | NOT NULL, UNIQUE | Nombre de usuario |
| `email` | TEXT | NOT NULL | Email del usuario |
| `birthdate` | TEXT | nullable | Fecha de nacimiento (LocalDate serializada) |
| `address` | TEXT | NOT NULL, default '' | Dirección |
| `country` | TEXT | NOT NULL, default '' | País |
| `phone` | TEXT | NOT NULL, default '' | Teléfono |
| `accept_emails` | INTEGER | NOT NULL, default 0 | Boolean (0/1) aceptación de emails |
| `created_at` | TEXT | NOT NULL | Timestamp de creación (LocalDateTime serializado) |

---

### Tabla: `trips`

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | INTEGER | PK, autoincrement | Identificador del viaje |
| `user_id` | TEXT | FK → `users.firebase_uid`, CASCADE DELETE, nullable | Usuario propietario |
| `destination` | TEXT | NOT NULL | Destino del viaje |
| `country` | TEXT | NOT NULL | País |
| `start_datetime` | TEXT | NOT NULL | Fecha y hora de inicio (LocalDateTime serializado) |
| `end_datetime` | TEXT | NOT NULL | Fecha y hora de fin (LocalDateTime serializado) |
| `duration_days` | INTEGER | NOT NULL | Duración en días |
| `budget_amount` | INTEGER | NOT NULL | Presupuesto en € |
| `status_label` | TEXT | NOT NULL | Estado: `UPCOMING`, `PLANNED`, `COMPLETED` |
| `image_res` | INTEGER | NOT NULL | Referencia al recurso drawable |
| `created_at` | TEXT | NOT NULL | Timestamp de creación (LocalDateTime serializado) |

---

### Tabla: `itinerary_items`

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | INTEGER | PK, autoincrement | Identificador del ítem |
| `trip_id` | INTEGER | FK → `trips.id`, CASCADE DELETE | Viaje al que pertenece |
| `day_number` | INTEGER | NOT NULL | Día dentro del viaje (1-based) |
| `section` | TEXT | NOT NULL | Sección del día: `Morning`, `Afternoon`, `Evening` |
| `title` | TEXT | NOT NULL | Título de la actividad |
| `location` | TEXT | NOT NULL | Lugar |
| `cost_amount` | INTEGER | NOT NULL | Coste en € |
| `scheduled_at` | TEXT | NOT NULL | Hora programada (LocalDateTime serializado) |
| `notes` | TEXT | nullable | Notas adicionales |

---

### Tabla: `access_log`

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | INTEGER | PK, autoincrement | Identificador del registro |
| `user_id` | TEXT | NOT NULL, FK → `users.firebase_uid`, CASCADE DELETE | Usuario que genera el evento |
| `event_type` | TEXT | NOT NULL | Tipo de evento: `LOGIN` o `LOGOUT` |
| `timestamp` | TEXT | NOT NULL | Momento del evento (LocalDateTime serializado) |

---

## 🔄 Migraciones

### MIGRATION_1_2

Introducida en el Sprint 3 para añadir soporte multi-usuario.

Cambios aplicados:
- Creación de la tabla `users` con índices únicos en `firebase_uid` y `username`
- Adición de la columna `user_id` en `trips` con FK a `users.firebase_uid`
- Creación del índice `index_trips_user_id`
- Creación de la tabla `access_log` con FK a `users.firebase_uid`
- Creación del índice `index_access_log_user_id`

---

## 🔗 Relaciones entre tablas

```
users (firebase_uid)
  │
  ├──< trips (user_id)
  │       │
  │       └──< itinerary_items (trip_id)
  │
  └──< access_log (user_id)
```

Todas las relaciones usan `ON DELETE CASCADE`: eliminar un usuario elimina sus viajes, ítems de itinerario y registros de acceso.

---

## 📦 Serialización de tipos

Los tipos `LocalDateTime` y `LocalDate` no son soportados nativamente por SQLite. Se serializan a `TEXT` mediante `RoomTypeConverters` usando `DateTimeFormatter.ISO_LOCAL_DATE_TIME`.