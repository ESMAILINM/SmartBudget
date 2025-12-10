package edu.ucne.smartbudget.presentation.dashboardScreen.model

import edu.ucne.smartbudget.domain.model.Categorias

data class SummaryData(
    val totalIngresos: Double = 0.0,
    val totalGastos: Double = 0.0,
    val balance: Double = 0.0,
    val categorias: List<Categorias> = emptyList(),
    val transaccionesRecientes: List<TransactionItem> = emptyList()
)



data class TrendData(
    val label: String,
    val monto: Double,

)

