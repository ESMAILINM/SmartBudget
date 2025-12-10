package edu.ucne.smartbudget.domain.usecase.usuariousecase

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)