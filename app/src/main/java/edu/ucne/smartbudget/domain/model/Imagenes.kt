package edu.ucne.smartbudget.domain.model

import java.util.UUID

data class Imagenes(
    val imagenId: String = UUID.randomUUID().toString(),
    val remoteId: Int? = null,
    val metaId: String,
    val url: String,
    val localUrl: String,
    val isPendingCreate: Boolean = false,
    val isPendingUpdate: Boolean = false,
    val isPendingDelete: Boolean = false
)
