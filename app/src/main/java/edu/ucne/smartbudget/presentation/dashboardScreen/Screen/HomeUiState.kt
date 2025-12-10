package edu.ucne.smartbudget.presentation.dashboardScreen.Screen

import edu.ucne.smartbudget.presentation.dashboardScreen.model.SummaryData
import edu.ucne.smartbudget.presentation.dashboardScreen.model.TrendData
import edu.ucne.smartbudget.presentation.dashboardScreen.components.items.CategoryProgress
import edu.ucne.smartbudget.presentation.dashboardScreen.model.TransactionItem

data class HomeUiState(
    val isLoading: Boolean = true,
    val summary: SummaryData? = null,
    val trend: List<TrendData> = emptyList(),
    val breakdown: List<CategoryProgress> = emptyList(),
    val recentTransactions: List<TransactionItem> = emptyList(),
    val error: String? = null
)
