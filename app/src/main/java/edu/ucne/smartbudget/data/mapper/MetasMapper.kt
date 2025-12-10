package edu.ucne.smartbudget.data.mapper

import edu.ucne.smartbudget.data.local.entities.MetasEntity
import edu.ucne.smartbudget.data.remote.dto.metasdto.MetaRequest
import edu.ucne.smartbudget.data.remote.dto.metasdto.MetaResponse
import edu.ucne.smartbudget.domain.model.Imagenes
import edu.ucne.smartbudget.domain.model.Metas

fun Metas.toRequest(mappedUsuarioId: Int): MetaRequest =
    MetaRequest(
        nombre = nombre,
        fecha = fecha.ifEmpty { null },
        monto = monto,
        contribucionMensual = contribucionMensual,
        emoji = emoji.ifEmpty { null },
        imagenes = imagenes.map { it.toRequest(mappedUsuarioId) },
        usuarioId = mappedUsuarioId
    )

fun MetaResponse.toDomain(currentLocalId: String, imagenes: List<Imagenes> = emptyList()): Metas {
    return Metas(
        metaId = currentLocalId,
        remoteId = this.metaId,
        nombre = nombre,
        contribucionMensual = contribucionMensual,
        monto = monto,
        fecha = fecha ?: "",
        imagenes = imagenes,
        emoji = emoji ?: "",
        usuarioId = usuarioId.toString(),
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )
}

fun MetasEntity.toDomain(imagenes: List<Imagenes>): Metas =
    Metas(
        metaId = metaId,
        remoteId = remoteId,
        nombre = nombre,
        contribucionMensual = contribucionMensual,
        monto = monto,
        fecha = fecha,
        imagenes = imagenes,
        emoji = emoji ?: "",
        usuarioId = usuarioId,
        isPendingCreate = isPendingCreate,
        isPendingUpdate = isPendingUpdate,
        isPendingDelete = isPendingDelete
    )

fun Metas.toEntity(): MetasEntity =
    MetasEntity(
        metaId = metaId,
        remoteId = remoteId,
        nombre = nombre,
        contribucionMensual = contribucionMensual,
        monto = monto,
        fecha = fecha,
        emoji = emoji.ifEmpty { null },
        usuarioId = usuarioId,
        isPendingCreate = isPendingCreate,
        isPendingUpdate = isPendingUpdate,
        isPendingDelete = isPendingDelete
    )
