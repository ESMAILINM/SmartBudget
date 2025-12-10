package edu.ucne.smartbudget.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import edu.ucne.smartbudget.R
import edu.ucne.smartbudget.presentation.Usuarios.UsuarioUiEvent
import edu.ucne.smartbudget.presentation.Usuarios.UsuarioUiState

@Composable
fun LogoSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "SmartBudget",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Gestiona tus finanzas de manera inteligente",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CustomLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
    unfocusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
    disabledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    cursorColor = MaterialTheme.colorScheme.primary,
    errorBorderColor = MaterialTheme.colorScheme.error,
    errorCursorColor = MaterialTheme.colorScheme.error,
    errorLabelColor = MaterialTheme.colorScheme.error
)

@Composable
fun ForgotPasswordSection(state: UsuarioUiState, onEvent: (UsuarioUiEvent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "¿Olvidaste tu contraseña?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                onEvent(UsuarioUiEvent.OnForgotPasswordClicked)
            }
        )
    }
}

@Composable
fun AuthButton(
    state: UsuarioUiState,
    isLogin: Boolean,
    onEvent: (UsuarioUiEvent) -> Unit
) {
    Button(
        onClick = {
            if (isLogin)
                onEvent(UsuarioUiEvent.Login(state.userName, state.password))
            else
                onEvent(UsuarioUiEvent.Save)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = ShapeDefaults.Medium,
        enabled = !state.isLoading && !state.isSaving
    ) {
        if (state.isLoading || state.isSaving) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = if (isLogin) "Iniciar sesión" else "Registrarse",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SwitchLoginRegister(
    isLogin: Boolean,
    onEvent: (UsuarioUiEvent) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = if (isLogin) "¿No tienes una cuenta? " else "¿Ya tienes una cuenta? ",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = if (isLogin) "Regístrate aquí" else "Inicia sesión aquí",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onEvent(UsuarioUiEvent.ToggleLoginMode) }
        )
    }
}
