package edu.ucne.smartbudget.data.remote

import edu.ucne.smartbudget.data.remote.dto.UsuariosDto
import javax.inject.Inject

class UsuariosRemoteDataSource @Inject constructor(
    private val api: SmartBudgetApi
) {

    suspend fun createUsuario(usuario: UsuariosDto): Resource<UsuariosDto> {
        return try {
            val response = api.createUsuario(usuario)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Error al crear usuario: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun updateUsuario(id: Int, usuario: UsuariosDto): Resource<Unit> {
        return try {
            val response = api.updateUsuario(id, usuario)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Error al actualizar usuario: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getUsuarios(): Resource<List<UsuariosDto>> {
        return try {
            val usuarios = api.getUsuarios()
            Resource.Success(usuarios)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getUsuario(id: Int): Resource<UsuariosDto?> {
        return try {
            val usuario = api.getUsuario(id)
            Resource.Success(usuario)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }
}
