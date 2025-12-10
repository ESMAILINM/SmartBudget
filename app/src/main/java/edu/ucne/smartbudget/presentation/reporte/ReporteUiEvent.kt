package edu.ucne.smartbudget.presentation.reporte

sealed interface ReporteUiEvent {
    data class SelectTypeFilter(val filter: String) : ReporteUiEvent
    data class SelectDateFilter(val filter: String) : ReporteUiEvent
    data object OnClose : ReporteUiEvent
    data object ToggleShowAll : ReporteUiEvent

}
