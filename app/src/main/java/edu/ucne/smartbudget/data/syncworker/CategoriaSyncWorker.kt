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
        when (val create = categoriaRepository.postPendingCategorias()) {
            is Resource.Error -> return Result.retry()
            is Resource.Success -> Unit
            else -> return Result.retry()
        }

        when (val update = categoriaRepository.postPendingUpdates()) {
            is Resource.Error -> return Result.retry()
            is Resource.Success -> Unit
            else -> return Result.retry()
        }

        return when (val delete = categoriaRepository.postPendingDeletes()) {
            is Resource.Success -> Result.success()
            is Resource.Error -> Result.retry()
            else -> Result.retry()
        }
    }
}