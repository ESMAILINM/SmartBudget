package edu.ucne.smartbudget.data.mapper

import edu.ucne.smartbudget.data.local.entities.UsuariosEntity
import edu.ucne.smartbudget.data.remote.dto.usuariosdto.UsuarioRequest
import edu.ucne.smartbudget.data.remote.dto.usuariosdto.UsuarioResponse
import edu.ucne.smartbudget.domain.model.Usuarios
import java.util.UUID

fun Usuarios.toRequest(): UsuarioRequest =
    UsuarioRequest(
        userName = userName,
        password = password
    )

fun Usuarios.toEntity(
    isPendingCreate: Boolean = false,
    isPendingUpdate: Boolean = false,
    isPendingDelete: Boolean = false
): UsuariosEntity =
    UsuariosEntity(
        usuarioId = usuarioId,
        remoteId = remoteId,
        userName = userName,
        password = password,
        isPendingCreate = isPendingCreate,
        isPendingUpdate = isPendingUpdate,
        isPendingDelete = isPendingDelete
    )

fun UsuariosEntity.toDomain(): Usuarios =
    Usuarios(
        usuarioId = usuarioId,
        remoteId = remoteId,
        userName = userName,
        password = password,
        isPendingCreate = isPendingCreate,
        isPendingUpdate = isPendingUpdate,
        isPendingDelete = isPendingDelete
    )

fun UsuarioResponse.toEntity(localUuid: String? = null): UsuariosEntity =
    UsuariosEntity(
        usuarioId = localUuid ?: UUID.randomUUID().toString(),
        remoteId = this.usuarioId,
        userName = this.userName,
        password = "",
        isPendingCreate = false,
        isPendingUpdate = false,
        isPendingDelete = false
    )

fun UsuarioResponse.toDomain(localUuid: String): Usuarios =
    Usuarios(
        usuarioId = localUuid,
        remoteId = this.usuarioId,
        userName = this.userName,
        password = ""
    )
