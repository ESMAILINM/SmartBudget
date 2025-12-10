package edu.ucne.smartbudget.presentation.dashboardScreen.model

import edu.ucne.smartbudget.presentation.dashboardScreen.components.items.CategoryProgress

data class DashboardUiData(
    val summary: SummaryData,
    val trend: List<TrendData>,
    val breakdown: List<CategoryProgress>,
    val recentTransactions: List<TransactionItem>
)
