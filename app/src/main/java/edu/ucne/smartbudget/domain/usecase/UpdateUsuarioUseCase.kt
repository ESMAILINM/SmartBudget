package edu.ucne.smartbudget.domain.usecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Usuarios
import edu.ucne.smartbudget.domain.repository.UsuarioRepository
import javax.inject.Inject

class UpdateUsuarioUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(usuarios: Usuarios): Resource<Usuarios> {
        return try {
            usuarioRepository.updateUsuario(usuarios)
            Resource.Success(usuarios)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al actualizar usuario")
        }
    }
}


