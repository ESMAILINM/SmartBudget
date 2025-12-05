package edu.ucne.smartbudget.domain.repository

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Categorias
import kotlinx.coroutines.flow.Flow

interface CategoriaRepository {
    fun getCategorias(): Flow<List<Categorias>>

    suspend fun getCategoria(id: String): Resource<Categorias?>

    suspend fun insertCategoria(categorias: Categorias): Resource<Categorias>

    suspend fun upsertCategoria(categorias: Categorias) : Resource<Unit>

    suspend fun deleteCategoria(id: String): Resource<Unit>

    suspend fun postPendingCategorias(): Resource<Unit>

    suspend fun postPendingDeletes(): Resource<Unit>

    suspend fun postPendingUpdates(): Resource<Unit>
}
