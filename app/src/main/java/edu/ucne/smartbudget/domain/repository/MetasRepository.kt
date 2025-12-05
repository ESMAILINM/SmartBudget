package edu.ucne.smartbudget.domain.repository

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Metas
import kotlinx.coroutines.flow.Flow

interface MetasRepository {

    fun getMetas(usuarioId: String): Flow<List<Metas>>

    suspend fun getMeta(id: String): Resource<Metas?>

    suspend fun insertMeta(meta: Metas): Resource<Metas>

    suspend fun updateMeta(meta: Metas): Resource<Unit>

    suspend fun deleteMeta(id: String): Resource<Unit>

    suspend fun postPendingMetas(): Resource<Unit>

    suspend fun postPendingDeletes(): Resource<Unit>

    suspend fun postPendingUpdates(): Resource<Unit>
}
