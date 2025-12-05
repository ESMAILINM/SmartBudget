package edu.ucne.smartbudget.data.syncworker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import androidx.work.CoroutineWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.repository.IngresoRepository

@HiltWorker
class IngresoSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val ingresoRepository: IngresoRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        when (ingresoRepository.postPendingIngresos()) {
            is Resource.Error -> return Result.retry()
        }

        when (ingresoRepository.postPendingUpdates()) {
            is Resource.Error -> return Result.retry()
        }

        return when (ingresoRepository.postPendingDeletes()) {
            is Resource.Error -> Result.retry()
            else -> Result.success()
        }
    }
}
