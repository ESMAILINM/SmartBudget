package edu.ucne.smartbudget.presentation.Usuarios

import edu.ucne.smartbudget.domain.model.Usuarios

data class UsuarioUiState (
    val usuarioId: Int = 0,
    val userName: String = "",
    val password: String = "",
    val isNew: Boolean = true,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val saved: Boolean = false,
    val deleted: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false,
    val showDialog: Boolean = false,
    val userMessage: String? = null,
    val successLogin: Boolean = false,
    val loginError: String? = null,
    val usuarios: List<Usuarios> = emptyList(),
    val usuarioActual: Usuarios? = null

)