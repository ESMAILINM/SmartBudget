package edu.ucne.smartbudget.domain.repository

import edu.ucne.smartbudget.domain.model.Usuarios
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {

         fun getUsuarios(): Flow<List<Usuarios>>

        suspend fun getUsuario(id: Int): Usuarios?

        suspend fun insertUsuario(usuario: Usuarios)

        suspend fun updateUsuario(usuario: Usuarios)
}