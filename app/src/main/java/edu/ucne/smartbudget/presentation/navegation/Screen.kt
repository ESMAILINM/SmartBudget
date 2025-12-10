package edu.ucne.smartbudget.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String? = null,
    val icon: ImageVector? = null
) {
    object Login : Screen("login")
    object MainAppWrapper : Screen("main_app_wrapper")

    object Home : Screen("home/{userId}", "Inicio", Icons.Outlined.Home) {
        fun createRoute(userId: String) = "home/$userId"
    }

    object Metas : Screen("metas/{userId}", "Metas", Icons.Outlined.Savings) {
        fun createRoute(userId: String) = "metas/$userId"
    }

    object Reports : Screen("reports/{userId}", "Reportes", Icons.Outlined.BarChart) {
        fun createRoute(userId: String) = "reports/$userId"
    }

    object Profile : Screen("profile/{userId}", "Perfil", Icons.Outlined.Person) {
        fun createRoute(userId: String) = "profile/$userId"
    }

    object Gastos : Screen("gastos/{userId}") {
        fun createRoute(userId: String) = "gastos/$userId"
    }

    object Ingresos : Screen("ingresos/{userId}") {
        fun createRoute(userId: String) = "ingresos/$userId"
    }

    object Configuracion : Screen("configuracion/{userId}") {
        fun createRoute(userId: String) = "configuracion/$userId"
    }

    object Categorias : Screen("categorias/{userId}") {
        fun createRoute(userId: String) = "categorias/$userId"
    }

    object MetaEdit : Screen("meta/{userId}/{metaId}") {
        fun createRoute(userId: String, metaId: String) = "meta/$userId/$metaId"
    }

    object GastosAdd : Screen("gastos_add/{userId}?gastoId={gastoId}") {
        fun createRoute(userId: String, gastoId: String?) =
            "gastos_add/$userId?gastoId=${gastoId ?: "null"}"
    }
    object RecentTransactions : Screen("recent_transactions"){
        fun createRoute(userId: String) = "recent_transactions/$userId"
    }

}
