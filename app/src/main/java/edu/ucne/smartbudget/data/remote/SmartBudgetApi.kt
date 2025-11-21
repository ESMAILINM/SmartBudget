package edu.ucne.smartbudget.data.remote

import edu.ucne.smartbudget.data.remote.dto.UsuariosDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SmartBudgetApi {

    @GET("api/Usuarios")
    suspend fun getUsuarios(): Response<List<UsuariosDto>>

    @GET ("api/Usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Response<UsuariosDto?>

    @PUT ("api/Usuarios/{id}")
    suspend fun updateUsuario(@Path("id") id: Int, @Body usuarios: UsuariosDto ): Response<Unit>

    @POST ("api/Usuarios")
    suspend fun createUsuario(@Body usuarios: UsuariosDto ): Response<UsuariosDto>
}