package edu.ucne.smartbudget.data.remote.mapper

import edu.ucne.smartbudget.data.remote.dto.UsuariosDto
import edu.ucne.smartbudget.domain.model.Usuarios

fun UsuariosDto.toDomain(): Usuarios = Usuarios(
        usuarioId = usuarioId,
        userName = userName,
        password = password
    )

fun Usuarios.toDto(): UsuariosDto = UsuariosDto(
        usuarioId = usuarioId,
        userName = userName,
        password = password

)