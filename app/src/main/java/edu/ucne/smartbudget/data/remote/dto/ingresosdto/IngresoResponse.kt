package edu.ucne.smartbudget.data.remote.dto.ingresosdto

data class IngresoResponse(
    val ingresoId: Int,
    val descripcion: String?,
    val fecha: String,
    val categoriaId: Int,
    val categoriaNombre: String?,
    val monto: Double,
    val usuarioId: Int
)
