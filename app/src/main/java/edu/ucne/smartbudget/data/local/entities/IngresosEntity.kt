package edu.ucne.smartbudget.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "ingresos")
data class IngresosEntity(
    @PrimaryKey val ingresoId: String = UUID.randomUUID().toString(),
    val remoteId: Int? = null,
    val monto: Double,
    val fecha: String,
    val descripcion: String,
    val categoriaId: String,
    val usuarioId: String,
    val isPendingCreate: Boolean = false,
    val isPendingUpdate: Boolean = false,
    val isPendingDelete: Boolean = false
)
