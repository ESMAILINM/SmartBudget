package edu.ucne.smartbudget.domain.usecase.usuariousecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Usuarios
import edu.ucne.smartbudget.domain.repository.UsuarioRepository
import jakarta.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(username: String, password: String): Resource<Usuarios> {
        return repository.login(username, password)
    }
}
