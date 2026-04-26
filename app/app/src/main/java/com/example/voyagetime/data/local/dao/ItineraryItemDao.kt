package com.example.voyagetime.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.voyagetime.data.local.entity.ItineraryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryItemDao {

    // ── Queries ──────────────────────────────────────────────────────────────

    /**
     * Todos los items de un viaje concreto como Flow reactivo, ordenados por día y hora.
     * La UI del itinerario se actualizará automáticamente al añadir/editar/borrar (T1.6).
     */
    @Query("""
        SELECT * FROM itinerary_items
        WHERE trip_id = :tripId
        ORDER BY day_number ASC, scheduled_at ASC
    """)
    fun getItemsByTrip(tripId: Long): Flow<List<ItineraryItemEntity>>

    /**
     * Items de un día concreto dentro de un viaje, ordenados por hora.
     */
    @Query("""
        SELECT * FROM itinerary_items
        WHERE trip_id = :tripId AND day_number = :dayNumber
        ORDER BY scheduled_at ASC
    """)
    fun getItemsByDay(tripId: Long, dayNumber: Int): Flow<List<ItineraryItemEntity>>

    /**
     * Items de un día y sección específicos (MORNING / AFTERNOON / EVENING).
     * El campo section se almacena como String con esos valores.
     */
    @Query("""
        SELECT * FROM itinerary_items
        WHERE trip_id = :tripId AND day_number = :dayNumber AND UPPER(section) = UPPER(:section)
        ORDER BY scheduled_at ASC
    """)
    fun getItemsBySection(tripId: Long, dayNumber: Int, section: String): Flow<List<ItineraryItemEntity>>

    /**
     * Obtiene un item por su ID. Útil para editar un evento concreto.
     */
    @Query("SELECT * FROM itinerary_items WHERE id = :itemId LIMIT 1")
    suspend fun getItemById(itemId: Long): ItineraryItemEntity?

    /**
     * Versión suspend de getItemsByTrip para lecturas puntuales (tests, mappers).
     */
    @Query("""
        SELECT * FROM itinerary_items
        WHERE trip_id = :tripId
        ORDER BY day_number ASC, scheduled_at ASC
    """)
    suspend fun getItemsByTripOnce(tripId: Long): List<ItineraryItemEntity>

    // ── Mutations ─────────────────────────────────────────────────────────────

    /**
     * Inserta un nuevo evento en el itinerario y devuelve el rowId generado.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItineraryItemEntity): Long

    /**
     * Inserta una lista de eventos de golpe (útil para inicializar días de un viaje nuevo).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItineraryItemEntity>)

    /**
     * Actualiza un evento existente (busca por PrimaryKey = id).
     */
    @Update
    suspend fun updateItem(item: ItineraryItemEntity)

    /**
     * Elimina un evento por su entidad.
     */
    @Delete
    suspend fun deleteItem(item: ItineraryItemEntity)

    /**
     * Elimina un evento directamente por ID sin necesidad de tener la entidad cargada.
     */
    @Query("DELETE FROM itinerary_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Long)

    /**
     * Elimina todos los eventos de un viaje. Útil si se regenera el itinerario completo.
     * Nota: el ForeignKey.CASCADE en la entity ya hace esto automáticamente al borrar el Trip,
     * pero este método permite hacerlo sin borrar el viaje.
     */
    @Query("DELETE FROM itinerary_items WHERE trip_id = :tripId")
    suspend fun deleteAllItemsForTrip(tripId: Long)

    /**
     * Elimina todos los eventos de un día concreto dentro de un viaje.
     */
    @Query("DELETE FROM itinerary_items WHERE trip_id = :tripId AND day_number = :dayNumber")
    suspend fun deleteItemsForDay(tripId: Long, dayNumber: Int)
}