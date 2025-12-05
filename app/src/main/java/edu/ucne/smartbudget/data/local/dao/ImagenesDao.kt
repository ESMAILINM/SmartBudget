package edu.ucne.smartbudget.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.smartbudget.data.local.entities.ImagenesEntity
@Dao
interface ImagenesDao {

    @Upsert
    suspend fun upsertImagen(imagen: ImagenesEntity)

    @Query("SELECT * FROM imagenes WHERE metaId = :metaId")
    suspend fun getImagesByMeta(metaId: String): List<ImagenesEntity>

    @Query("DELETE FROM imagenes WHERE imagenId = :imagenId")
    suspend fun deleteImagen(imagenId: String)

    @Query("DELETE FROM imagenes WHERE metaId = :metaId")
    suspend fun deleteImagesByMeta(metaId: String)

    @Query("UPDATE imagenes SET isPendingDelete = 1 WHERE metaId = :metaId")
    suspend fun markImagesForDelete(metaId: String)

    @Query("SELECT * FROM imagenes WHERE isPendingCreate = 1")
    suspend fun getPendingCreate(): List<ImagenesEntity>

    @Query("SELECT * FROM imagenes WHERE isPendingUpdate = 1")
    suspend fun getPendingUpdate(): List<ImagenesEntity>

    @Query("SELECT * FROM imagenes WHERE isPendingDelete = 1")
    suspend fun getPendingDelete(): List<ImagenesEntity>

    @Query("SELECT * FROM imagenes WHERE remoteId = :id LIMIT 1")
    suspend fun getImagen(id: String): ImagenesEntity?
}
