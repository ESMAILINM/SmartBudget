package edu.ucne.smartbudget.domain.usecase.ingresosusecase

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.ucne.smartbudget.data.syncworker.IngresoSyncWorker
import javax.inject.Inject

class TriggerSyncIngresosUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke() {
        val request = OneTimeWorkRequestBuilder<IngresoSyncWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
