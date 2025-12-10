package edu.ucne.smartbudget.presentation.perfil

import edu.ucne.smartbudget.domain.model.Usuarios

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val password: String = "",
    val usuarioActual: Usuarios? = null,
    val totalSaved: Double = 0.0,
    val netWorthGrowth: Double = 0.0,
    val metaName: String = "Sin meta activa",
    val metaGoal: Double = 0.0,
    val metaProgress: Float = 0.0f,
    val successMessage: String? = null,
    val isAccountDeleted: Boolean = false

)