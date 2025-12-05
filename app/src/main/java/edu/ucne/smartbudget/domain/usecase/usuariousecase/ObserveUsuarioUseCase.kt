package edu.ucne.smartbudget.domain.usecase.usuariousecase

import edu.ucne.smartbudget.domain.model.Usuarios
import edu.ucne.smartbudget.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUsuarioUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    operator fun invoke(): Flow<List<Usuarios>> {
        return usuarioRepository.getUsuarios()
    }
}