package edu.ucne.smartbudget.presentation.auth

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreen(
    state: UsuarioUiState,
    onEvent: (UsuarioUiEvent) -> Unit,
    onLoginExitoso: () -> Unit
) {
    val isLogin = state.isLogin

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
                    if (isLogin)
                        onEvent(UsuarioUiEvent.Login(state.userName, state.password))
                    else
                        onEvent(UsuarioUiEvent.Save)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLogin) "Entrar" else "Registrar")
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = {
                onEvent(UsuarioUiEvent.ToggleLoginMode)   // ⬅️ Nuevo evento
            }) {
                Text(if (isLogin) "¿No tienes cuenta? Registrarse"
                else "¿Ya tienes cuenta? Iniciar sesión")
            }
        }

        if (state.isLoading || state.isSaving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    LinearWavyProgressIndicator()

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