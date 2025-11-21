package edu.ucne.smartbudget.domain.repository

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Usuarios
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {

         fun getUsuarios(): Flow<List<Usuarios>>

        suspend fun getUsuario(id: Int): Resource<Usuarios?>

        suspend fun insertUsuario(usuario: Usuarios): Resource<Usuarios>

        suspend fun updateUsuario(usuario: Usuarios) : Resource<Unit>
}