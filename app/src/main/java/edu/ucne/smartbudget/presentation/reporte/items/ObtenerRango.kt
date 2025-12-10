package edu.ucne.smartbudget.presentation.reporte.items

fun obtenerRangoTexto(filtro: String): String {
    return when (filtro) {
        "Este mes" -> "Este mes"
        "Mes pasado" -> "Mes pasado"
        "Este año" -> "Este año"
        else -> ""
    }
}
