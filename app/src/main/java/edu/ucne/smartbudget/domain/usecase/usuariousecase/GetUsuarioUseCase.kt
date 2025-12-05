package edu.ucne.smartbudget.domain.usecase.usuariousecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Usuarios
import edu.ucne.smartbudget.domain.repository.UsuarioRepository
import javax.inject.Inject

class GetUsuarioUseCase @Inject constructor(
    private val usuariosRepository: UsuarioRepository
) {
    suspend operator fun invoke(id: String): Resource<Usuarios?> {
        return usuariosRepository.getUsuario(id)
    }
}