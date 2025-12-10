package edu.ucne.smartbudget.domain.usecase.usuariousecase

import edu.ucne.smartbudget.domain.model.Usuarios
import javax.inject.Inject

class ValidationAuthUseCase @Inject constructor() {

    fun validateUser(userName: String): ValidationResult {
        if (userName.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "El usuario es requerido"
            )
        }
        if (userName.length < 4) {
            return ValidationResult(
                successful = false,
                errorMessage = "El usuario debe tener al menos 4 caracteres"
            )
        }
        return ValidationResult(successful = true)
    }

    fun validatePassword(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "La contraseña es requerida"
            )
        }
        if (password.length < 6) {
            return ValidationResult(
                successful = false,
                errorMessage = "La contraseña debe tener al menos 6 caracteres"
            )
        }
        return ValidationResult(successful = true)
    }

    fun validateRepeatedPassword(password: String, repeatedPassword: String): ValidationResult {
        if (password != repeatedPassword) {
            return ValidationResult(
                successful = false,
                errorMessage = "Las contraseñas no coinciden"
            )
        }
        return ValidationResult(successful = true)
    }

    fun validateUserAvailability(userName: String, existingUsers: List<Usuarios>): ValidationResult {
        val exists = existingUsers.any {
            it.userName.equals(userName, ignoreCase = true)
        }

        if (exists) {
            return ValidationResult(
                successful = false,
                errorMessage = "El nombre de usuario '$userName' ya está en uso."
            )
        }
        return ValidationResult(successful = true)
    }
}
