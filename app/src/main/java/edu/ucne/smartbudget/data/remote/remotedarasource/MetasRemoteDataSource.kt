package edu.ucne.smartbudget.data.remote.remotedatasource

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.SmartBudgetApi
import edu.ucne.smartbudget.data.remote.dto.metasdto.MetaRequest
import edu.ucne.smartbudget.data.remote.dto.metasdto.MetaResponse
import javax.inject.Inject

class MetasRemoteDataSource @Inject constructor(
    private val api: SmartBudgetApi
) {
    suspend fun insertMeta(request: MetaRequest): Resource<MetaResponse> {
        return try {
            val response = api.createMeta(request)
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

    suspend fun updateMeta(id: Int, request: MetaRequest): Resource<Unit> {
        return try {
            val response = api.updateMeta(id, request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun deleteMeta(id: Int): Resource<Unit> {
        return try {
            val response = api.deleteMeta(id)
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
