package edu.ucne.smartbudget.data.mapper

import edu.ucne.smartbudget.data.local.entities.MetasEntity
import edu.ucne.smartbudget.data.remote.dto.metasdto.MetaRequest
import edu.ucne.smartbudget.data.remote.dto.metasdto.MetaResponse
import edu.ucne.smartbudget.domain.model.Metas
import java.util.UUID

fun Metas.toRequest(): MetaRequest =
    MetaRequest(
        nombre = nombre,
        fecha = fecha.ifEmpty { null },
        monto = monto,
        contribucionMensual = contribucionMensual,
        emoji = emoji.ifEmpty { null },
        imagenes = Imagenes.map { it.toRequest() },
        usuarioId = usuarioId.toIntOrNull() ?: 0
    )

fun MetaResponse.toDomain(currentLocalId: String? = null): Metas =
    Metas(
        metaId = currentLocalId ?: metaId.toString(),
        remoteId = metaId,
        nombre = nombre,
        contribucionMensual = contribucionMensual,
        monto = monto,
        fecha = fecha ?: "",
        Imagenes = imagenes?.map { it.toDomain(metaLocalId = currentLocalId) } ?: emptyList(),
        emoji = emoji ?: "",
        usuarioId = usuarioId.toString()
    )

fun MetaResponse.toEntity(currentLocalId: String? = null): MetasEntity =
    MetasEntity(
        metaId = currentLocalId ?: UUID.randomUUID().toString(),
        remoteId = metaId,
        nombre = nombre,
        contribucionMensual = contribucionMensual,
        monto = monto,
        fecha = fecha ?: "",
        emoji = emoji,
        usuarioId = usuarioId.toString(),
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )

fun MetasEntity.toDomain(): Metas =
    Metas(
        metaId = metaId,
        remoteId = remoteId,
        nombre = nombre,
        contribucionMensual = contribucionMensual,
        monto = monto,
        fecha = fecha,
        Imagenes = emptyList(),
        emoji = emoji ?: "",
        usuarioId = usuarioId
    )

fun Metas.toEntity(): MetasEntity =
    MetasEntity(
        metaId = metaId,
        remoteId = remoteId,
        nombre = nombre,
        contribucionMensual = contribucionMensual,
        monto = monto,
        fecha = fecha,
        emoji = emoji,
        usuarioId = usuarioId,
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )
