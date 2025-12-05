package edu.ucne.smartbudget.data.remote.repository

import edu.ucne.smartbudget.data.local.dao.CategoriaDao
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.mapper.toDomain
import edu.ucne.smartbudget.data.mapper.toEntity
import edu.ucne.smartbudget.data.mapper.toRequest
import edu.ucne.smartbudget.data.remote.remotedatasource.CategoriasRemoteDataSource
import edu.ucne.smartbudget.domain.model.Categorias
import edu.ucne.smartbudget.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriasRepositoryImpl @Inject constructor(
    private val localDataSource: CategoriaDao,
    private val remoteDataSource: CategoriasRemoteDataSource
) : CategoriaRepository {

    override suspend fun insertCategoria(categorias: Categorias): Resource<Categorias> {
        val pending = categorias.copy(isPendingCreate = true)
        localDataSource.upsertCategoria(pending.toEntity())
        return Resource.Success(pending)
    }

    override suspend fun upsertCategoria(categorias: Categorias): Resource<Unit> {
        return try {
            val isPendingCreate = categorias.remoteId == null || categorias.isPendingCreate
            val updated = if (isPendingCreate) {
                categorias.copy(isPendingCreate = true, isPendingUpdate = false)
            } else {
                categorias.copy(isPendingUpdate = true)
            }
            localDataSource.upsertCategoria(updated.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error local")
        }
    }

    override suspend fun deleteCategoria(id: String): Resource<Unit> {
        return try {
            val entity = localDataSource.getCategoria(id)
                ?: return Resource.Error("No encontrada")

            val domain = entity.toDomain()

            if (domain.remoteId == null) {
                localDataSource.deleteCategoria(id)
            } else {
                val pendingDelete = domain.copy(isPendingDelete = true)
                localDataSource.upsertCategoria(pendingDelete.toEntity())
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error local")
        }
    }

    override suspend fun postPendingCategorias(): Resource<Unit> {
        val pending = localDataSource.getPendingCreateCategorias()
        for (item in pending) {
            val request = item.toDomain().toRequest()
            when (val result = remoteDataSource.createCategoria(request)) {
                is Resource.Success -> {
                    val synced = item.copy(
                        remoteId = result.data?.categoriaId,
                        isPendingCreate = false
                    )
                    localDataSource.upsertCategoria(synced)
                }
                else -> {}
            }
        }
        return Resource.Success(Unit)
    }

    override suspend fun postPendingUpdates(): Resource<Unit> {
        val pending = localDataSource.getPendingUpdate()
        for (item in pending) {
            val remoteId = item.remoteId ?: continue
            val request = item.toDomain().toRequest()
            when (remoteDataSource.updateCategoria(remoteId, request)) {
                is Resource.Success -> {
                    val synced = item.copy(isPendingUpdate = false)
                    localDataSource.upsertCategoria(synced)
                }
                else -> {}
            }
        }
        return Resource.Success(Unit)
    }

    override suspend fun postPendingDeletes(): Resource<Unit> {
        val pending = localDataSource.getPendingDelete()
        for (item in pending) {
            if (item.remoteId == null) {
                localDataSource.deleteCategoria(item.categoriaId)
                continue
            }
            when (val result = remoteDataSource.deleteCategoria(item.remoteId)) {
                is Resource.Success -> localDataSource.deleteCategoria(item.categoriaId)
                is Resource.Error -> {
                    if (result.message?.contains("404") == true) {
                        localDataSource.deleteCategoria(item.categoriaId)
                    }
                }
                else -> {}
            }
        }
        return Resource.Success(Unit)
    }

    override fun getCategorias(): Flow<List<Categorias>> =
        localDataSource.observeCategorias()
            .map { list ->
                list.map { it.toDomain() }
                    .filter { !it.isPendingDelete }
            }

    override suspend fun getCategoria(id: String): Resource<Categorias?> {
        val local = localDataSource.getCategoria(id)?.toDomain()
        if (local != null) return Resource.Success(local)

        val remoteId = id.toIntOrNull() ?: return Resource.Error("No encontrado")

        return when (val res = remoteDataSource.getCategoria(remoteId)) {
            is Resource.Success -> {
                res.data?.let {
                    localDataSource.upsertCategoria(it.toDomain().toEntity())
                }
                Resource.Success(res.data?.toDomain())
            }
            is Resource.Error -> Resource.Error(res.message ?: "Error remoto")
            else -> Resource.Error("Error desconocido")
        }
    }
}
