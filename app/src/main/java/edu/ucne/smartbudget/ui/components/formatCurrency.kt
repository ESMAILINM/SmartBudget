package edu.ucne.smartbudget.ui.components

import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Double, currencyCode: String): String {
    val symbol = when (currencyCode) {
        "DOP" -> "RD$"
        "USD" -> "$"
        "EUR" -> "€"
        "MXN" -> "MX$"
        else -> "$"
    }

    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    numberFormat.minimumFractionDigits = 2
    numberFormat.maximumFractionDigits = 2
    val formattedNumber = numberFormat.format(amount)

    return "$symbol$formattedNumber"
}
