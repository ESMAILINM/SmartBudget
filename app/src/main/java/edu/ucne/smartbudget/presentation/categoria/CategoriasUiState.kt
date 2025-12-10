package edu.ucne.smartbudget.presentation.categoria

import edu.ucne.smartbudget.domain.model.Categorias

data class CategoriasUiState(
    val isLoading: Boolean = false,

    val isRefreshing: Boolean = false,

    val userMessage: String? = null,

    val showDialog: Boolean = false,

    val categorias: List<Categorias> = emptyList(),

    val currentFilterTipoId: Int = 2,

    val nombre: String = "",

    val searchQuery: String = "",

    val filteredCategorias: List<Categorias> = emptyList(),

    val tipoId: Int = 1,

    val categoriaSeleccionada: Categorias? = null
)
