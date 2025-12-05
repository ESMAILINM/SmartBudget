package edu.ucne.smartbudget.domain.model

import java.util.UUID

data class Categorias(
    val categoriaId: String = UUID.randomUUID().toString(),
    val remoteId: Int?,
    val nombre: String,
    val tipoId: Int
)
