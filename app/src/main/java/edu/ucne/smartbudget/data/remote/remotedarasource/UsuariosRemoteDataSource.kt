package edu.ucne.smartbudget.data.remote.remotedatasource

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.SmartBudgetApi
import edu.ucne.smartbudget.data.remote.dto.usuariosdto.UsuarioRequest
import edu.ucne.smartbudget.data.remote.dto.usuariosdto.UsuarioResponse
import javax.inject.Inject

class UsuariosRemoteDataSource @Inject constructor(
    private val api: SmartBudgetApi
) {
    suspend fun createUsuario(request: UsuarioRequest): Resource<UsuarioResponse> {
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

    suspend fun updateUsuario(id: Int, request: UsuarioRequest): Resource<Unit> {
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

    suspend fun deleteUsuario(id: Int): Resource<Unit> {
        return try {
            val response = api.deleteUsuario(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }
    suspend fun getUsuario(id: Int): Resource<UsuarioResponse> {
        return try {
            val response = api.getUsuario(id)
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

}
