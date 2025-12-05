package edu.ucne.smartbudget.data.remote.repository

import edu.ucne.smartbudget.data.local.dao.GastoDao
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.mapper.toDomain
import edu.ucne.smartbudget.data.mapper.toEntity
import edu.ucne.smartbudget.data.mapper.toRequest
import edu.ucne.smartbudget.data.remote.remotedatasource.GastosRemoteDataSource
import edu.ucne.smartbudget.domain.model.Gastos
import edu.ucne.smartbudget.domain.repository.GastosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GastosRepositoryImpl @Inject constructor(
    private val localDataSource: GastoDao,
    private val remoteDataSource: GastosRemoteDataSource
) : GastosRepository {

    override fun getGastos(usuarioId: String): Flow<List<Gastos>> =
        localDataSource.observeGastosByUsuario(usuarioId)
            .map { list ->
                list.filter { !it.isPendingDelete }.map { it.toDomain() }
            }

    override suspend fun getGasto(id: String): Resource<Gastos?> {
        val gasto = localDataSource.getGasto(id)?.toDomain()
        return Resource.Success(gasto)
    }

    override suspend fun insertGasto(gasto: Gastos): Resource<Gastos> {
        val pending = gasto.toEntity().copy(
            isPendingCreate = true,
            isPendingUpdate = false,
            isPendingDelete = false
        )
        localDataSource.upsertGasto(pending)
        return Resource.Success(pending.toDomain())
    }

    override suspend fun updateGasto(gasto: Gastos): Resource<Unit> {
        val remoteId = gasto.remoteId
        val entity = gasto.toEntity()

        return if (remoteId == null) {
            val pending = entity.copy(
                isPendingCreate = true,
                isPendingUpdate = false,
                isPendingDelete = false
            )
            localDataSource.upsertGasto(pending)
            Resource.Success(Unit)
        } else {
            val request = gasto.toRequest()
            when (remoteDataSource.updateGasto(remoteId, request)) {
                is Resource.Success -> {
                    localDataSource.upsertGasto(entity.copy(isPendingUpdate = false))
                    Resource.Success(Unit)
                }
                is Resource.Error -> Resource.Error("Error remoto")
                else -> Resource.Loading()
            }
        }
    }

    override suspend fun deleteGasto(id: String): Resource<Unit> {
        val entity = localDataSource.getGasto(id) ?: return Resource.Error("No encontrado")
        val remoteId = entity.remoteId

        return if (remoteId == null) {
            localDataSource.deleteGasto(id)
            Resource.Success(Unit)
        } else {
            when (remoteDataSource.deleteGasto(remoteId)) {
                is Resource.Success -> {
                    localDataSource.deleteGasto(id)
                    Resource.Success(Unit)
                }
                is Resource.Error -> Resource.Error("Error remoto")
                else -> Resource.Loading()
            }
        }
    }

    override suspend fun postPendingGastos(): Resource<Unit> {
        val pending = localDataSource.getPendingCreateGastos()
        for (item in pending) {
            val request = item.toDomain().toRequest()
            when (val result = remoteDataSource.createGasto(request)) {
                is Resource.Success -> {
                    val synced = item.copy(
                        remoteId = result.data?.gastoId,
                        isPendingCreate = false
                    )
                    localDataSource.upsertGasto(synced)
                }
                is Resource.Error -> return Resource.Error("Falló sincronización")
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

            when (remoteDataSource.updateGasto(remoteId, request)) {
                is Resource.Success ->
                    localDataSource.upsertGasto(item.copy(isPendingUpdate = false))
                is Resource.Error -> return Resource.Error("Falló sincronización")
                else -> {}
            }
        }
        return Resource.Success(Unit)
    }

    override suspend fun postPendingDeletes(): Resource<Unit> {
        val pending = localDataSource.getPendingDelete()
        for (item in pending) {
            val remoteId = item.remoteId

            if (remoteId == null) {
                localDataSource.deleteGasto(item.gastoId)
                continue
            }

            when (remoteDataSource.deleteGasto(remoteId)) {
                is Resource.Success -> localDataSource.deleteGasto(item.gastoId)
                is Resource.Error -> localDataSource.deleteGasto(item.gastoId)
                else -> {}
            }
        }
        return Resource.Success(Unit)
    }
}
