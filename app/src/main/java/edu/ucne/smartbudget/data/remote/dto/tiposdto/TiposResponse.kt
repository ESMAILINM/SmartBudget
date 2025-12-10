package edu.ucne.smartbudget.data.remote.dto.tiposdto

import edu.ucne.smartbudget.data.remote.dto.categoriasdto.CategoriaResponse

data class TiposResponse (
    val tipoId: Int,
    val nombre: String?,
    val categorias: List<CategoriaResponse>?
)