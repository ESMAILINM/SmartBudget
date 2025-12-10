package edu.ucne.smartbudget.presentation.perfil

sealed interface ProfileUiEvent {
    data class NameChanged(val userName: String) : ProfileUiEvent
    data class PasswordChanged(val password: String) : ProfileUiEvent
    object SaveChanges : ProfileUiEvent
    object ClearMessage : ProfileUiEvent
    object DeleteAccount : ProfileUiEvent

}