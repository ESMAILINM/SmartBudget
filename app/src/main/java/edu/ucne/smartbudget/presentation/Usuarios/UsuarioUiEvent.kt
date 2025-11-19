package edu.ucne.smartbudget.presentation.Usuarios

sealed class UsuarioUiEvent {
    data class userNameChanged(val usuario: String) : UsuarioUiEvent()
    data class passwordChanged(val password: String) : UsuarioUiEvent()
    data class load(val usuarioId: Int) : UsuarioUiEvent()
    object Save : UsuarioUiEvent()
    object showDialog : UsuarioUiEvent()
    object hideDialog : UsuarioUiEvent()
    object userMessageShown : UsuarioUiEvent()
    data class Login(val userName: String, val password: String) : UsuarioUiEvent()

}