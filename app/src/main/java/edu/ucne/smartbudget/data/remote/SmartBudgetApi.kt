package edu.ucne.smartbudget.data.remote

import edu.ucne.smartbudget.data.remote.dto.categoriasdto.CategoriaRequest
import edu.ucne.smartbudget.data.remote.dto.categoriasdto.CategoriaResponse
import edu.ucne.smartbudget.data.remote.dto.gastosdto.GastoRequest
import edu.ucne.smartbudget.data.remote.dto.gastosdto.GastoResponse
import edu.ucne.smartbudget.data.remote.dto.imagenesdto.ImagenRequest
import edu.ucne.smartbudget.data.remote.dto.imagenesdto.ImagenResponse
import edu.ucne.smartbudget.data.remote.dto.ingresosdto.IngresoRequest
import edu.ucne.smartbudget.data.remote.dto.ingresosdto.IngresoResponse
import edu.ucne.smartbudget.data.remote.dto.metasdto.MetaRequest
import edu.ucne.smartbudget.data.remote.dto.metasdto.MetaResponse
import edu.ucne.smartbudget.data.remote.dto.usuariosdto.UsuarioRequest
import edu.ucne.smartbudget.data.remote.dto.usuariosdto.UsuarioResponse
import retrofit2.Response
import retrofit2.http.*

interface SmartBudgetApi {

    @GET("api/Usuarios")
    suspend fun getUsuarios(): Response<List<UsuarioResponse>>

    @GET("api/Usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Response<UsuarioResponse?>

    @POST("api/Usuarios")
    suspend fun createUsuario(@Body usuario: UsuarioRequest): Response<UsuarioResponse>

    @PUT("api/Usuarios/{id}")
    suspend fun updateUsuario(@Path("id") id: Int, @Body usuario: UsuarioRequest): Response<Unit>

    @DELETE("api/Usuarios/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int): Response<Unit>


    @GET("api/Categorias")
    suspend fun getCategorias(): Response<List<CategoriaResponse>>

    @GET("api/Categorias/{id}")
    suspend fun getCategoria(@Path("id") id: Int): Response<CategoriaResponse?>

    @POST("api/Categorias")
    suspend fun createCategoria(@Body categoria: CategoriaRequest): Response<CategoriaResponse>

    @PUT("api/Categorias/{id}")
    suspend fun updateCategoria(@Path("id") id: Int, @Body categoria: CategoriaRequest): Response<Unit>

    @DELETE("api/Categorias/{id}")
    suspend fun deleteCategoria(@Path("id") id: Int): Response<Unit>


    @GET("api/Ingresos")
    suspend fun getIngresos(): Response<List<IngresoResponse>>

    @GET("api/Ingresos/{id}")
    suspend fun getIngreso(@Path("id") id: Int): Response<IngresoResponse?>

    @POST("api/Ingresos")
    suspend fun createIngreso(@Body ingreso: IngresoRequest): Response<IngresoResponse>

    @PUT("api/Ingresos/{id}")
    suspend fun updateIngreso(@Path("id") id: Int, @Body ingreso: IngresoRequest): Response<Unit>

    @DELETE("api/Ingresos/{id}")
    suspend fun deleteIngreso(@Path("id") id: Int): Response<Unit>


    @GET("api/Gastos")
    suspend fun getGastos(): Response<List<GastoResponse>>

    @GET("api/Gastos/{id}")
    suspend fun getGasto(@Path("id") id: Int): Response<GastoResponse?>

    @POST("api/Gastos")
    suspend fun createGasto(@Body gasto: GastoRequest): Response<GastoResponse>

    @PUT("api/Gastos/{id}")
    suspend fun updateGasto(@Path("id") id: Int, @Body gasto: GastoRequest): Response<Unit>

    @DELETE("api/Gastos/{id}")
    suspend fun deleteGasto(@Path("id") id: Int): Response<Unit>


    @GET("api/Metas")
    suspend fun getMetas(): Response<List<MetaResponse>>

    @GET("api/Metas/{id}")
    suspend fun getMeta(@Path("id") id: Int): Response<MetaResponse?>

    @POST("api/Metas")
    suspend fun createMeta(@Body meta: MetaRequest): Response<MetaResponse>

    @PUT("api/Metas/{id}")
    suspend fun updateMeta(@Path("id") id: Int, @Body meta: MetaRequest): Response<Unit>

    @DELETE("api/Metas/{id}")
    suspend fun deleteMeta(@Path("id") id: Int): Response<Unit>


    @POST("api/Imagenes")
    suspend fun createImagen(@Body imagen: ImagenRequest): Response<ImagenResponse>

    @DELETE("api/Imagenes/{id}")
    suspend fun deleteImagen(@Path("id") id: Int): Response<Unit>
}
