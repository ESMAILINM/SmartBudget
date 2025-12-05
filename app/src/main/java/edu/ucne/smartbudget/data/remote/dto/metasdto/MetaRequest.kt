package edu.ucne.smartbudget.data.remote.dto.metasdto

import edu.ucne.smartbudget.data.remote.dto.imagenesdto.ImagenRequest

data class MetaRequest(
    val nombre: String,
    val fecha: String?,
    val monto: Double,
    val contribucionMensual: Double,
    val emoji: String?,
    val imagenes: List<ImagenRequest> = emptyList(),
    val usuarioId: Int
)
