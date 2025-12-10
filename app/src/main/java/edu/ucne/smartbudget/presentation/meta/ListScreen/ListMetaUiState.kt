package edu.ucne.smartbudget.presentation.meta.ListScreen

import edu.ucne.smartbudget.domain.model.Metas

data class ListMetaUiState(
    val metas: List<Metas> = emptyList(),
    val icono : String = "",
    val nombre : String = "",
    val fecha : String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false

)
