package edu.ucne.smartbudget.data.mapper

import edu.ucne.smartbudget.data.local.entities.IngresosEntity
import edu.ucne.smartbudget.data.remote.dto.ingresosdto.IngresoRequest
import edu.ucne.smartbudget.data.remote.dto.ingresosdto.IngresoResponse
import edu.ucne.smartbudget.domain.model.Ingresos
import java.util.UUID

fun IngresosEntity.toDomain(): Ingresos =
    Ingresos(
        ingresoId = ingresoId,
        remoteId = remoteId,
        monto = monto,
        fecha = fecha,
        descripcion = descripcion.takeIf { it.isNotEmpty() },
        categoriaId = categoriaId,
        usuarioId = usuarioId
    )

fun Ingresos.toEntity(): IngresosEntity =
    IngresosEntity(
        ingresoId = ingresoId,
        remoteId = remoteId,
        monto = monto,
        fecha = fecha,
        descripcion = descripcion ?: "",
        categoriaId = categoriaId,
        usuarioId = usuarioId,
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )

fun Ingresos.toRequest(): IngresoRequest =
    IngresoRequest(
        descripcion = descripcion,
        fecha = fecha,
        categoriaId = categoriaId.toInt(),
        monto = monto,
        usuarioId = usuarioId.toInt()
    )

fun IngresoResponse.toEntity(
    currentLocalId: String? = null,
    categoriaLocalId: String? = null,
    usuarioLocalId: String? = null
): IngresosEntity =
    IngresosEntity(
        ingresoId = currentLocalId ?: UUID.randomUUID().toString(),
        remoteId = ingresoId,
        monto = monto,
        fecha = fecha,
        descripcion = descripcion ?: "",
        categoriaId = categoriaLocalId ?: categoriaId.toString(),
        usuarioId = usuarioLocalId ?: usuarioId.toString(),
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )

fun IngresoResponse.toDomain(
    currentLocalId: String? = null,
    categoriaLocalId: String? = null,
    usuarioLocalId: String? = null
): Ingresos =
    Ingresos(
        ingresoId = currentLocalId ?: UUID.randomUUID().toString(),
        remoteId = ingresoId,
        monto = monto,
        fecha = fecha,
        descripcion = descripcion,
        categoriaId = categoriaLocalId ?: categoriaId.toString(),
        usuarioId = usuarioLocalId ?: usuarioId.toString()
    )
