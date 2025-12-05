package edu.ucne.smartbudget.data

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class MyWorkerFactory @Inject constructor(
    private val hiltWorkerFactory: HiltWorkerFactory
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return hiltWorkerFactory.createWorker(appContext, workerClassName, workerParameters)
    }
}
