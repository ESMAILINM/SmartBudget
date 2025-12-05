package edu.ucne.smartbudget.domain.usecase.usuariousecase

import edu.ucne.smartbudget.domain.repository.UsuarioRepository
import edu.ucne.smartbudget.data.remote.Resource
import javax.inject.Inject

class DeleteUsuarioUseCase @Inject constructor(
     private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit>
        = usuarioRepository.deleteUsuario(id)
}