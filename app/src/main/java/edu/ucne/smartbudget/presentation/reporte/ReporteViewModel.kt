package edu.ucne.smartbudget.presentation.reporte

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.smartbudget.data.local.datastore.SessionDataStore
import edu.ucne.smartbudget.domain.model.Categorias
import edu.ucne.smartbudget.domain.model.Gastos
import edu.ucne.smartbudget.domain.model.Ingresos
import edu.ucne.smartbudget.domain.usecase.categoriasusecase.ObserveCategoriasUseCase
import edu.ucne.smartbudget.domain.usecase.gastosusecase.ObserveGastosUseCase
import edu.ucne.smartbudget.domain.usecase.ingresosusecase.ObserveIngresosUseCase
import edu.ucne.smartbudget.presentation.reporte.items.CategoryProgress
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ReporteViewModel @Inject constructor(
    private val observeIngresosUseCase: ObserveIngresosUseCase,
    private val observeGastosUseCase: ObserveGastosUseCase,
    private val observeCategoriasUseCase: ObserveCategoriasUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _filterType = MutableStateFlow("Todo")
    private val _filterDate = MutableStateFlow("Este mes")
    private val _showAllCategories = MutableStateFlow(false)

    private val _state = MutableStateFlow(ReporteUiState(isLoading = true, isIngreso = true))
    val state: StateFlow<ReporteUiState> = _state.asStateFlow()

    init {
        initDataStream()
    }

    fun onEvent(event: ReporteUiEvent) {
        when (event) {
            is ReporteUiEvent.SelectTypeFilter -> _filterType.value = event.filter
            is ReporteUiEvent.SelectDateFilter -> _filterDate.value = event.filter
            is ReporteUiEvent.ToggleShowAll -> _showAllCategories.update { !it }
            ReporteUiEvent.OnClose -> Unit
        }
    }

    private fun initDataStream() {
        viewModelScope.launch {
            sessionDataStore.userIdFlow.flatMapLatest { userId ->
                if (userId.isNullOrBlank()) {
                    flowOf(ReporteUiState(isLoading = false, isIngreso = true))
                } else {
                    combine(
                        observeIngresosUseCase(userId),
                        observeGastosUseCase(userId),
                        observeCategoriasUseCase(),
                        _filterType,
                        _filterDate,
                        _showAllCategories
                    ) { args: Array<Any> ->
                        val ingresos = args[0] as List<Ingresos>
                        val gastos = args[1] as List<Gastos>
                        val categorias = args[2] as List<Categorias>
                        val typeFilter = args[3] as String
                        val dateFilter = args[4] as String
                        val showAll = args[5] as Boolean

                        calculateState(
                            userId,
                            ingresos,
                            gastos,
                            categorias,
                            typeFilter,
                            dateFilter,
                            showAll
                        )
                    }
                }
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    private fun calculateState(
        userId: String,
        ingresos: List<Ingresos>,
        gastos: List<Gastos>,
        categorias: List<Categorias>,
        typeFilter: String,
        dateFilter: String,
        showAll: Boolean
    ): ReporteUiState {
        val ingresosUsuario = ingresos.filter { it.usuarioId.toString() == userId }
        val gastosUsuario = gastos.filter { it.usuarioId.toString() == userId }

        val ingresosMesActual = ingresosUsuario.filter { it.fecha.startsWith(mesActual()) }
        val gastosMesActual = gastosUsuario.filter { it.fecha.startsWith(mesActual()) }
        val totalIngresosActual = ingresosMesActual.sumOf { it.monto }
        val totalGastosActual = gastosMesActual.sumOf { it.monto }

        val ingresosMesAnterior = ingresosUsuario.filter { it.fecha.startsWith(mesPasado()) }
        val gastosMesAnterior = gastosUsuario.filter { it.fecha.startsWith(mesPasado()) }
        val totalIngresosAnterior = ingresosMesAnterior.sumOf { it.monto }
        val totalGastosAnterior = gastosMesAnterior.sumOf { it.monto }

        val ingresosPercentage = calcularPorcentaje(totalIngresosActual, totalIngresosAnterior)
        val gastosPercentage = calcularPorcentaje(totalGastosActual, totalGastosAnterior)

        val ingresosFiltrados = ingresosUsuario.filter { filtrarPorFecha(it.fecha, dateFilter) }
        val gastosFiltrados = gastosUsuario.filter { filtrarPorFecha(it.fecha, dateFilter) }

        val (finalIngresos, finalGastos) = aplicarTipoFiltro(ingresosFiltrados, gastosFiltrados, typeFilter)

        val allCategoriesData = calcularCategorias(finalIngresos, finalGastos, categorias, typeFilter)
        val displayedCategories = if (showAll) allCategoriesData else allCategoriesData.take(3)

        val totalNetoCalculado = finalIngresos.sumOf { it.monto } - finalGastos.sumOf { it.monto }
        val totalGastosCalculado = finalGastos.sumOf { it.monto }

        return ReporteUiState(
            isLoading = false,
            categoriasData = displayedCategories,
            categoriasDataAll = allCategoriesData,
            totalIngresos = totalIngresosActual,
            totalGastos = if (typeFilter == "Gastos" || typeFilter == "Todo") totalGastosCalculado else totalGastosActual,
            ingresosPercentageChange = ingresosPercentage,
            gastosPercentageChange = gastosPercentage,
            totalNeto = totalNetoCalculado,
            categorias = categorias,
            selectedTypeFilter = typeFilter,
            selectedDateFilter = dateFilter,
            showAllCategories = showAll,
            isIngreso = true
        )
    }

    private fun filtrarPorFecha(fechaItem: String, filtro: String): Boolean =
        when (filtro) {
            "Este mes" -> fechaItem.startsWith(mesActual())
            "Mes pasado" -> fechaItem.startsWith(mesPasado())
            "Este año" -> fechaItem.startsWith(anioActual())
            else -> true
        }

    private fun aplicarTipoFiltro(
        ingresos: List<Ingresos>,
        gastos: List<Gastos>,
        filtro: String
    ): Pair<List<Ingresos>, List<Gastos>> =
        when (filtro) {
            "Ingresos" -> ingresos to emptyList()
            "Gastos" -> emptyList<Ingresos>() to gastos
            else -> ingresos to gastos
        }

    private fun calcularCategorias(
        ingresos: List<Ingresos>,
        gastos: List<Gastos>,
        categorias: List<Categorias>,
        filtro: String
    ): List<CategoryProgress> {
        val items = when (filtro) {
            "Ingresos" -> ingresos.map { it.categoriaId to it.monto }
            "Gastos" -> gastos.map { it.categoriaId to it.monto }
            else -> ingresos.map { it.categoriaId to it.monto } + gastos.map { it.categoriaId to it.monto }
        }

        if (items.isEmpty()) return emptyList()

        val categoriasFiltradas = when (filtro) {
            "Ingresos" -> categorias.filter { it.tipoId == 1 }
            "Gastos" -> categorias.filter { it.tipoId == 2 }
            else -> categorias
        }

        val total = items.sumOf { it.second }
        if (total == 0.0) return emptyList()

        return categoriasFiltradas.map { cat ->
            val suma = items.filter { it.first.toString() == cat.categoriaId }.sumOf { it.second }
            val porcentaje = suma / total
            CategoryProgress(cat.nombre, porcentaje)
        }
            .filter { it.percentage > 0 }
            .sortedByDescending { it.percentage }
    }

    private fun calcularPorcentaje(actual: Double, anterior: Double): Double =
        when {
            anterior == 0.0 && actual == 0.0 -> 0.0
            anterior == 0.0 && actual > 0.0 -> 100.0
            else -> ((actual - anterior) / anterior) * 100
        }

    private fun mesActual() = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    private fun mesPasado() = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"))
    private fun anioActual() = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))
}
