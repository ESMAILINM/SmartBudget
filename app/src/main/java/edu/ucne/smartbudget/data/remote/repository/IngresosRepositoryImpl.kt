package edu.ucne.smartbudget.data.remote.repository

import edu.ucne.smartbudget.data.local.dao.CategoriaDao
import edu.ucne.smartbudget.data.local.dao.IngresoDao
import edu.ucne.smartbudget.data.local.dao.UsuarioDao
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.mapper.toDomain
import edu.ucne.smartbudget.data.mapper.toEntity
import edu.ucne.smartbudget.data.mapper.toRequest
import edu.ucne.smartbudget.data.remote.remotedatasource.IngresosRemoteDataSource
import edu.ucne.smartbudget.domain.model.Ingresos
import edu.ucne.smartbudget.domain.repository.IngresoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IngresosRepositoryImpl @Inject constructor(
    private val localDataSource: IngresoDao,
    private val remoteDataSource: IngresosRemoteDataSource,
    private val categoriaDao: CategoriaDao,
    private val usuarioDao: UsuarioDao
) : IngresoRepository {

    override suspend fun insertIngreso(ingreso: Ingresos): Resource<Ingresos> {
        val pending = ingreso.copy(isPendingCreate = true)
        localDataSource.upsertIngreso(pending.toEntity())
        return Resource.Success(pending)
    }

    override suspend fun upsertIngreso(ingreso: Ingresos): Resource<Unit> {
        return try {
            val isPendingCreate = ingreso.remoteId == null || ingreso.isPendingCreate

            val updated = if (isPendingCreate) {
                ingreso.copy(isPendingCreate = true, isPendingUpdate = false)
            } else {
                ingreso.copy(isPendingUpdate = true)
            }

            localDataSource.upsertIngreso(updated.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error local")
        }
    }

    override suspend fun deleteIngreso(id: String): Resource<Unit> {
        return try {
            val entity = localDataSource.getIngreso(id) ?: return Resource.Error("No encontrado")
            val domain = entity.toDomain()

            if (domain.remoteId == null) {
                localDataSource.deleteIngreso(id)
            } else {
                val pendingDelete = domain.copy(isPendingDelete = true)
                localDataSource.upsertIngreso(pendingDelete.toEntity())
            }
            Resource.Success(Unit)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error local")
        }
    }

    override suspend fun postPendingIngresos(): Resource<Unit> {
        val pending = localDataSource.getPendingCreateIngresos()

        for (entity in pending) {
            val domain = entity.toDomain()

            val mappedCategoria = categoriaDao.getCategoria(domain.categoriaId)
            val mappedUsuario = usuarioDao.getUsuario(domain.usuarioId)

            val remoteCategoriaId = mappedCategoria?.remoteId
            val remoteUsuarioId = mappedUsuario?.remoteId

            if (remoteCategoriaId == null || remoteUsuarioId == null) continue

            val request = domain.toRequest(
                mappedCategoriaId = remoteCategoriaId,
                mappedUsuarioId = remoteUsuarioId
            )

            when (val result = remoteDataSource.insertIngreso(request)) {
                is Resource.Success -> {
                    val syncedEntity = domain.copy(
                        remoteId = result.data?.ingresoId,
                        isPendingCreate = false
                    ).toEntity()
                    localDataSource.upsertIngreso(syncedEntity)
                }
                else -> Unit
            }
        }

        return Resource.Success(Unit)
    }

    override suspend fun postPendingUpdates(): Resource<Unit> {
        val pending = localDataSource.getPendingUpdate()

        for (entity in pending) {
            val domain = entity.toDomain()
            val remoteId = domain.remoteId ?: continue

            val mappedCategoria = categoriaDao.getCategoria(domain.categoriaId)
            val mappedUsuario = usuarioDao.getUsuario(domain.usuarioId)

            val remoteCategoriaId = mappedCategoria?.remoteId
            val remoteUsuarioId = mappedUsuario?.remoteId

            if (remoteCategoriaId == null || remoteUsuarioId == null) continue

            val request = domain.toRequest(
                mappedCategoriaId = remoteCategoriaId,
                mappedUsuarioId = remoteUsuarioId
            )

            when (remoteDataSource.updateIngreso(remoteId, request)) {
                is Resource.Success -> {
                    val syncedEntity = domain.copy(isPendingUpdate = false).toEntity()
                    localDataSource.upsertIngreso(syncedEntity)
                }
                else -> Unit
            }
        }

        return Resource.Success(Unit)
    }

    override suspend fun postPendingDeletes(): Resource<Unit> {
        val pending = localDataSource.getPendingDelete()

        for (entity in pending) {
            if (entity.remoteId == null) {
                localDataSource.deleteIngreso(entity.ingresoId)
                continue
            }

            when (val result = remoteDataSource.deleteIngreso(entity.remoteId)) {
                is Resource.Success -> localDataSource.deleteIngreso(entity.ingresoId)
                is Resource.Error -> {
                    if (result.message?.contains("404") == true) {
                        localDataSource.deleteIngreso(entity.ingresoId)
                    }
                }
                else -> Unit
            }
        }

        return Resource.Success(Unit)
    }

    override fun getIngresos(usuarioId: String): Flow<List<Ingresos>> =
        localDataSource.observeIngresosByUsuario(usuarioId)
            .map { list ->
                list.map { it.toDomain() }
                    .filter { !it.isPendingDelete }
            }

    override suspend fun getIngreso(id: String): Resource<Ingresos?> {
        val local = localDataSource.getIngreso(id)?.toDomain()
        if (local != null) return Resource.Success(local)

        val remoteId = id.toIntOrNull() ?: return Resource.Error("No encontrado")

        return when (val res = remoteDataSource.getIngreso(remoteId)) {
            is Resource.Success -> {
                val dto = res.data ?: return Resource.Success(null)

                val categoriaLocal = categoriaDao.getCategoriaByRemote(dto.categoriaId)
                val usuarioLocal = usuarioDao.getUsuarioByRemoteId(dto.usuarioId)

                val entity = dto.toEntity().copy(
                    categoriaId = categoriaLocal?.categoriaId ?: "",
                    usuarioId = usuarioLocal?.usuarioId ?: ""
                )

                localDataSource.upsertIngreso(entity)

                Resource.Success(entity.toDomain())
            }
            is Resource.Error -> Resource.Error(res.message ?: "Error remoto")
            is Resource.Loading -> Resource.Loading()
        }
    }
}
