package edu.ucne.smartbudget.presentation.categoria

import edu.ucne.smartbudget.domain.model.Categorias

sealed class CategoriaUiEvent {

    data class Success(val message: String) : CategoriaUiEvent()

    data class Error(val message: String) : CategoriaUiEvent()

    data class OnTipoFilterChange(val tipoId: Int) : CategoriaUiEvent()

    data class AddCategoria(val nombre: String, val tipoId: Int) : CategoriaUiEvent()

    data class EditCategoria(val categoria: Categorias) : CategoriaUiEvent()

    data class DeleteCategoria(val categoria: Categorias) : CategoriaUiEvent()

    object Refresh : CategoriaUiEvent()

    object ShowDialog : CategoriaUiEvent()

    object HideDialog : CategoriaUiEvent()

    object UserMessageShown : CategoriaUiEvent()

    data class OnSearchQueryChange(val query: String) : CategoriaUiEvent()
}
