package edu.ucne.smartbudget.domain.repository

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Gastos
import kotlinx.coroutines.flow.Flow

interface GastosRepository {
    fun getGastos(usuarioId: String): Flow<List<Gastos>>

    suspend fun getGasto(id: String): Resource<Gastos?>

    suspend fun insertGasto(gasto: Gastos): Resource<Gastos>

    suspend fun updateGasto(gasto: Gastos): Resource<Unit>

    suspend fun deleteGasto(id: String): Resource<Unit>

    suspend fun postPendingGastos(): Resource<Unit>

    suspend fun postPendingDeletes(): Resource<Unit>

    suspend fun postPendingUpdates(): Resource<Unit>
}

