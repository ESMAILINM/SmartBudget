package edu.ucne.smartbudget.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.smartbudget.data.local.entities.IngresosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngresoDao {

    @Query("SELECT * FROM ingresos WHERE usuarioId = :usuarioId")
    fun observeIngresosByUsuario(usuarioId: String): Flow<List<IngresosEntity>>

    @Query("SELECT * FROM ingresos WHERE ingresoId = :id")
    suspend fun getIngreso(id: String): IngresosEntity?

    @Upsert
    suspend fun upsertIngreso(ingreso: IngresosEntity)

    @Query("DELETE FROM ingresos WHERE ingresoId = :id")
    suspend fun deleteIngreso(id: String)

    @Query("SELECT * FROM ingresos WHERE isPendingCreate = 1")
    suspend fun getPendingCreateIngresos(): List<IngresosEntity>

    @Query("SELECT * FROM ingresos WHERE isPendingUpdate = 1")
    suspend fun getPendingUpdate(): List<IngresosEntity>

    @Query("SELECT * FROM ingresos WHERE isPendingDelete = 1")
    suspend fun getPendingDelete(): List<IngresosEntity>
}

