package edu.ucne.smartbudget.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.smartbudget.data.local.entities.UsuariosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios")
    fun observeUsuarios(): Flow<List<UsuariosEntity>>

    @Query("SELECT * FROM usuarios WHERE usuarioId = :id")
    suspend fun getUsuario(id: String): UsuariosEntity?

    @Query("SELECT * FROM usuarios WHERE userName = :username LIMIT 1")
    suspend fun getUsuarioByUsername(username: String): UsuariosEntity?

    @Query("SELECT * FROM usuarios WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getUsuarioByRemoteId(remoteId: Int): UsuariosEntity?

    @Upsert
    suspend fun upsertUsuario(usuario: UsuariosEntity)

    @Query("DELETE FROM usuarios WHERE usuarioId = :id")
    suspend fun deleteUsuario(id: String)

    @Query("SELECT * FROM usuarios WHERE isPendingCreate = 1")
    suspend fun getPendingCreateUsuarios(): List<UsuariosEntity>

    @Query("SELECT * FROM usuarios WHERE isPendingUpdate = 1")
    suspend fun getPendingUpdate(): List<UsuariosEntity>

    @Query("SELECT * FROM usuarios WHERE isPendingDelete = 1")
    suspend fun getPendingDelete(): List<UsuariosEntity>
}
