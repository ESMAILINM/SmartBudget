package edu.ucne.smartbudget.domain.model

import java.util.UUID

data class Usuarios (
    val usuarioId: String = UUID.randomUUID().toString(),
    val remoteId: Int? = null,
    val userName: String,
    val password: String,
    val isPendingCreate: Boolean = false,
    val isPendingUpdate: Boolean = false,
    val isPendingDelete: Boolean = false
)