package edu.ucne.smartbudget.presentation.dashboardScreen

import edu.ucne.smartbudget.presentation.dashboardScreen.model.DashboardRawData
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getDashboardData(usuarioId: String): Flow<DashboardRawData>
}