package edu.ucne.smartbudget.presentation.ingreso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.smartbudget.data.local.datastore.SessionDataStore
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Ingresos
import edu.ucne.smartbudget.domain.usecase.categoriasusecase.ObserveCategoriasUseCase
import edu.ucne.smartbudget.domain.usecase.ingresosusecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class IngresoViewModel @Inject constructor(
    private val observeIngresosUseCase: ObserveIngresosUseCase,
    private val observeCategoriasUseCase: ObserveCategoriasUseCase,
    private val insertIngresoUseCase: InsertIngresoUseCase,
    private val updateIngresoUseCase: UpdateIngresoUseCase,
    private val deleteIngresoUseCase: DeleteIngresoUseCase,
    private val triggerSyncUseCase: TriggerSyncIngresosUseCase,
    private val sessionDataStore: SessionDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(IngresoUiState(isLoading = true))
    val state: StateFlow<IngresoUiState> = _state.asStateFlow()

    val selectedCurrency: StateFlow<String> = sessionDataStore.currencyFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "USD"
        )

    private var currentUserId: String = ""

    init {
        viewModelScope.launch {
            sessionDataStore.userIdFlow.collectLatest { userId ->
                if (!userId.isNullOrBlank()) {
                    currentUserId = userId
                    observeIngresos(userId)
                    try { triggerSyncUseCase() } catch (_: Exception) {}
                }
            }
        }
        observeCategorias()
    }

    private fun reduce(block: (IngresoUiState) -> IngresoUiState) {
        _state.update(block)
    }

    fun onEvent(event: IngresoUiEvent) {
        when (event) {
            is IngresoUiEvent.MontoChanged ->
                reduce { it.copy(monto = event.monto) }

            is IngresoUiEvent.DescripcionChanged ->
                reduce { it.copy(descripcion = event.descripcion) }

            is IngresoUiEvent.FechaChanged ->
                reduce { it.copy(fecha = event.fecha) }

            is IngresoUiEvent.CategoriaIdChanged -> {
                val cat = _state.value.categorias.firstOrNull { it.categoriaId == event.categoriaId }
                reduce { it.copy(categoriaId = event.categoriaId, categoriaSeleccionada = cat) }
            }

            is IngresoUiEvent.Edit -> loadIngreso(event.ingreso)

            is IngresoUiEvent.Delete -> deleteIngreso(event.id)

            IngresoUiEvent.Save -> saveIngreso()

            IngresoUiEvent.ShowDialog -> {
                resetForm()
                reduce { it.copy(showDialog = true) }
            }

            IngresoUiEvent.HideDialog -> resetForm()

            else -> {}
        }
    }

    private fun observeIngresos(userId: String) = viewModelScope.launch {
        observeIngresosUseCase(userId)
            .onStart { reduce { it.copy(isLoading = true) } }
            .catch { e -> reduce { it.copy(isLoading = false, userMessage = e.message) } }
            .collect { ingresos ->
                reduce { it.copy(ingresos = ingresos, isLoading = false) }
            }
    }

    private fun observeCategorias() = viewModelScope.launch {
        observeCategoriasUseCase()
            .onStart { reduce { it.copy(isLoading = true) } }
            .catch { e -> reduce { it.copy(isLoading = false, userMessage = e.message) } }
            .collect { categorias ->
                reduce { it.copy(categorias = categorias) }
            }
    }

    private fun loadIngreso(ingreso: Ingresos) {
        reduce {
            it.copy(
                descripcion = ingreso.descripcion ?: "",
                monto = ingreso.monto.toString(),
                fecha = ingreso.fecha,
                categoriaId = ingreso.categoriaId,
                categoriaSeleccionada = it.categorias.firstOrNull { c -> c.categoriaId == ingreso.categoriaId },
                editingId = ingreso.ingresoId,
                showDialog = true
            )
        }
    }

    private fun saveIngreso() = viewModelScope.launch {
        val s = _state.value
        val categoria = s.categoriaSeleccionada ?: return@launch

        if (currentUserId.isBlank()) {
            reduce { it.copy(userMessage = "Usuario no identificado") }
            return@launch
        }

        val ingreso = Ingresos(
            ingresoId = s.editingId ?: UUID.randomUUID().toString(),
            remoteId = if (s.editingId != null)
                s.ingresos.find { it.ingresoId == s.editingId }?.remoteId else null,
            monto = s.monto.toDoubleOrNull() ?: 0.0,
            descripcion = s.descripcion,
            fecha = s.fecha,
            categoriaId = categoria.categoriaId,
            usuarioId = currentUserId,
        )

        val result = if (s.editingId == null)
            insertIngresoUseCase(ingreso)
        else updateIngresoUseCase(ingreso)

        when (result) {
            is Resource.Success -> {
                reduce { it.copy(userMessage = "Ingreso guardado", showDialog = false) }
                resetForm()
                try { triggerSyncUseCase() } catch (_: Exception) {}
            }
            is Resource.Error ->
                reduce { it.copy(userMessage = "Error al guardar") }

            else -> {}
        }
    }

    private fun deleteIngreso(id: String) = viewModelScope.launch {
        when (deleteIngresoUseCase(id)) {
            is Resource.Success -> {
                reduce { it.copy(userMessage = "Ingreso eliminado") }
                try { triggerSyncUseCase() } catch (_: Exception) {}
            }
            is Resource.Error ->
                reduce { it.copy(userMessage = "Error al eliminar") }

            else -> {}
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            try { triggerSyncUseCase() } catch (_: Exception) {}
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun resetForm() {
        reduce {
            it.copy(
                descripcion = "",
                monto = "",
                fecha = "",
                categoriaId = "",
                categoriaSeleccionada = null,
                editingId = null,
                showDialog = false
            )
        }
    }
}
