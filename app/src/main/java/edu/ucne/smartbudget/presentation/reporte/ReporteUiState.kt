package edu.ucne.smartbudget.presentation.reporte

import edu.ucne.smartbudget.domain.model.Categorias

data class ReporteUiState(
    val isLoading: Boolean = false,
    val selectedTypeFilter: String = "Todo",
    val selectedDateFilter: String = "Este mes",
    val totalGastos: Double = 1200.00,
    val gastosPercentageChange: Double = -10.0,
    val totalIngresos: Double = 0.0,
    val ingresosPercentageChange: Double = 0.0,
    val totalNeto: Double = 2500.00,
    val netoPercentageChange: Double = 5.0,
    val categoriasData: List<CategoryProgress> = emptyList(),
    val categoriasDataAll: List<CategoryProgress> = emptyList(),
    val showAllCategories: Boolean = false,
    val ingresoVsGastoData: IngresoVsGastoData = IngresoVsGastoData(0.0, 0.0),
    val categorias: List<Categorias> = emptyList(),
    val isIngreso: Boolean

)

