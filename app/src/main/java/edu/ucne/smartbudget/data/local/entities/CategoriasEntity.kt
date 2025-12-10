package edu.ucne.smartbudget.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "categorias")
data class CategoriasEntity(
    @PrimaryKey val categoriaId: String = UUID.randomUUID().toString(),
    val remoteId: Int? = null,
    val nombre: String,
    val tipoId: Int,
    val isPendingCreate: Boolean = false,
    val isPendingUpdate: Boolean = false,
    val isPendingDelete: Boolean = false
)
