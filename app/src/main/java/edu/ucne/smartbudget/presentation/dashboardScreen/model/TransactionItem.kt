package edu.ucne.smartbudget.presentation.dashboardScreen.model

data class TransactionItem(
    val id: String,
    val description: String,
    val amount: Double,
    val date: String,
    val categoryName: String,
    val categoryColor: String?,
    val categoryIcon: String?,
    val isExpense: Boolean
)
