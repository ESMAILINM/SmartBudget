package edu.ucne.smartbudget.presentation.dashboardScreen.Screen

sealed class HomeUiEvent {
    object ViewAllTransactions : HomeUiEvent()
    object ViewReports : HomeUiEvent()
    object ViewGoals : HomeUiEvent()
    data class OpenAddTransaction(val isExpense: Boolean) : HomeUiEvent()
}