package edu.ucne.smartbudget.presentation.gasto

data class CategoryData(
    val categoriaId: String,
    val nombre: String,
    val tipoId: Int,
    val total: Double,
    val porcentaje: Float
)