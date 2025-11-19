package edu.ucne.smartbudget.presentation.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import edu.ucne.smartbudget.presentation.Usuarios.UsuarioUiEvent
import edu.ucne.smartbudget.presentation.Usuarios.UsuarioUiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreen(
    state: UsuarioUiState,
    onEvent: (UsuarioUiEvent) -> Unit,
    onLoginExitoso: () -> Unit
) {
    var isLogin by remember { mutableStateOf(true) }

    LaunchedEffect(state.successLogin) {
        if (state.successLogin) {
            onEvent(UsuarioUiEvent.hideDialog)
            onLoginExitoso()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isLogin) "Iniciar Sesión" else "Registro",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = state.userName,
                onValueChange = { onEvent(UsuarioUiEvent.userNameChanged(it)) },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { onEvent(UsuarioUiEvent.passwordChanged(it)) },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isLogin) onEvent(UsuarioUiEvent.Login(state.userName, state.password))
                    else onEvent(UsuarioUiEvent.Save)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLogin) "Entrar" else "Registrar")
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = { isLogin = !isLogin }) {
                Text(if (isLogin) "¿No tienes cuenta? Registrarse" else "¿Ya tienes cuenta? Iniciar sesión")
            }
        }

        // Loading overlay
        if (state.isLoading || state.isSaving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LinearWavyLoadingIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Por favor espere…")
                }
            }
        }

        state.userMessage?.let { msg ->
            AlertDialog(
                onDismissRequest = { onEvent(UsuarioUiEvent.userMessageShown) },
                confirmButton = {
                    TextButton(onClick = { onEvent(UsuarioUiEvent.userMessageShown) }) {
                        Text("OK")
                    }
                },
                title = { Text("Información") },
                text = { Text(msg) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LinearWavyLoadingIndicator(
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableFloatStateOf(0.1f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    LaunchedEffect(Unit) {
        while (true) {
            progress = if (progress >= 1f) 0f else progress + 0.01f
            delay(16)
        }
    }

    LinearWavyProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
    )
}