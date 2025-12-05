package edu.ucne.smartbudget.data.remote.dto.categoriasdto

data class CategoriaResponse (
    val categoriaId: Int,
    val nombre: String,
    val tipoId: Int,
    val tipoNombre: String
)