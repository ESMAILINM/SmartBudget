package edu.ucne.smartbudget.data.mapper

import edu.ucne.smartbudget.data.local.entities.ImagenesEntity
import edu.ucne.smartbudget.data.remote.dto.imagenesdto.ImagenRequest
import edu.ucne.smartbudget.data.remote.dto.imagenesdto.ImagenResponse
import edu.ucne.smartbudget.domain.model.Imagenes
import java.util.UUID

// ENTITY -> DOMAIN
fun ImagenesEntity.toDomain(): Imagenes =
    Imagenes(
        imagenId = imagenId,
        remoteId = remoteId,
        metaId = metaId,
        url = url,
        localUrl = localUrl.ifBlank { null },
        isPendingCreate = isPendingCreate,
        isPendingUpdate = isPendingUpdate,
        isPendingDelete = isPendingDelete
    )

// DOMAIN -> ENTITY
fun Imagenes.toEntity(): ImagenesEntity =
    ImagenesEntity(
        imagenId = imagenId,
        remoteId = remoteId,
        metaId = metaId,
        url = url,
        localUrl = localUrl ?: "",
        isPendingCreate = isPendingCreate,
        isPendingUpdate = isPendingUpdate,
        isPendingDelete = isPendingDelete
    )

// DOMAIN -> REQUEST (usa el metaId mapeado a Int)
fun Imagenes.toRequest(mappedMetaId: Int): ImagenRequest =
    ImagenRequest(
        metaId = mappedMetaId,
        url = url.takeIf { it.isNotBlank() } ?: localUrl ?: ""
    )

// RESPONSE -> ENTITY (requiere metaLocalId para no mezclar ids)
fun ImagenResponse.toEntity(
    currentLocalId: String? = null,
    metaLocalId: String
): ImagenesEntity =
    ImagenesEntity(
        imagenId = currentLocalId ?: UUID.randomUUID().toString(),
        remoteId = this.imagenId,
        metaId = metaLocalId,
        url = this.url ?: "",
        localUrl = "",
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )

// RESPONSE -> DOMAIN (requiere metaLocalId)
fun ImagenResponse.toDomain(
    currentLocalId: String? = null,
    metaLocalId: String
): Imagenes =
    Imagenes(
        imagenId = currentLocalId ?: UUID.randomUUID().toString(),
        remoteId = this.imagenId,
        metaId = metaLocalId,
        url = this.url ?: "",
        localUrl = null,
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )
