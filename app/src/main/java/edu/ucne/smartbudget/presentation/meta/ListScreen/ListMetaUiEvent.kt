package edu.ucne.smartbudget.presentation.meta.ListScreen


sealed class ListMetaUiEvent {
    object Load : ListMetaUiEvent()
    object Refresh : ListMetaUiEvent()

}
