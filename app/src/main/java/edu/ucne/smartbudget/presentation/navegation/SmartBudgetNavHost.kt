package edu.ucne.smartbudget.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.ucne.smartbudget.presentation.Usuarios.UsuarioViewModel
import edu.ucne.smartbudget.presentation.auth.AuthScreen

@Composable
fun SmartBudgetNavHost(
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel = hiltViewModel()
) {
    val state by usuarioViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.usuarioActual) {
        val user = state.usuarioActual
        if (user != null) {
            navController.navigate(Screen.MainAppWrapper.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            AuthScreen(
                state = state,
                onEvent = usuarioViewModel::onEvent,
                onLoginExitoso = {
                    navController.navigate(Screen.MainAppWrapper.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    usuarioViewModel.limpiarSuccessLogin()
                }
            )
        }

        composable(Screen.MainAppWrapper.route) {
            val user = state.usuarioActual
            val currentUserId = when {
                user?.remoteId != null && user.remoteId != 0 -> user.remoteId.toString()
                else -> user?.usuarioId ?: ""
            }

            if (currentUserId.isNotEmpty()) {
                MainAppScreen(
                    onLogout = {
                        usuarioViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.MainAppWrapper.route) { inclusive = true }
                        }
                    },
                    currentUserId = currentUserId
                )
            }
        }
    }
}
