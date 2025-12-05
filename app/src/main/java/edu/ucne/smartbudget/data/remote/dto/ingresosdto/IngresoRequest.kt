package edu.ucne.smartbudget.data.remote.dto.ingresosdto

data class IngresoRequest (
    val descripcion: String?,
    val fecha: String,
    val categoriaId: Int,
    val monto: Double,
    val usuarioId: Int
)