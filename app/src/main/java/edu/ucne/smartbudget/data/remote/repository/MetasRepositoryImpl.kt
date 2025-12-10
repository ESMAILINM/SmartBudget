package edu.ucne.smartbudget.data.remote.repository

import edu.ucne.smartbudget.data.local.dao.ImagenesDao
import edu.ucne.smartbudget.data.local.dao.MetasDao
import edu.ucne.smartbudget.data.local.dao.UsuarioDao
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
    private val local: MetasDao,
    private val imagenesDao: ImagenesDao,
    private val remote: MetasRemoteDataSource,
    private val usuarioDao: UsuarioDao
) : MetasRepository {

    override fun getMetas(usuarioId: String): Flow<List<Metas>> =
        local.observeMetasByUsuario(usuarioId).map { list ->
            list.filter { !it.isPendingDelete }.map { meta ->
                val imgs = imagenesDao.getImagesByMeta(meta.metaId).map { it.toDomain() }
                meta.toDomain(imgs)
            }
        }

    override suspend fun getMeta(id: String): Resource<Metas?> {
        val meta = local.getMeta(id) ?: return Resource.Success(null)
        val imgs = imagenesDao.getImagesByMeta(id).map { it.toDomain() }
        return Resource.Success(meta.toDomain(imgs))
    }

    override suspend fun insertMeta(meta: Metas): Resource<Metas> {
        val pending = meta.copy(
            isPendingCreate = true,
            isPendingUpdate = false,
            isPendingDelete = false
        )

        local.upsertMeta(pending.toEntity())
        pending.imagenes.forEach { img ->
            imagenesDao.upsertImagen(
                img.copy(
                    metaId = pending.metaId,
                    remoteId = null,
                    isPendingCreate = true
                ).toEntity()
            )
        }

        try {
            val usuario = usuarioDao.getUsuario(pending.usuarioId) ?: return Resource.Success(pending)
            val remoteUser = usuario.remoteId ?: return Resource.Success(pending)
            val req = pending.toRequest(remoteUser)
            val res = remote.insertMeta(req)

            if (res is Resource.Success && res.data != null) {
                val server = res.data
                val syncedDomain = server.toDomain(currentLocalId = pending.metaId, imagenes = pending.imagenes)
                local.upsertMeta(syncedDomain.toEntity())

                val pendingImages = imagenesDao.getPendingCreateImagesByMeta(pending.metaId)
                server.imagenes?.forEach { imgResp ->
                    val urlToMatch = imgResp.url ?: ""
                    val localImg = pendingImages.firstOrNull { it.url == urlToMatch || it.localUrl == urlToMatch }
                    if (localImg != null) {
                        imagenesDao.updateImagenRemoteId(localImg.imagenId, imgResp.imagenId)
                    } else {
                        val newImgEntity = imgResp.toEntity(currentLocalId = null, metaLocalId = pending.metaId)
                        imagenesDao.upsertImagen(newImgEntity)
                    }
                }

                return Resource.Success(syncedDomain)
            }
        } catch (_: Exception) { }

        return Resource.Success(pending)
    }

    override suspend fun updateMeta(meta: Metas): Resource<Unit> {
        val pending = meta.copy(isPendingUpdate = true)
        local.upsertMeta(pending.toEntity())

        pending.imagenes.forEach { img ->
            imagenesDao.upsertImagen(img.copy(metaId = pending.metaId).toEntity())
        }

        val remoteId = meta.remoteId ?: return Resource.Success(Unit)

        try {
            val usuario = usuarioDao.getUsuario(meta.usuarioId) ?: run {
                local.upsertMeta(pending.copy(isPendingUpdate = true).toEntity())
                return Resource.Success(Unit)
            }
            val remoteUser = usuario.remoteId ?: run {
                local.upsertMeta(pending.copy(isPendingUpdate = true).toEntity())
                return Resource.Success(Unit)
            }

            val req = pending.toRequest(remoteUser)
            val res = remote.updateMeta(remoteId, req)

            if (res is Resource.Success) {
                local.upsertMeta(pending.copy(isPendingUpdate = false).toEntity())
                val imgs = imagenesDao.getImagesByMeta(meta.metaId)
                imgs.forEach { if (it.isPendingUpdate) imagenesDao.clearPendingUpdate(it.imagenId) }
            } else {
                local.upsertMeta(pending.copy(isPendingUpdate = true).toEntity())
            }
        } catch (_: Exception) {
            local.upsertMeta(pending.copy(isPendingUpdate = true).toEntity())
        }

        return Resource.Success(Unit)
    }

    override suspend fun deleteMeta(id: String): Resource<Unit> {
        val entity = local.getMeta(id) ?: return Resource.Error("Meta no encontrada")
        val remoteId = entity.remoteId

        imagenesDao.markImagesForDelete(id)
        local.upsertMeta(entity.copy(isPendingDelete = true))

        if (remoteId == null) {
            imagenesDao.deleteImagesByMeta(id)
            local.deleteMeta(id)
            return Resource.Success(Unit)
        }

        try {
            val res = remote.deleteMeta(remoteId)
            if (res is Resource.Success) {
                imagenesDao.deleteImagesByMeta(id)
                local.deleteMeta(id)
            }
        } catch (_: Exception) { }

        return Resource.Success(Unit)
    }

    override suspend fun postPendingMetas(): Resource<Unit> {
        val pendingList = local.getPendingCreateMetas()
        for (entity in pendingList) {
            try {
                val imagenes = imagenesDao.getImagesByMeta(entity.metaId).map { it.toDomain() }
                val domain = entity.toDomain(imagenes)

                val mappedUsuario = usuarioDao.getUsuario(domain.usuarioId)
                val remoteUsuarioId = mappedUsuario?.remoteId ?: continue

                val req = domain.toRequest(mappedUsuarioId = remoteUsuarioId)
                val res = remote.insertMeta(req)

                if (res is Resource.Success && res.data != null) {
                    val server = res.data
                    local.upsertMeta(entity.copy(remoteId = server.metaId, isPendingCreate = false))
                    val pendingImages = imagenesDao.getPendingCreateImagesByMeta(entity.metaId)
                    server.imagenes?.forEach { imgResp ->
                        val urlToMatch = imgResp.url ?: ""
                        val localImg = pendingImages.firstOrNull { it.url == urlToMatch || it.localUrl == urlToMatch }
                        if (localImg != null) {
                            imagenesDao.updateImagenRemoteId(localImg.imagenId, imgResp.imagenId)
                        } else {
                            val newImgEntity = imgResp.toEntity(currentLocalId = null, metaLocalId = entity.metaId)
                            imagenesDao.upsertImagen(newImgEntity)
                        }
                    }
                }
            } catch (_: Exception) { }
        }
        return Resource.Success(Unit)
    }

    override suspend fun postPendingUpdates(): Resource<Unit> {
        val pendingList = local.getPendingUpdate()
        for (entity in pendingList) {
            val remoteId = entity.remoteId ?: continue
            try {
                val imagenes = imagenesDao.getImagesByMeta(entity.metaId).map { it.toDomain() }
                val domain = entity.toDomain(imagenes)

                val mappedUsuario = usuarioDao.getUsuario(domain.usuarioId)
                val remoteUsuarioId = mappedUsuario?.remoteId ?: continue

                val req = domain.toRequest(mappedUsuarioId = remoteUsuarioId)
                val res = remote.updateMeta(remoteId, req)

                if (res is Resource.Success) {
                    local.upsertMeta(entity.copy(isPendingUpdate = false))
                    val imgs = imagenesDao.getImagesByMeta(entity.metaId)
                    imgs.forEach { if (it.isPendingUpdate) imagenesDao.clearPendingUpdate(it.imagenId) }
                }
            } catch (_: Exception) { }
        }
        return Resource.Success(Unit)
    }

    override suspend fun postPendingDeletes(): Resource<Unit> {
        val pendingList = local.getPendingDelete()
        for (entity in pendingList) {
            try {
                entity.remoteId?.let { remote.deleteMeta(it) }
                imagenesDao.deleteImagesByMeta(entity.metaId)
                local.deleteMeta(entity.metaId)
            } catch (_: Exception) { }
        }
        return Resource.Success(Unit)
    }
}
