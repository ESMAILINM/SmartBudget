package edu.ucne.smartbudget.data.remote.repository

import edu.ucne.smartbudget.data.local.dao.UsuarioDao
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.mapper.toDomain
import edu.ucne.smartbudget.data.mapper.toEntity
import edu.ucne.smartbudget.data.mapper.toRequest
import edu.ucne.smartbudget.data.remote.remotedatasource.UsuariosRemoteDataSource
import edu.ucne.smartbudget.domain.model.Usuarios
import edu.ucne.smartbudget.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UsuarioRepositoryImpl @Inject constructor(
    private val localDataSource: UsuarioDao,
    private val remoteDataSource: UsuariosRemoteDataSource
) : UsuarioRepository {

    override suspend fun insertUsuario(usuario: Usuarios): Resource<Usuarios> {
        val pending = usuario.copy(
            isPendingCreate = true,
            isPendingUpdate = false,
            isPendingDelete = false,
            remoteId = null
        )

        localDataSource.upsertUsuario(
            pending.toEntity(
                isPendingCreate = true,
                isPendingUpdate = false,
                isPendingDelete = false
            )
        )

        return Resource.Success(pending)
    }

    override suspend fun updateUsuario(usuario: Usuarios): Resource<Unit> {
        val updated = if (usuario.remoteId == null) {
            usuario.copy(isPendingCreate = true, isPendingUpdate = false)
        } else {
            usuario.copy(isPendingUpdate = true)
        }
        localDataSource.upsertUsuario(updated.toEntity())
        return Resource.Success(Unit)
    }

    override suspend fun deleteUsuario(id: String): Resource<Unit> {
        val entity = localDataSource.getUsuario(id) ?: return Resource.Error("No encontrado")
        val domain = entity.toDomain()

        if (domain.remoteId == null) {
            localDataSource.deleteUsuario(id)
            return Resource.Success(Unit)
        }

        val pending = domain.copy(isPendingDelete = true)
        localDataSource.upsertUsuario(pending.toEntity())
        return Resource.Success(Unit)
    }

    override suspend fun postPendingUsuarios(): Resource<Unit> {
        val pending = localDataSource.getPendingCreateUsuarios()

        for (item in pending) {
            val request = item.toDomain().toRequest()

            when (val result = remoteDataSource.createUsuario(request)) {
                is Resource.Success -> {
                    val remote = result.data ?: continue
                    val synced = item.copy(
                        remoteId = remote.usuarioId,
                        isPendingCreate = false
                    )
                    localDataSource.upsertUsuario(synced)
                }
                else -> continue
            }
        }

        return Resource.Success(Unit)
    }

    override suspend fun postPendingUpdates(): Resource<Unit> {
        val pendingUpdates = localDataSource.getPendingUpdate()

        for (item in pendingUpdates) {
            val remoteId = item.remoteId ?: continue
            val request = item.toDomain().toRequest()

            when (val result = remoteDataSource.updateUsuario(remoteId, request)) {
                is Resource.Success -> {
                    val synced = item.copy(
                        isPendingUpdate = false
                    )
                    localDataSource.upsertUsuario(synced)
                }
                is Resource.Error -> {
                    return Resource.Error(result.message ?: "Error desconocido")
                }
                is Resource.Loading -> continue
            }
        }

        return Resource.Success(Unit)
    }

    override suspend fun postPendingDeletes(): Resource<Unit> {
        val pending = localDataSource.getPendingDelete()

        for (item in pending) {
            if (item.remoteId == null) {
                localDataSource.deleteUsuario(item.usuarioId)
                continue
            }

            when (val result = remoteDataSource.deleteUsuario(item.remoteId)) {
                is Resource.Success -> localDataSource.deleteUsuario(item.usuarioId)
                is Resource.Error -> {
                    if (result.message?.contains("404") == true)
                        localDataSource.deleteUsuario(item.usuarioId)
                }
                else -> continue
            }
        }

        return Resource.Success(Unit)
    }

    override fun getUsuarios(): Flow<List<Usuarios>> =
        localDataSource.observeUsuarios().map { list ->
            list.map { it.toDomain() }
                .filter { !it.isPendingDelete }
        }

    override suspend fun getUsuario(id: String): Resource<Usuarios?> {
        val local = localDataSource.getUsuario(id)?.toDomain()
        if (local != null) return Resource.Success(local)

        val remoteId = id.toIntOrNull() ?: return Resource.Error("No encontrado")

        return when (val res = remoteDataSource.getUsuario(remoteId)) {
            is Resource.Success -> {
                val remote = res.data ?: return Resource.Error("Respuesta vacía del servidor")

                val existingLocal = localDataSource.getUsuarioByRemoteId(remote.usuarioId)

                if (existingLocal != null) {
                    val updatedEntity = remote.toEntity(existingLocal.usuarioId)
                    localDataSource.upsertUsuario(updatedEntity)
                    Resource.Success(updatedEntity.toDomain())
                } else {
                    val uuid = java.util.UUID.randomUUID().toString()
                    val entity = remote.toEntity(uuid)
                    localDataSource.upsertUsuario(entity)
                    Resource.Success(entity.toDomain())
                }
            }
            is Resource.Error -> Resource.Error(res.message ?: "Error remoto")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override fun getUsuarioActual(): Flow<Usuarios?> =
        localDataSource.observeUsuarios()
            .map { list -> list.firstOrNull()?.toDomain() }

    override suspend fun login(username: String, password: String): Resource<Usuarios> {
        val usuarioEntity = localDataSource.getUsuarioByUsername(username)

        return if (usuarioEntity != null && usuarioEntity.password == password) {
            Resource.Success(usuarioEntity.toDomain())
        } else {
            Resource.Error("Usuario o contraseña incorrectos")
        }
    }
}