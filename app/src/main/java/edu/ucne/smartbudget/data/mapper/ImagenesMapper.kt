package edu.ucne.smartbudget.data.mapper

import edu.ucne.smartbudget.data.local.entities.ImagenesEntity
import edu.ucne.smartbudget.data.remote.dto.imagenesdto.ImagenRequest
import edu.ucne.smartbudget.data.remote.dto.imagenesdto.ImagenResponse
import edu.ucne.smartbudget.domain.model.Imagenes
import java.util.UUID

fun ImagenesEntity.toDomain(): Imagenes =
    Imagenes(
        imagenId = imagenId,
        remoteId = remoteId,
        metaId = metaId,
        url = url,
        localUrl = localUrl
    )

fun Imagenes.toEntity(): ImagenesEntity =
    ImagenesEntity(
        imagenId = imagenId,
        remoteId = remoteId,
        metaId = metaId,
        url = url,
        localUrl = localUrl,
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )

fun Imagenes.toRequest(): ImagenRequest =
    ImagenRequest(
        metaId = metaId.toIntOrNull() ?: 0,
        url = url
    )

fun ImagenResponse.toEntity(
    currentLocalId: String? = null,
    metaLocalId: String? = null
): ImagenesEntity =
    ImagenesEntity(
        imagenId = currentLocalId ?: UUID.randomUUID().toString(),
        remoteId = imagenId,
        metaId = metaLocalId ?: metaId.toString(),
        url = url ?: "",
        localUrl = "",
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )

fun ImagenResponse.toDomain(
    currentLocalId: String? = null,
    metaLocalId: String? = null
): Imagenes =
    Imagenes(
        imagenId = currentLocalId ?: UUID.randomUUID().toString(),
        remoteId = imagenId,
        metaId = metaLocalId ?: metaId.toString(),
        url = url ?: "",
        localUrl = ""
    )
