package edu.ucne.smartbudget.domain.usecase.metasusecase

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.ucne.smartbudget.data.syncworker.MetaSyncWorker
import javax.inject.Inject

class TriggerSyncMetaUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke() {
        val request = OneTimeWorkRequestBuilder<MetaSyncWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }
}