package edu.ucne.smartbudget.data.remote.remotedatasource

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.SmartBudgetApi
import edu.ucne.smartbudget.data.remote.dto.imagenesdto.ImagenRequest
import edu.ucne.smartbudget.data.remote.dto.imagenesdto.ImagenResponse
import javax.inject.Inject

class ImagenesRemoteDataSource @Inject constructor(
    private val api: SmartBudgetApi
) {
    suspend fun insertMeta(request: ImagenRequest): Resource<ImagenResponse> {
        return try {
            val response = api.createImagen(request)
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

    suspend fun deleteImagen(id: Int): Resource<Unit> {
        return try {
            val response = api.deleteImagen(id)
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
