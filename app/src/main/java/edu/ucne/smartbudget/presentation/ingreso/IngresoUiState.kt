package edu.ucne.smartbudget.presentation.ingreso

import edu.ucne.smartbudget.domain.model.Ingresos
import edu.ucne.smartbudget.domain.model.Categorias
data class IngresoUiState(
    val isLoading: Boolean = false,    val ingresos: List<Ingresos> = emptyList(),
    val userMessage: String? = null,
    val showDialog: Boolean = false,
    val descripcion: String = "",
    val fecha: String = "",
    val remoteId: Int? = null,
    val isSaving: Boolean = false,
    val monto: String = "",
    val categoriaId: String = "",
    val categorias: List<Categorias> = emptyList(),
    val categoriaSeleccionada: Categorias? = null,
    val usuarioId: String = "",
    val editingId: String? = null,
    val isRefreshing: Boolean = false
)
