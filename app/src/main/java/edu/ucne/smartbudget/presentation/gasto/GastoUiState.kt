package edu.ucne.smartbudget.presentation.gasto

import edu.ucne.smartbudget.domain.model.Categorias
import edu.ucne.smartbudget.domain.model.Gastos

data class GastoUiState(
    val gastosListOriginal: List<Gastos> = emptyList(),
    val gastosList: List<Gastos> = emptyList(),
    val categorias: List<Categorias> = emptyList(),
    val totalGastos: Double = 0.0,
    val categoriasData: List<CategoryData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    val monto: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val categoriaSeleccionadaId: String? = null,
    val categoriaSeleccionadaNombre: String? = null,
    val montoOrden: String = "",
    val showDialog: Boolean = false,
    val userMessage: String? = null,
    val isRefreshing: Boolean = false
)
