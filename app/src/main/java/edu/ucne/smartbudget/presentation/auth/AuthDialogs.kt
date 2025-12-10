package edu.ucne.smartbudget.presentation.auth

import android.content.Context
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import edu.ucne.smartbudget.presentation.Usuarios.UsuarioUiEvent
import edu.ucne.smartbudget.presentation.Usuarios.UsuarioUiState

@Composable
fun AuthDialogs(
    state: UsuarioUiState,
    onEvent: (UsuarioUiEvent) -> Unit,
    context: Context
) {
    val colors = MaterialTheme.colorScheme

    if (state.showDialog && state.userMessage != null) {
        AlertDialog(
            onDismissRequest = { onEvent(UsuarioUiEvent.UserMessageShown) },
            confirmButton = {
                TextButton(onClick = { onEvent(UsuarioUiEvent.UserMessageShown) }) {
                    Text("OK", color = colors.primary)
                }
            },
            title = { Text("Información") },
            text = { Text(state.userMessage) },
            containerColor = colors.surface
        )
    }

    if (state.showRecoveryDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(UsuarioUiEvent.OnRecoveryDialogDismissed) },
            title = { Text("Recuperar Contraseña") },
            text = {
                Column {
                    Text("Ingresa tu correo electrónico o usuario para enviarte un enlace de recuperación.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = state.recoveryEmail,
                        onValueChange = { onEvent(UsuarioUiEvent.OnRecoveryEmailChanged(it)) },
                        label = { Text("Correo o Usuario") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onEvent(UsuarioUiEvent.OnSendRecoveryEmail(context))
                }) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(UsuarioUiEvent.OnRecoveryDialogDismissed) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
