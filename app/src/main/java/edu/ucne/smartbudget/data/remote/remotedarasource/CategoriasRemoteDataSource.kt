package edu.ucne.smartbudget.data.remote.remotedatasource

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.SmartBudgetApi
import edu.ucne.smartbudget.data.remote.dto.categoriasdto.CategoriaRequest
import edu.ucne.smartbudget.data.remote.dto.categoriasdto.CategoriaResponse
import javax.inject.Inject

class CategoriasRemoteDataSource @Inject constructor(
    private val api: SmartBudgetApi
) {

    suspend fun createCategoria(request: CategoriaRequest): Resource<CategoriaResponse> {
        return try {
            val response = api.createCategoria(request)
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

    suspend fun updateCategoria(id: Int, request: CategoriaRequest): Resource<Unit> {
        return try {
            val response = api.updateCategoria(id, request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun deleteCategoria(id: Int): Resource<Unit> {
        return try {
            val response = api.deleteCategoria(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getCategoria(id: Int): Resource<CategoriaResponse?> {
        return try {
            val response = api.getCategoria(id)
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
