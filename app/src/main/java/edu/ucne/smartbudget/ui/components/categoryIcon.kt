package edu.ucne.smartbudget.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.vector.ImageVector


fun categoryIcon(nombre: String): ImageVector {
    return when (nombre.trim().lowercase()) {
        "groceries", "comida", "supermercado", "food", "alimentacion", "despensa" ->
            Icons.Outlined.ShoppingCart

        "dining out", "restaurante", "cena", "restaurant", "bar", "bebidas" ->
            Icons.Outlined.Restaurant

        "transport", "transporte", "gasolina", "fuel", "uber", "taxi", "bus", "coche", "auto" ->
            Icons.Outlined.LocalGasStation

        "healthcare", "medicina", "salud", "health", "farmacia", "doctor", "hospital" ->
            Icons.Outlined.HealthAndSafety

        "self-care", "cuidado personal", "spa", "belleza", "gym", "gimnasio", "deporte" ->
            Icons.Outlined.Spa

        "entertainment", "entretenimiento", "cine", "movies", "juegos", "games", "hobby", "diversion" ->
            Icons.Outlined.Movie

        "housing", "hogar", "casa", "home", "rent", "alquiler", "hipoteca" ->
            Icons.Outlined.Home

        "utilities", "servicios", "luz", "agua", "internet", "telefono", "bills", "facturas" ->
            Icons.Outlined.Lightbulb

        "education", "educacion", "escuela", "school", "universidad", "libros", "curso" ->
            Icons.Outlined.School

        "work", "trabajo", "salary", "salario", "nomina", "sueldo", "negocio" ->
            Icons.Outlined.Work

        "shopping", "compras", "ropa", "store", "zapatos", "moda" ->
            Icons.Outlined.ShoppingBag

        "travel", "viajes", "vacaciones", "trip", "hotel", "vuelo" ->
            Icons.Outlined.Flight

        "pets", "mascotas", "perro", "gato", "veterinario" ->
            Icons.Outlined.Pets

        "gifts", "regalos", "donaciones", "charity" ->
            Icons.Outlined.CardGiftcard

        "savings", "ahorros", "inversion", "banco" ->
            Icons.Outlined.Savings

        else -> Icons.Outlined.Category
    }
}

