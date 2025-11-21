package edu.ucne.smartbudget.data.remote

import edu.ucne.smartbudget.data.remote.dto.UsuariosDto
import javax.inject.Inject

class UsuariosRemoteDataSource @Inject constructor(
    private val api: SmartBudgetApi
) {

    suspend fun createUsuario(request: UsuariosDto): Resource<UsuariosDto> {
        return try {
            val response = api.createUsuario(request)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun updateUsuario(id: Int, request: UsuariosDto): Resource<Unit> {
        return try {
            val response = api.updateUsuario(id, request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getUsuarios(): Resource<List<UsuariosDto>> {
        return try {
            val response = api.getUsuarios()
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getUsuario(id: Int): Resource<UsuariosDto?> {
        return try {
            val response = api.getUsuario(id)
            if (response.isSuccessful) {
                Resource.Success(response.body())
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }
}
