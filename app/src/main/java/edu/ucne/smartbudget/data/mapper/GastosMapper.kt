package edu.ucne.smartbudget.data.mapper

import edu.ucne.smartbudget.data.local.entities.GastosEntity
import edu.ucne.smartbudget.data.remote.dto.gastosdto.GastoRequest
import edu.ucne.smartbudget.data.remote.dto.gastosdto.GastoResponse
import edu.ucne.smartbudget.domain.model.Gastos
import java.util.UUID


fun GastosEntity.toDomain() = Gastos(
    gastoId = gastoId,
    remoteId = remoteId,
    descripcion = descripcion,
    monto = monto,
    fecha = fecha,
    categoriaId = categoriaId,
    usuarioId = usuarioId,
    isPendingCreate = isPendingCreate,
    isPendingUpdate = isPendingUpdate,
    isPendingDelete = isPendingDelete
)

fun Gastos.toEntity() = GastosEntity(
    gastoId = gastoId,
    remoteId = remoteId,
    descripcion = descripcion,
    monto = monto,
    fecha = fecha,
    categoriaId = categoriaId,
    usuarioId = usuarioId,
    isPendingCreate = isPendingCreate,
    isPendingUpdate = isPendingUpdate,
    isPendingDelete = isPendingDelete
)

fun Gastos.toRequest(
    mappedUsuarioId: Int,
    mappedCategoriaId: Int
): GastoRequest {
    return GastoRequest(
        monto = monto,
        fecha = fecha,
        descripcion = descripcion,
        usuarioId = mappedUsuarioId,
        categoriaId = mappedCategoriaId
    )
}

fun GastoResponse.toEntity(localId: String) = GastosEntity(
    gastoId = localId,
    remoteId = gastoId,
    descripcion = descripcion,
    monto = monto,
    fecha = fecha,
    categoriaId = "",
    usuarioId = "",
    isPendingCreate = false,
    isPendingUpdate = false,
    isPendingDelete = false
)

fun GastoResponse.toEntity() = GastosEntity(
    gastoId = UUID.randomUUID().toString(),
    remoteId = gastoId,
    descripcion = descripcion,
    monto = monto,
    fecha = fecha,
    categoriaId = "",
    usuarioId = "",
    isPendingCreate = false,
    isPendingUpdate = false,
    isPendingDelete = false
)

fun GastoResponse.toDomain() = Gastos(
    gastoId = UUID.randomUUID().toString(),
    remoteId = gastoId,
    descripcion = descripcion,
    monto = monto,
    fecha = fecha,
    categoriaId = "",
    usuarioId = "",
    isPendingCreate = false,
    isPendingUpdate = false,
    isPendingDelete = false
)
