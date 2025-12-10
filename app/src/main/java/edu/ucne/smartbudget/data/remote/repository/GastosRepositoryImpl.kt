package edu.ucne.smartbudget.data.remote.repository

import edu.ucne.smartbudget.data.local.dao.GastoDao
import edu.ucne.smartbudget.data.local.dao.UsuarioDao
import edu.ucne.smartbudget.data.local.dao.CategoriaDao
import edu.ucne.smartbudget.data.mapper.toDomain
import edu.ucne.smartbudget.data.mapper.toEntity
import edu.ucne.smartbudget.data.mapper.toRequest
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.remotedatasource.GastosRemoteDataSource
import edu.ucne.smartbudget.domain.model.Gastos
import edu.ucne.smartbudget.domain.repository.GastosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GastosRepositoryImpl @Inject constructor(
    private val dao: GastoDao,
    private val usuarioDao: UsuarioDao,
    private val categoriaDao: CategoriaDao,
    private val remote: GastosRemoteDataSource
) : GastosRepository {

    override fun getGastos(usuarioId: String): Flow<List<Gastos>> =
        dao.observeGastosByUsuario(usuarioId).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getGasto(id: String): Resource<Gastos?> =
        Resource.Success(dao.getGasto(id)?.toDomain())

    override suspend fun insertGasto(gasto: Gastos): Resource<Gastos> {
        val pending = gasto.copy(
            isPendingCreate = true,
            isPendingUpdate = false,
            isPendingDelete = false
        )
        dao.upsertGasto(pending.toEntity())

        return try {
            val usuario = usuarioDao.getUsuario(pending.usuarioId)
            val remoteUser = usuario?.remoteId ?: return Resource.Success(pending)

            val categoria = categoriaDao.getCategoria(pending.categoriaId)
            val remoteCat = categoria?.remoteId ?: return Resource.Success(pending)

            val req = pending.toRequest(
                mappedUsuarioId = remoteUser,
                mappedCategoriaId = remoteCat
            )

            val res = remote.createGasto(req)

            if (res is Resource.Success && res.data != null) {
                val synced = res.data.toEntity().copy(
                    gastoId = pending.gastoId,
                    usuarioId = pending.usuarioId,
                    categoriaId = pending.categoriaId,
                    isPendingCreate = false
                )
                dao.upsertGasto(synced)
                Resource.Success(synced.toDomain())
            } else {
                Resource.Success(pending)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error insertando gasto")
        }
    }

    override suspend fun updateGasto(gasto: Gastos): Resource<Unit> {
        val pending = gasto.copy(isPendingUpdate = true)
        dao.upsertGasto(pending.toEntity())

        val remoteId = gasto.remoteId ?: return Resource.Success(Unit)

        return try {
            val usuario = usuarioDao.getUsuario(gasto.usuarioId)
            val remoteUser = usuario?.remoteId ?: return Resource.Success(Unit)

            val categoria = categoriaDao.getCategoria(gasto.categoriaId)
            val remoteCat = categoria?.remoteId ?: return Resource.Success(Unit)

            val req = pending.toRequest(
                mappedUsuarioId = remoteUser,
                mappedCategoriaId = remoteCat
            )

            val res = remote.updateGasto(remoteId, req)
            if (res is Resource.Success) {
                dao.upsertGasto(pending.copy(isPendingUpdate = false).toEntity())
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error actualizando gasto")
        }
    }

    override suspend fun deleteGasto(id: String): Resource<Unit> {
        val local = dao.getGasto(id) ?: return Resource.Error("No existe")
        val remoteId = local.remoteId

        dao.upsertGasto(local.copy(isPendingDelete = true))

        return try {
            if (remoteId == null) {
                dao.deleteGasto(id)
                return Resource.Success(Unit)
            }

            val res = remote.deleteGasto(remoteId)
            if (res is Resource.Success) {
                dao.deleteGasto(id)
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error eliminando gasto")
        }
    }

    override suspend fun postPendingGastos(): Resource<Unit> {
        val pending = dao.getPendingCreateGastos()

        pending.forEach { localEntity ->
            try {
                val usuario = usuarioDao.getUsuario(localEntity.usuarioId)
                val remoteUser = usuario?.remoteId ?: return@forEach

                val categoria = categoriaDao.getCategoria(localEntity.categoriaId)
                val remoteCat = categoria?.remoteId ?: return@forEach

                val req = localEntity.toDomain().toRequest(
                    mappedUsuarioId = remoteUser,
                    mappedCategoriaId = remoteCat
                )

                val res = remote.createGasto(req)
                if (res is Resource.Success && res.data != null) {
                    val updated = localEntity.copy(
                        remoteId = res.data.gastoId,
                        isPendingCreate = false
                    )
                    dao.upsertGasto(updated)
                }
            } catch (_: Exception) {}
        }

        return Resource.Success(Unit)
    }

    override suspend fun postPendingUpdates(): Resource<Unit> {
        val pending = dao.getPendingUpdate()

        pending.forEach { localEntity ->
            val remoteId = localEntity.remoteId ?: return@forEach

            try {
                val usuario = usuarioDao.getUsuario(localEntity.usuarioId)
                val remoteUser = usuario?.remoteId ?: return@forEach

                val categoria = categoriaDao.getCategoria(localEntity.categoriaId)
                val remoteCat = categoria?.remoteId ?: return@forEach

                val req = localEntity.toDomain().toRequest(
                    mappedUsuarioId = remoteUser,
                    mappedCategoriaId = remoteCat
                )

                val res = remote.updateGasto(remoteId, req)
                if (res is Resource.Success) {
                    dao.upsertGasto(localEntity.copy(isPendingUpdate = false))
                }
            } catch (_: Exception) {}
        }

        return Resource.Success(Unit)
    }

    override suspend fun postPendingDeletes(): Resource<Unit> {
        val pending = dao.getPendingDelete()

        pending.forEach { gasto ->
            try {
                gasto.remoteId?.let { remote.deleteGasto(it) }
                dao.deleteGasto(gasto.gastoId)
            } catch (_: Exception) {}
        }

        return Resource.Success(Unit)
    }
}
