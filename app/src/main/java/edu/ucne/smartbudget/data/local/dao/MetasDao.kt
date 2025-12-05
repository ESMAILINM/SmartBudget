package edu.ucne.smartbudget.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.smartbudget.data.local.entities.MetasEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface MetasDao {
    @Query("SELECT * FROM metas WHERE usuarioId = :usuarioId")
    fun observeMetasByUsuario(usuarioId: String): Flow<List<MetasEntity>>

    @Query("SELECT * FROM metas WHERE metaId = :id")
    suspend fun getMeta(id: String): MetasEntity?

    @Upsert
    suspend fun upsertMeta(meta: MetasEntity)

    @Query("DELETE FROM metas WHERE metaId = :id")
    suspend fun deleteMeta(id: String)

    @Query("SELECT * FROM metas WHERE isPendingCreate = 1")
    suspend fun getPendingCreateMetas(): List<MetasEntity>

    @Query("SELECT * FROM metas WHERE isPendingUpdate = 1")
    suspend fun getPendingUpdate(): List<MetasEntity>

    @Query("SELECT * FROM metas WHERE isPendingDelete = 1")
    suspend fun getPendingDelete(): List<MetasEntity>
}

