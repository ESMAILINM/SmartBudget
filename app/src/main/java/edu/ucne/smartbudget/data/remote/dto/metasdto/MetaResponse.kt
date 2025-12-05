package edu.ucne.smartbudget.data.remote.dto.metasdto

import edu.ucne.smartbudget.data.remote.dto.imagenesdto.ImagenResponse

data class MetaResponse (
    val metaId: Int,
    val nombre: String,
    val fecha: String?,
    val monto: Double,
    val contribucionMensual: Double,
    val imagenes: List<ImagenResponse>?,
    val emoji: String?,
    val usuarioId: Int
)