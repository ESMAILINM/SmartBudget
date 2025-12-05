package edu.ucne.smartbudget.data.remote.repository

import edu.ucne.smartbudget.data.local.dao.ImagenesDao
import edu.ucne.smartbudget.data.local.dao.MetasDao
import edu.ucne.smartbudget.data.mapper.toDomain
import edu.ucne.smartbudget.data.mapper.toEntity
import edu.ucne.smartbudget.data.mapper.toRequest
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.remotedatasource.MetasRemoteDataSource
import edu.ucne.smartbudget.domain.model.Metas
import edu.ucne.smartbudget.domain.repository.MetasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MetasRepositoryImpl @Inject constructor(
    private val localDataSource: MetasDao,
    private val imagenesDao: ImagenesDao,
    private val remoteDataSource: MetasRemoteDataSource,
) : MetasRepository {

    override fun getMetas(usuarioId: String): Flow<List<Metas>> =
        localDataSource.observeMetasByUsuario(usuarioId).map { list ->
            list.map { meta ->
                val imagenes = imagenesDao.getImagesByMeta(meta.metaId).map { it.toDomain() }
                meta.toDomain().copy(Imagenes = imagenes)
            }.filter { !it.isPendingDelete }
        }

    override suspend fun getMeta(id: String): Resource<Metas?> {
        val meta = localDataSource.getMeta(id) ?: return Resource.Success(null)
        val imagenes = imagenesDao.getImagesByMeta(id).map { it.toDomain() }
        return Resource.Success(meta.toDomain().copy(Imagenes = imagenes))
    }

    override suspend fun insertMeta(meta: Metas): Resource<Metas> {
        val pending = meta.copy(
            isPendingCreate = true,
            isPendingUpdate = false,
            isPendingDelete = false
        )

        localDataSource.upsertMeta(pending.toEntity())

        pending.Imagenes.forEach { img ->
            imagenesDao.upsertImagen(
                img.copy(metaId = pending.metaId, isPendingCreate = true).toEntity()
            )
        }

        return Resource.Success(pending)
    }

    override suspend fun updateMeta(meta: Metas): Resource<Unit> {
        val remoteId = meta.remoteId ?: return Resource.Error("No remoteId")
        val req = meta.toRequest()

        return when (remoteDataSource.updateMeta(remoteId, req)) {
            is Resource.Success -> {
                localDataSource.upsertMeta(meta.toEntity())
                Resource.Success(Unit)
            }
            is Resource.Error -> Resource.Error("Falló actualización")
            else -> Resource.Loading()
        }
    }

    override suspend fun deleteMeta(id: String): Resource<Unit> {
        val entity = localDataSource.getMeta(id) ?: return Resource.Error("No encontrada")
        val remoteId = entity.remoteId ?: return Resource.Error("No remoteId")

        return when (remoteDataSource.deleteMeta(remoteId)) {
            is Resource.Success -> {
                imagenesDao.deleteImagesByMeta(id)
                localDataSource.deleteMeta(id)
                Resource.Success(Unit)
            }
            is Resource.Error -> Resource.Error("Falló eliminación")
            else -> Resource.Loading()
        }
    }

    override suspend fun postPendingMetas(): Resource<Unit> {
        val pending = localDataSource.getPendingCreateMetas()

        for (meta in pending) {
            val req = meta.toDomain().toRequest()

            when (val res = remoteDataSource.insertMeta(req)) {
                is Resource.Success -> {
                    val synced = meta.copy(
                        remoteId = res.data?.metaId,
                        isPendingCreate = false
                    )
                    localDataSource.upsertMeta(synced)
                }
                is Resource.Error -> return Resource.Error("Falló sincronización")
                else -> {}
            }
        }
        return Resource.Success(Unit)
    }

    override suspend fun postPendingUpdates(): Resource<Unit> {
        val pending = localDataSource.getPendingUpdate()

        for (meta in pending) {
            val remoteId = meta.remoteId ?: continue
            val req = meta.toDomain().toRequest()

            when (remoteDataSource.updateMeta(remoteId, req)) {
                is Resource.Success ->
                    localDataSource.upsertMeta(meta.copy(isPendingUpdate = false))
                else -> {}
            }
        }

        return Resource.Success(Unit)
    }

    override suspend fun postPendingDeletes(): Resource<Unit> {
        val pending = localDataSource.getPendingDelete()

        for (meta in pending) {
            val remoteId = meta.remoteId ?: continue

            when (remoteDataSource.deleteMeta(remoteId)) {
                is Resource.Success -> {
                    imagenesDao.deleteImagesByMeta(meta.metaId)
                    localDataSource.deleteMeta(meta.metaId)
                }
                else -> {}
            }
        }

        return Resource.Success(Unit)
    }
}
