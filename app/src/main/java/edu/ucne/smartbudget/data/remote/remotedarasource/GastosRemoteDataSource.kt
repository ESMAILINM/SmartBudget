package edu.ucne.smartbudget.data.remote.remotedatasource

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.SmartBudgetApi
import edu.ucne.smartbudget.data.remote.dto.gastosdto.GastoRequest
import edu.ucne.smartbudget.data.remote.dto.gastosdto.GastoResponse
import javax.inject.Inject

class GastosRemoteDataSource @Inject constructor(
    private val api: SmartBudgetApi
) {
    suspend fun createGasto(request: GastoRequest): Resource<GastoResponse> {
        return try {
            val response = api.createGasto(request)
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

    suspend fun updateGasto(id: Int, request: GastoRequest): Resource<Unit> {
        return try {
            val response = api.updateGasto(id, request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun deleteGasto(id: Int): Resource<Unit> {
        return try {
            val response = api.deleteGasto(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }
}
