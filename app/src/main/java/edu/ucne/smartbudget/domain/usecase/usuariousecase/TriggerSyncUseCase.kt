package edu.ucne.smartbudget.domain.usecase.usuariousecase

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.ucne.smartbudget.data.syncworker.UsuarioSyncWorker
import javax.inject.Inject

class TriggerSyncUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke() {
        val request = OneTimeWorkRequestBuilder<UsuarioSyncWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }
}