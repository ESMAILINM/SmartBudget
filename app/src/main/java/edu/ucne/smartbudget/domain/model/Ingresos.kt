package edu.ucne.smartbudget.domain.model

import java.util.UUID

data class Ingresos(
    val ingresoId: String = UUID.randomUUID().toString(),
    val remoteId: Int? = null,
    val monto: Double,
    val fecha: String,
    val descripcion: String? = null,
    val categoriaId: String,
    val usuarioId: String,
    val isPendingCreate: Boolean = false,
    val isPendingUpdate: Boolean = false,
    val isPendingDelete: Boolean = false
)
