package edu.ucne.smartbudget.presentation.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import edu.ucne.smartbudget.presentation.Usuarios.UsuarioUiEvent
import edu.ucne.smartbudget.presentation.Usuarios.UsuarioUiState

@Composable
fun UserField(state: UsuarioUiState, onEvent: (UsuarioUiEvent) -> Unit) {
    CustomLabel("Usuario")
    OutlinedTextField(
        value = state.userName,
        onValueChange = { onEvent(UsuarioUiEvent.UserNameChanged(it)) },
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeDefaults.Medium,
        colors = customTextFieldColors(),
        singleLine = true,
        isError = state.userNameError != null,
        supportingText = {
            state.userNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    )
}

@Composable
fun PasswordField(
    state: UsuarioUiState,
    passwordVisible: Boolean,
    toggleVisibility: () -> Unit,
    onEvent: (UsuarioUiEvent) -> Unit
) {
    CustomLabel("Contraseña")
    OutlinedTextField(
        value = state.password,
        onValueChange = { onEvent(UsuarioUiEvent.PasswordChanged(it)) },
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeDefaults.Medium,
        colors = customTextFieldColors(),
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Password),
        isError = state.passwordError != null,
        supportingText = {
            state.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        trailingIcon = {
            IconButton(onClick = toggleVisibility) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun ConfirmPasswordField(
    state: UsuarioUiState,
    visible: Boolean,
    toggleVisibility: () -> Unit,
    onEvent: (UsuarioUiEvent) -> Unit
) {
    CustomLabel("Confirmar Contraseña")
    OutlinedTextField(
        value = state.confirmPassword,
        onValueChange = { onEvent(UsuarioUiEvent.ConfirmPasswordChanged(it)) },
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeDefaults.Medium,
        colors = customTextFieldColors(),
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Password),
        isError = state.confirmPasswordError != null,
        supportingText = {
            state.confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        trailingIcon = {
            IconButton(onClick = toggleVisibility) {
                Icon(
                    imageVector = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null
                )
            }
        }
    )
}
