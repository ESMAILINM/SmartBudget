package edu.ucne.smartbudget.data.mapper

import edu.ucne.smartbudget.data.local.entities.IngresosEntity
import edu.ucne.smartbudget.data.remote.dto.ingresosdto.IngresoRequest
import edu.ucne.smartbudget.data.remote.dto.ingresosdto.IngresoResponse
import edu.ucne.smartbudget.domain.model.Ingresos
import java.util.UUID

fun IngresosEntity.toDomain() = Ingresos(
    ingresoId = ingresoId,
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

fun Ingresos.toEntity() = IngresosEntity(
    ingresoId = ingresoId,
    remoteId = remoteId,
    descripcion = descripcion ?: "",
    monto = monto,
    fecha = fecha,
    categoriaId = categoriaId,
    usuarioId = usuarioId,
    isPendingCreate = isPendingCreate,
    isPendingUpdate = isPendingUpdate,
    isPendingDelete = isPendingDelete
)

fun Ingresos.toRequest(
    mappedUsuarioId: Int,
    mappedCategoriaId: Int
): IngresoRequest {
    return IngresoRequest(
        monto = monto,
        fecha = fecha,
        descripcion = descripcion,
        usuarioId = mappedUsuarioId,
        categoriaId = mappedCategoriaId
    )
}

fun IngresoResponse.toEntity(localId: String) = IngresosEntity(
    ingresoId = localId,
    remoteId = ingresoId,
    descripcion = descripcion ?: "",
    monto = monto,
    fecha = fecha,
    categoriaId = "",
    usuarioId = "",
    isPendingCreate = false,
    isPendingUpdate = false,
    isPendingDelete = false
)

fun IngresoResponse.toEntity() = IngresosEntity(
    ingresoId = UUID.randomUUID().toString(),
    remoteId = ingresoId,
    descripcion = descripcion ?: "",
    monto = monto,
    fecha = fecha,
    categoriaId = "",
    usuarioId = "",
    isPendingCreate = false,
    isPendingUpdate = false,
    isPendingDelete = false
)

fun IngresoResponse.toDomain() = Ingresos(
    ingresoId = UUID.randomUUID().toString(),
    remoteId = ingresoId,
    descripcion = descripcion,
    monto = monto,
    fecha = fecha,
    categoriaId = "",
    usuarioId = "",
    isPendingCreate = false,
    isPendingUpdate = false,
    isPendingDelete = false
)
