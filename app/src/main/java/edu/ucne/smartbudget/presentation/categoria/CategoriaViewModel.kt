package edu.ucne.smartbudget.presentation.categoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Categorias
import edu.ucne.smartbudget.domain.usecase.categoriasusecase.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriaViewModel @Inject constructor(
    private val observeCategoriasUseCase: ObserveCategoriasUseCase,
    private val insertCategoriaUseCase: InsertCategoriaUseCase,
    private val updateCategoriaUseCase: UpdateCategoriaUseCase,
    private val deleteCategoriaUseCase: DeleteCategoriaUseCase,
    private val triggerSyncUseCase: TriggerSyncCategoriaUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CategoriasUiState(isLoading = true))
    val state: StateFlow<CategoriasUiState> = _state.asStateFlow()

    init {
        observeCategorias()
    }

    fun onEvent(event: CategoriaUiEvent) {
        when (event) {
            is CategoriaUiEvent.AddCategoria -> crearCategoria(event.nombre, event.tipoId)
            is CategoriaUiEvent.EditCategoria -> editarCategoria(event.categoria)
            is CategoriaUiEvent.DeleteCategoria -> eliminarCategoria(event.categoria.categoriaId)
            is CategoriaUiEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
                applyFilters()
            }
            is CategoriaUiEvent.OnTipoFilterChange -> {
                _state.update { it.copy(currentFilterTipoId = event.tipoId) }
                applyFilters()
            }
            CategoriaUiEvent.ShowDialog -> _state.update { it.copy(showDialog = true) }
            CategoriaUiEvent.HideDialog -> _state.update { it.copy(showDialog = false, nombre = "", tipoId = 1) }
            CategoriaUiEvent.UserMessageShown -> limpiarMensaje()
            CategoriaUiEvent.Refresh -> refresh()
            else -> {}
        }
    }

    private fun applyFilters() {
        _state.update { current ->
            val search = current.searchQuery.trim().lowercase()
            val tipoId = current.currentFilterTipoId

            val filtered = current.categorias.filter { categoria ->
                val matchesType = categoria.tipoId == tipoId
                val matchesSearch = search.isBlank() || categoria.nombre.lowercase().contains(search)
                matchesType && matchesSearch
            }

            current.copy(filteredCategorias = filtered)
        }
    }

    private fun observeCategorias() {
        observeCategoriasUseCase()
            .onEach { list ->
                _state.update { it.copy(categorias = list, isLoading = false) }
                applyFilters()
            }
            .launchIn(viewModelScope)
    }

    private fun crearCategoria(nombre: String, tipoId: Int) = viewModelScope.launch {
        val categoria = Categorias(nombre = nombre, tipoId = tipoId, remoteId = 0)
        when (val result = insertCategoriaUseCase(categoria)) {
            is Resource.Success -> {
                _state.update {
                    it.copy(userMessage = "¡Categoría guardada exitosamente!", showDialog = false, nombre = "")
                }
                triggerSyncUseCase()
            }
            is Resource.Error -> _state.update { it.copy(userMessage = "Error al guardar: ${result.message}") }
            else -> {}
        }
    }

    private fun editarCategoria(categoria: Categorias) = viewModelScope.launch {
        when (val result = updateCategoriaUseCase(categoria)) {
            is Resource.Success -> {
                _state.update { it.copy(userMessage = "¡Categoría actualizada!", showDialog = false) }
                triggerSyncUseCase()
            }
            is Resource.Error -> _state.update { it.copy(userMessage = "Error al actualizar: ${result.message}") }
            else -> {}
        }
    }

    private fun eliminarCategoria(id: String) = viewModelScope.launch {
        when (val result = deleteCategoriaUseCase(id)) {
            is Resource.Success -> {
                _state.update { it.copy(userMessage = "Categoría eliminada con éxito") }
                triggerSyncUseCase()
            }
            is Resource.Error -> _state.update { it.copy(userMessage = "Error al eliminar: ${result.message}") }
            else -> {}
        }
        limpiarMensaje()
    }

    private fun refresh() = viewModelScope.launch {
        _state.update { it.copy(isRefreshing = true) }
        triggerSyncUseCase()
        delay(1000)
        _state.update { it.copy(isRefreshing = false) }
    }

    private fun limpiarMensaje() {
        _state.update { it.copy(userMessage = null) }
    }
}
