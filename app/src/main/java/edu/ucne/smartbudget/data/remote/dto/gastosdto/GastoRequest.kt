package edu.ucne.smartbudget.data.remote.dto.gastosdto

data class GastoRequest(
    val descripcion: String?,
    val fecha: String,
    val categoriaId: Int,
    val monto: Double,
    val usuarioId: Int
)
