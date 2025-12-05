package edu.ucne.smartbudget.data.syncworker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.repository.CategoriaRepository

@HiltWorker
class CategoriaSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val categoriaRepository: CategoriaRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        when (categoriaRepository.postPendingCategorias()) {
            is Resource.Error -> return Result.retry()
            else -> { }
        }

        when (categoriaRepository.postPendingUpdates()) {
            is Resource.Error -> return Result.retry()
            else -> { }
        }

        return when (categoriaRepository.postPendingDeletes()) {
            is Resource.Error -> Result.retry()
            else -> Result.success()
        }
    }
}
