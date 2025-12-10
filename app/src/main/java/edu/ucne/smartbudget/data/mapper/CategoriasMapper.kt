package edu.ucne.smartbudget.data.mapper

import edu.ucne.smartbudget.data.local.entities.CategoriasEntity
import edu.ucne.smartbudget.data.remote.dto.categoriasdto.CategoriaRequest
import edu.ucne.smartbudget.data.remote.dto.categoriasdto.CategoriaResponse
import edu.ucne.smartbudget.domain.model.Categorias
import java.util.UUID

fun CategoriasEntity.toDomain(): Categorias =
    Categorias(
        categoriaId = categoriaId,
        remoteId = remoteId,
        nombre = nombre,
        tipoId = tipoId,
        isPendingCreate = isPendingCreate,
        isPendingUpdate = isPendingUpdate,
        isPendingDelete = isPendingDelete
    )

fun Categorias.toEntity(): CategoriasEntity =
    CategoriasEntity(
        categoriaId = categoriaId,
        remoteId = remoteId,
        nombre = nombre,
        tipoId = tipoId,
        isPendingCreate = isPendingCreate,
        isPendingUpdate = isPendingUpdate,
        isPendingDelete = isPendingDelete
    )

fun Categorias.toRequest(): CategoriaRequest =
    CategoriaRequest(
        nombre = nombre,
        tipoId = tipoId
    )

fun CategoriaResponse.toEntity(currentLocalId: String? = null): CategoriasEntity =
    CategoriasEntity(
        categoriaId = currentLocalId ?: UUID.randomUUID().toString(),
        remoteId = categoriaId,
        nombre = nombre,
        tipoId = tipoId,
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )

fun CategoriaResponse.toDomain(currentLocalId: String? = null): Categorias =
    Categorias(
        categoriaId = currentLocalId ?: UUID.randomUUID().toString(),
        remoteId = categoriaId,
        nombre = nombre,
        tipoId = tipoId,
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )
