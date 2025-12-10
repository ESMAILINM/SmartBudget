package edu.ucne.smartbudget.presentation.Usuarios

import android.content.Context

sealed class UsuarioUiEvent {
    data class UserNameChanged(val value: String) : UsuarioUiEvent()
    data class PasswordChanged(val value: String) : UsuarioUiEvent()
    data class ConfirmPasswordChanged(val value: String) : UsuarioUiEvent()
    data class Login(val user: String, val pass: String) : UsuarioUiEvent()
    data class RememberMeChanged(val isChecked: Boolean) : UsuarioUiEvent()
    data class OnRecoveryEmailChanged(val value: String) : UsuarioUiEvent()
    data class OnSendRecoveryEmail(val context: Context) : UsuarioUiEvent()

    object Save : UsuarioUiEvent()
    object ToggleLoginMode : UsuarioUiEvent()
    object OnForgotPasswordClicked : UsuarioUiEvent()
    object OnRecoveryDialogDismissed : UsuarioUiEvent()
    object HideDialog : UsuarioUiEvent()
    object UserMessageShown : UsuarioUiEvent()
}
