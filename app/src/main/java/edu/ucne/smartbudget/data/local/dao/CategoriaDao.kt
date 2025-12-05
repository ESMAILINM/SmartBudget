package edu.ucne.smartbudget.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.smartbudget.data.local.entities.CategoriasEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    @Query("SELECT * FROM categorias")
    fun observeCategorias(): Flow<List<CategoriasEntity>>

    @Query("SELECT * FROM categorias WHERE categoriaId = :id")
    suspend fun getCategoria(id: String): CategoriasEntity?

    @Upsert
    suspend fun upsertCategoria(categoria: CategoriasEntity)

    @Query("DELETE FROM categorias WHERE categoriaId = :id")
    suspend fun deleteCategoria(id: String)

    @Query("SELECT * FROM categorias WHERE isPendingCreate = 1")
    suspend fun getPendingCreateCategorias(): List<CategoriasEntity>

    @Query("SELECT * FROM categorias WHERE isPendingUpdate = 1")
    suspend fun getPendingUpdate(): List<CategoriasEntity>

    @Query("SELECT * FROM categorias WHERE isPendingDelete = 1")
    suspend fun getPendingDelete(): List<CategoriasEntity>
}
