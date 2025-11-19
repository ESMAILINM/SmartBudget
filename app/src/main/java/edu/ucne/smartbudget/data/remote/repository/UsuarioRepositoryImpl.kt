package edu.ucne.smartbudget.data.remote.repository

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.UsuariosRemoteDataSource
import edu.ucne.smartbudget.data.remote.mapper.toDomain
import edu.ucne.smartbudget.data.remote.mapper.toDto
import edu.ucne.smartbudget.domain.model.Usuarios
import edu.ucne.smartbudget.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UsuarioRepositoryImpl @Inject constructor(
    private val api: UsuariosRemoteDataSource
) : UsuarioRepository {

    override fun getUsuarios(): Flow<List<Usuarios>> = flow {
        when (val res = api.getUsuarios()) {
            is Resource.Success -> emit(res.data?.map { it.toDomain() } ?: emptyList())
            is Resource.Error -> emit(emptyList())
            is Resource.Loading -> emit(emptyList())
        }
    }

    override suspend fun getUsuario(id: Int): Usuarios? {
        return when (val res = api.getUsuario(id)) {
            is Resource.Success -> res.data?.toDomain()
            else -> null
        }
    }

    override suspend fun insertUsuario(usuario: Usuarios) {
        api.createUsuario(usuario.toDto())
    }

    override suspend fun updateUsuario(usuario: Usuarios) {
        val id = usuario.usuarioId ?: return
        api.updateUsuario(id, usuario.toDto())
    }
}
