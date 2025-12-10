package edu.ucne.smartbudget.presentation.gasto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.smartbudget.data.local.datastore.SessionDataStore
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Categorias
import edu.ucne.smartbudget.domain.model.Gastos
import edu.ucne.smartbudget.domain.usecase.categoriasusecase.ObserveCategoriasUseCase
import edu.ucne.smartbudget.domain.usecase.gastosusecase.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GastoViewModel @Inject constructor(
    private val observeGastosUseCase: ObserveGastosUseCase,
    private val insertGastoUseCase: InsertGastoUseCase,
    private val deleteGastoUseCase: DeleteGastoUseCase,
    private val triggerSyncGastosUseCase: TriggerSyncGastosUseCase,
    private val observeCategoriasUseCase: ObserveCategoriasUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(GastoUiState(isLoading = true))
    val state: StateFlow<GastoUiState> = _state.asStateFlow()

    val selectedCurrency = sessionDataStore.currencyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "USD")

    private var currentUserId: String = ""
    private var currentGastoId: String? = null

    init {
        viewModelScope.launch {
            sessionDataStore.userIdFlow.collectLatest { userId ->
                if (!userId.isNullOrBlank()) {
                    currentUserId = userId
                    observeLocalData(userId)
                    syncRemoteData()
                } else {
                    _state.update { it.copy(gastosList = emptyList(), gastosListOriginal = emptyList()) }
                }
            }
        }
    }

    private fun observeLocalData(userId: String) {
        viewModelScope.launch {
            val gastosFlow = observeGastosUseCase(userId)
            val categoriasFlow = observeCategoriasUseCase()
                .map { it.filter { cat -> cat.tipoId == 2 } }

            combine(gastosFlow, categoriasFlow) { gastos, categorias ->
                gastos.filter { it.usuarioId == userId } to categorias
            }
                .catch { e ->
                    _state.update { it.copy(userMessage = e.message, isLoading = false) }
                }
                .collect { (gastos, categorias) ->
                    val total = gastos.sumOf { it.monto }
                    val stats = calcularEstadisticas(gastos, total, categorias)

                    _state.update {
                        it.copy(
                            gastosListOriginal = gastos,
                            totalGastos = total,
                            categoriasData = stats,
                            categorias = categorias,
                            isLoading = false
                        )
                    }
                    if (currentGastoId != null) {
                        val gastoEncontrado = gastos.find { it.gastoId == currentGastoId }
                        if (gastoEncontrado != null) {
                            rellenarFormulario(gastoEncontrado)
                        }
                    }

                    aplicarFiltros()
                }
        }
    }

    private fun syncRemoteData() {
        viewModelScope.launch {
            try {
                if (_state.value.gastosListOriginal.isEmpty()) {
                    _state.update { it.copy(isLoading = true) }
                }
                triggerSyncGastosUseCase()
            } catch (_: Exception) {
                _state.update { it.copy(userMessage = "Modo Offline: Sincronización fallida") }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEvent(event: GastoUiEvent) {
        when (event) {
            is GastoUiEvent.ShowDialog -> _state.update { it.copy(showDialog = true) }
            is GastoUiEvent.HideDialog -> clearDialog()
            is GastoUiEvent.MontoChanged -> update { it.copy(monto = event.monto) }
            is GastoUiEvent.DescripcionChanged -> update { it.copy(descripcion = event.descripcion) }
            is GastoUiEvent.CategoriaIdChanged -> updateAndFilter { it.copy(categoriaSeleccionadaId = event.categoriaId) }
            is GastoUiEvent.CategoriaSelected -> {
                val catId = _state.value.categorias.find { it.nombre == event.categoria }?.categoriaId
                updateAndFilter {
                    it.copy(
                        categoriaSeleccionadaId = catId,
                        categoriaSeleccionadaNombre = event.categoria
                    )
                }
            }
            is GastoUiEvent.FechaChanged -> updateAndFilter { it.copy(fecha = event.fecha) }
            is GastoUiEvent.MontoOrdenChanged -> updateAndFilter { it.copy(montoOrden = event.orden) }
            is GastoUiEvent.Save -> saveGasto()
            is GastoUiEvent.Delete -> deleteGasto(event.gastoId)
            is GastoUiEvent.SyncGastos -> syncRemoteData()
            is GastoUiEvent.Edit -> loadGasto(event.gastoId)
            else -> {}
        }
    }

    private fun loadGasto(id: String) {
        currentGastoId = id

        val gasto = _state.value.gastosListOriginal.find { it.gastoId == id }
        if (gasto != null) {
            rellenarFormulario(gasto)
        }
    }
    private fun rellenarFormulario(gasto: Gastos) {
        val catNombre = _state.value.categorias.find { it.categoriaId == gasto.categoriaId }?.nombre

        _state.update {
            it.copy(
                showDialog = true,
                monto = gasto.monto.toString(),
                descripcion = gasto.descripcion ?: "",
                categoriaSeleccionadaId = gasto.categoriaId,
                categoriaSeleccionadaNombre = catNombre,
                fecha = gasto.fecha
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            try {
                triggerSyncGastosUseCase()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun saveGasto() = viewModelScope.launch {
        val ui = _state.value
        val montoDouble = ui.monto.toDoubleOrNull() ?: 0.0

        if (montoDouble <= 0) {
            _state.update { it.copy(userMessage = "El monto debe ser mayor a 0") }
            return@launch
        }
        if (currentUserId.isBlank()) {
            _state.update { it.copy(userMessage = "Usuario no identificado") }
            return@launch
        }

        val catId = ui.categoriaSeleccionadaId ?: ""
        if (catId.isEmpty()) {
            _state.update { it.copy(userMessage = "Seleccione una categoría") }
            return@launch
        }

        _state.update { it.copy(isLoading = true) }

        val previous = currentGastoId?.let { id ->
            _state.value.gastosListOriginal.find { it.gastoId == id }
        }

        val gasto = Gastos(
            gastoId = currentGastoId ?: UUID.randomUUID().toString(),
            monto = montoDouble,
            descripcion = ui.descripcion,
            categoriaId = catId,
            fecha = ui.fecha.ifBlank {
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            },
            usuarioId = currentUserId,
            remoteId = previous?.remoteId ?: 0
        )

        when (val result = insertGastoUseCase(gasto)) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        userMessage = "Guardado",
                        isLoading = false,
                        isSaved = true
                    )
                }
                clearDialog()
            }
            is Resource.Error -> {
                _state.update { it.copy(userMessage = result.message, isLoading = false) }
            }
            else -> {}
        }

        try { triggerSyncGastosUseCase() } catch (_: Exception) {}
    }

    private fun deleteGasto(id: String) = viewModelScope.launch {
        when (val res = deleteGastoUseCase(id)) {
            is Resource.Success -> _state.update { it.copy(userMessage = "Eliminado") }
            is Resource.Error -> _state.update { it.copy(userMessage = res.message) }
            else -> {}
        }

        try { triggerSyncGastosUseCase() } catch (_: Exception) {}
    }

    private fun clearDialog() {
        currentGastoId = null
        _state.update {
            it.copy(
                showDialog = false,
                monto = "",
                descripcion = "",
                categoriaSeleccionadaId = null,
                categoriaSeleccionadaNombre = null,
                fecha = "",
            )
        }
        viewModelScope.launch {
            delay(100)
            _state.update { it.copy(isSaved = false) }
        }
    }

    private fun update(block: (GastoUiState) -> GastoUiState) {
        _state.update(block)
    }

    private fun updateAndFilter(block: (GastoUiState) -> GastoUiState) {
        _state.update(block)
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val ui = _state.value
        var list = ui.gastosListOriginal

        if (!ui.categoriaSeleccionadaNombre.isNullOrBlank() && ui.categoriaSeleccionadaNombre != "Todas") {
            val catId = ui.categorias.find { it.nombre == ui.categoriaSeleccionadaNombre }?.categoriaId
            if (catId != null) list = list.filter { it.categoriaId == catId }
        }

        val today = LocalDate.now()
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        list = when (ui.fecha) {
            "Hoy" -> list.filter { safeDate(it.fecha, format) == today }
            "Ayer" -> list.filter { safeDate(it.fecha, format) == today.minusDays(1) }
            "Esta semana" -> {
                val start = today.minusDays(today.dayOfWeek.value.toLong() - 1)
                val end = start.plusDays(6)
                list.filter {
                    val f = safeDate(it.fecha, format)
                    f != null && f >= start && f <= end
                }
            }
            else -> list
        }

        list = when (ui.montoOrden) {
            "Menor a Mayor" -> list.sortedBy { it.monto }
            "Mayor a Menor" -> list.sortedByDescending { it.monto }
            else -> list
        }

        _state.update { it.copy(gastosList = list) }
    }

    private fun safeDate(date: String?, formatter: DateTimeFormatter): LocalDate? =
        try { if (date.isNullOrBlank()) null else LocalDate.parse(date, formatter) }
        catch (_: Exception) { null }

    private fun calcularEstadisticas(
        gastos: List<Gastos>,
        totalGeneral: Double,
        categorias: List<Categorias>
    ): List<CategoryData> {
        val mapa = categorias.associateBy { it.categoriaId }
        if (gastos.isEmpty() || totalGeneral == 0.0) return emptyList()
        return gastos.groupBy { it.categoriaId }.map { (catId, list) ->
            val cat = mapa[catId]
            val suma = list.sumOf { it.monto }
            val porcentaje = (suma / totalGeneral * 100).toFloat()
            CategoryData(catId, cat?.nombre ?: "Desconocida", cat?.tipoId ?: 0, suma, porcentaje)
        }
    }
}
