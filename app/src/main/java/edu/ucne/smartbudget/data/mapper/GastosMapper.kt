package edu.ucne.smartbudget.data.mapper

import edu.ucne.smartbudget.data.local.entities.GastosEntity
import edu.ucne.smartbudget.data.remote.dto.gastosdto.GastoRequest
import edu.ucne.smartbudget.data.remote.dto.gastosdto.GastoResponse
import edu.ucne.smartbudget.domain.model.Gastos
import java.util.UUID

fun GastosEntity.toDomain(): Gastos =
    Gastos(
        gastoId = gastoId,
        remoteId = remoteId,
        monto = monto,
        fecha = fecha,
        descripcion = descripcion,
        categoriaId = categoriaId,
        usuarioId = usuarioId
    )

fun Gastos.toEntity(): GastosEntity =
    GastosEntity(
        gastoId = gastoId,
        remoteId = remoteId,
        monto = monto,
        fecha = fecha,
        descripcion = descripcion,
        categoriaId = categoriaId,
        usuarioId = usuarioId,
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )

fun Gastos.toRequest(categoriaRemoteId: Int, usuarioRemoteId: Int): GastoRequest =
    GastoRequest(
        descripcion = descripcion,
        fecha = fecha,
        categoriaId = categoriaRemoteId,
        monto = monto,
        usuarioId = usuarioRemoteId
    )

fun GastoResponse.toEntity(
    currentLocalId: String? = null,
    categoriaLocalId: String? = null,
    usuarioLocalId: String? = null
): GastosEntity =
    GastosEntity(
        gastoId = currentLocalId ?: UUID.randomUUID().toString(),
        remoteId = gastoId,
        monto = monto,
        fecha = fecha,
        descripcion = descripcion,
        categoriaId = categoriaLocalId ?: categoriaId.toString(),
        usuarioId = usuarioLocalId ?: usuarioId.toString(),
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )

fun GastoResponse.toDomain(
    currentLocalId: String? = null,
    categoriaLocalId: String? = null,
    usuarioLocalId: String? = null
): Gastos =
    Gastos(
        gastoId = currentLocalId ?: UUID.randomUUID().toString(),
        remoteId = gastoId,
        monto = monto,
        fecha = fecha,
        descripcion = descripcion,
        categoriaId = categoriaLocalId ?: categoriaId.toString(),
        usuarioId = usuarioLocalId ?: usuarioId.toString()
    )
