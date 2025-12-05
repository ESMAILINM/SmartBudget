package edu.ucne.smartbudget.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.smartbudget.data.local.entities.GastosEntity
import kotlinx.coroutines.flow.Flow

@Dao
    interface GastoDao {
    @Query("SELECT * FROM gastos WHERE usuarioId = :usuarioId")
    fun observeGastosByUsuario(usuarioId: String): Flow<List<GastosEntity>>

    @Query("SELECT * FROM gastos WHERE gastoId = :id")
    suspend fun getGasto(id: String): GastosEntity?

    @Upsert
    suspend fun upsertGasto(gasto: GastosEntity)

    @Query("DELETE FROM gastos WHERE gastoId = :id")
    suspend fun deleteGasto(id: String)

    @Query("SELECT * FROM gastos WHERE isPendingCreate = 1")
    suspend fun getPendingCreateGastos(): List<GastosEntity>

    @Query("SELECT * FROM gastos WHERE isPendingUpdate = 1")
    suspend fun getPendingUpdate(): List<GastosEntity>

    @Query("SELECT * FROM gastos WHERE isPendingDelete = 1")
    suspend fun getPendingDelete(): List<GastosEntity>
}
