package edu.ucne.smartbudget.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "usuarios")
data class UsuariosEntity(
    @PrimaryKey val usuarioId: String = UUID.randomUUID().toString(),
    val remoteId: Int? = null,
    val userName: String,
    val password: String,
    val isPendingCreate: Boolean = false,
    val isPendingUpdate: Boolean = false,
    val isPendingDelete: Boolean = false

)
