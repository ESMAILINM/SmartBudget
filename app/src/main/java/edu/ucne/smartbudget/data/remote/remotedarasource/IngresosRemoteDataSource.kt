package edu.ucne.smartbudget.data.remote.remotedatasource

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.SmartBudgetApi
import edu.ucne.smartbudget.data.remote.dto.ingresosdto.IngresoRequest
import edu.ucne.smartbudget.data.remote.dto.ingresosdto.IngresoResponse
import javax.inject.Inject

class IngresosRemoteDataSource @Inject constructor(
    private val api: SmartBudgetApi
) {

    companion object {
        private const val NETWORK_ERROR = "Error de red"
    }

    suspend fun insertIngreso(request: IngresoRequest): Resource<IngresoResponse> {
        return try {
            val response = api.createIngreso(request)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR)
        }
    }

    suspend fun updateIngreso(id: Int, request: IngresoRequest): Resource<Unit> {
        return try {
            val response = api.updateIngreso(id, request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR)
        }
    }

    suspend fun deleteIngreso(id: Int): Resource<Unit> {
        return try {
            val response = api.deleteIngreso(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR)
        }
    }

    suspend fun getIngreso(id: Int): Resource<IngresoResponse> {
        return try {
            val response = api.getIngreso(id)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR)
        }
    }
}
