package edu.ucne.smartbudget.presentation.navegation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
}