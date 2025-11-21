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

    override suspend fun getUsuario(id: Int): Resource<Usuarios?> {
        return when (val res = api.getUsuario(id)) {
            is Resource.Success -> Resource.Success(res.data?.toDomain())
            is Resource.Error -> Resource.Error(res.message ?: "Error")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun insertUsuario(usuario: Usuarios): Resource<Usuarios> {
        val dto = usuario.toDto()
        return when (val res = api.createUsuario(dto)) {
            is Resource.Success -> {
                val domain = res.data?.toDomain()
                if (domain != null) Resource.Success(domain)
                else Resource.Error("Error al convertir")
            }
            is Resource.Error -> Resource.Error(res.message ?: "Error")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun updateUsuario(usuario: Usuarios): Resource<Unit> {
        val id = usuario.usuarioId ?: return Resource.Error("usuarioId nulo")
        val dto = usuario.toDto()
        return when (val res = api.updateUsuario(id, dto)) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(res.message ?: "Error")
            is Resource.Loading -> Resource.Loading()
        }
    }
}
