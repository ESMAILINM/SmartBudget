package edu.ucne.smartbudget.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.ucne.smartbudget.presentation.Usuarios.UsuarioUiEvent
import edu.ucne.smartbudget.presentation.Usuarios.UsuarioUiState

@Composable
fun AuthScreen(
    state: UsuarioUiState,
    onEvent: (UsuarioUiEvent) -> Unit,
    onLoginExitoso: (String) -> Unit
) {
    val isLogin = state.isLogin
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(state.successLogin, state.usuarioActual) {
        if (state.successLogin && state.usuarioActual != null) {
            onEvent(UsuarioUiEvent.HideDialog)
            val user = state.usuarioActual
            val userIdParaNavegar = if (user.remoteId != null && user.remoteId != 0) {
                user.remoteId.toString()
            } else {
                user.usuarioId
            }
            onLoginExitoso(userIdParaNavegar)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoSection()
            Spacer(Modifier.height(32.dp))

            UserField(state = state, onEvent = onEvent)
            Spacer(Modifier.height(16.dp))

            PasswordField(
                state = state,
                passwordVisible = passwordVisible,
                toggleVisibility = { passwordVisible = !passwordVisible },
                onEvent = onEvent
            )

            if (!isLogin) {
                Spacer(Modifier.height(16.dp))
                ConfirmPasswordField(
                    state = state,
                    visible = confirmPasswordVisible,
                    toggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                    onEvent = onEvent
                )
            }

            Spacer(Modifier.height(16.dp))

            if (isLogin) {
                ForgotPasswordSection(state = state, onEvent = onEvent)
            } else {
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))

            AuthButton(
                state = state,
                isLogin = isLogin,
                onEvent = onEvent
            )

            Spacer(Modifier.height(24.dp))

            SwitchLoginRegister(
                isLogin = isLogin,
                onEvent = onEvent
            )
        }

        AuthDialogs(state = state, onEvent = onEvent, context = context)
    }
}

@Preview(showBackground = true, name = "Auth Light")
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Auth Dark")
@Composable
fun AuthScreenPreview() {
    MaterialTheme {
        AuthScreen(
            state = UsuarioUiState(isLogin = true, userName = "Demo"),
            onEvent = {},
            onLoginExitoso = {}
        )
    }
}