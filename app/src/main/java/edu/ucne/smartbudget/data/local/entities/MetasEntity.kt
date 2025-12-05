package edu.ucne.smartbudget.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "metas")
data class MetasEntity(
    @PrimaryKey val metaId: String = UUID.randomUUID().toString(),
    val remoteId: Int? = null,
    val nombre: String,
    val contribucionMensual: Double,
    val monto: Double,
    val fecha: String,
    val emoji : String?,
    val usuarioId: String,
    val isPendingCreate: Boolean = false,
    val isPendingUpdate: Boolean = false,
    val isPendingDelete: Boolean = false

)
