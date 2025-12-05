package edu.ucne.smartbudget.domain.usecase.usuariousecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Usuarios
import edu.ucne.smartbudget.domain.repository.UsuarioRepository
import javax.inject.Inject

class UpdateUsuarioUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(usuarios: Usuarios): Resource<Unit> {
        return usuarioRepository.updateUsuario(usuarios)
    }
}
