package edu.ucne.smartbudget.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "imagenes")
data class ImagenesEntity (
    @PrimaryKey val imagenId: String = UUID.randomUUID().toString(),
    val remoteId: Int? = null,
    val metaId: String,
    val url: String = "",
    val localUrl: String ="",
    val isPendingCreate: Boolean = false,
    val isPendingUpdate: Boolean = false,
    val isPendingDelete: Boolean = false

)