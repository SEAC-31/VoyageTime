package com.example.voyagetime.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.voyagetime.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    // ── Queries ──────────────────────────────────────────────────────────────

    /**
     * Retorna todos los viajes como Flow reactivo.
     * La UI se actualizará automáticamente cuando cambie la BD (necesario para T1.6).
     */
    @Query("SELECT * FROM trips ORDER BY start_datetime ASC")
    fun getAllTrips(): Flow<List<TripEntity>>

    /**
     * Viajes futuros o en curso (estado UPCOMING o PLANNED).
     * statusLabel se compara en mayúsculas para evitar problemas de case.
     */
    @Query("""
        SELECT * FROM trips
        WHERE UPPER(status_label) IN ('UPCOMING', 'PLANNED')
        ORDER BY start_datetime ASC
    """)
    fun getUpcomingTrips(): Flow<List<TripEntity>>

    /**
     * Viajes finalizados (estado COMPLETED).
     */
    @Query("""
        SELECT * FROM trips
        WHERE UPPER(status_label) = 'COMPLETED'
        ORDER BY start_datetime DESC
    """)
    fun getPastTrips(): Flow<List<TripEntity>>

    /**
     * Obtiene un viaje por su ID. Útil para navegar al detalle / itinerario.
     */
    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    suspend fun getTripById(tripId: Long): TripEntity?

    /**
     * Versión suspend de getAllTrips para lecturas puntuales (ej. tests, mappers).
     */
    @Query("SELECT * FROM trips ORDER BY start_datetime ASC")
    suspend fun getAllTripsOnce(): List<TripEntity>

    // ── Mutations ─────────────────────────────────────────────────────────────

    /**
     * Inserta un nuevo viaje y devuelve el rowId generado.
     * Con REPLACE como estrategia de conflicto, si ya existe un id igual lo sobreescribe
     * (útil para upserts futuros).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity): Long

    /**
     * Actualiza un viaje existente (busca por PrimaryKey = id).
     */
    @Update
    suspend fun updateTrip(trip: TripEntity)

    /**
     * Elimina un viaje por su entidad.
     * Room también eliminará en cascada los ItineraryItems asociados
     * gracias al ForeignKey.CASCADE definido en ItineraryItemEntity.
     */
    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    /**
     * Elimina un viaje directamente por ID sin necesidad de tener la entidad cargada.
     */
    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: Long)

    /**
     * Elimina todos los viajes. Útil en tests o para resetear datos.
     */
    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()
}