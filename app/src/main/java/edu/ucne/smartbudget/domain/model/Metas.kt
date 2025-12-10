package edu.ucne.smartbudget.domain.model

import java.util.UUID

data class Metas(
    val metaId: String = UUID.randomUUID().toString(),
    val remoteId: Int? = null,
    val nombre: String,
    val contribucionMensual: Double,
    val monto: Double,
    val fecha: String,
    val imagenes: List<Imagenes>,
    val emoji : String,
    val usuarioId: String,
    val isPendingCreate: Boolean = false,
    val isPendingUpdate: Boolean = false,
    val isPendingDelete: Boolean = false
)
