package edu.ucne.smartbudget.data.syncworker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import androidx.work.CoroutineWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.repository.GastosRepository

@HiltWorker
class GastoSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val gastosRepository: GastosRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        when (gastosRepository.postPendingGastos()) {
            is Resource.Error -> return Result.retry()
            else -> { }
        }

        when (gastosRepository.postPendingUpdates()) {
            is Resource.Error -> return Result.retry()
            else -> { }
        }

        return when (gastosRepository.postPendingDeletes()) {
            is Resource.Success -> Result.success()
            is Resource.Error -> Result.retry()
            else -> Result.failure()
        }
    }
}
