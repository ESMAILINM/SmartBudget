package edu.ucne.smartbudget.domain.repository

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Usuarios
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {

        fun getUsuarios(): Flow<List<Usuarios>>

        suspend fun getUsuario(id: String): Resource<Usuarios?>

        suspend fun insertUsuario(usuario: Usuarios): Resource<Usuarios>

        suspend fun updateUsuario(usuario: Usuarios) : Resource<Unit>

        suspend fun deleteUsuario(id: String) : Resource<Unit>

        suspend fun postPendingUsuarios(): Resource<Unit>

        suspend fun postPendingDeletes(): Resource<Unit>

        suspend fun postPendingUpdates(): Resource<Unit>

        fun getUsuarioActual(): Flow<Usuarios?>

        suspend fun login(username: String, password: String): Resource<Usuarios>

}