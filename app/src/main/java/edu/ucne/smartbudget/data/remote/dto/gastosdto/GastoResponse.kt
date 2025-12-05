package edu.ucne.smartbudget.data.remote.dto.gastosdto

data class GastoResponse(
    val gastoId: Int,
    val descripcion: String?,
    val fecha: String,
    val categoriaId: Int,
    val categoriaNombre: String,
    val monto: Double,
    val usuarioId: Int
)
