package edu.ucne.smartbudget.presentation.navegation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.ucne.smartbudget.presentation.dashboardScreen.HomeScreen
import edu.ucne.smartbudget.presentation.auth.AuthScreen
import edu.ucne.smartbudget.presentation.Usuarios.UsuarioViewModel

@Composable
fun SmartBudgetNavHost(
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel = hiltViewModel()
) {
    val state by usuarioViewModel.state.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            AuthScreen(
                state = state,
                onEvent = usuarioViewModel::onEvent,
                onLoginExitoso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    usuarioViewModel.limpiarSuccessLogin()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                usuarioActual = state.usuarioActual,
                usuarios = state.usuarios,
                onLogout = {
                    usuarioViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
