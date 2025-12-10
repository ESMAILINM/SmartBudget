package edu.ucne.smartbudget.domain.repository

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Ingresos
import kotlinx.coroutines.flow.Flow

interface IngresoRepository {

    fun getIngresos(usuarioId: String): Flow<List<Ingresos>>

    suspend fun getIngreso(id: String): Resource<Ingresos?>

    suspend fun insertIngreso(ingreso: Ingresos): Resource<Ingresos>

    suspend fun deleteIngreso(id: String): Resource<Unit>

    suspend fun  upsertIngreso(ingreso: Ingresos): Resource<Unit>

    suspend fun postPendingIngresos(): Resource<Unit>

    suspend fun postPendingDeletes(): Resource<Unit>

    suspend fun postPendingUpdates(): Resource<Unit>
}
