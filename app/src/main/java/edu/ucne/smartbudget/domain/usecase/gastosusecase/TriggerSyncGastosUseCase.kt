package edu.ucne.smartbudget.domain.usecase.gastosusecase

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.ucne.smartbudget.data.syncworker.GastoSyncWorker
import javax.inject.Inject

class TriggerSyncGastosUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke() {
        val request = OneTimeWorkRequestBuilder<GastoSyncWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }
}