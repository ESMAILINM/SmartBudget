package edu.ucne.smartbudget

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.work.Configuration
import edu.ucne.smartbudget.data.MyWorkerFactory
import jakarta.inject.Inject

@HiltAndroidApp
class SmartBudget : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: MyWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}