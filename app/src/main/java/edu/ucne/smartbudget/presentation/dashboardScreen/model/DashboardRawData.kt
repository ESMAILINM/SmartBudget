package edu.ucne.smartbudget.presentation.dashboardScreen.model

import edu.ucne.smartbudget.domain.model.Categorias
import edu.ucne.smartbudget.domain.model.Gastos
import edu.ucne.smartbudget.domain.model.Ingresos
import edu.ucne.smartbudget.domain.model.Metas

data class DashboardRawData(
    val ingresos: List<Ingresos>,
    val gastos: List<Gastos>,
    val metas: List<Metas>,
    val categorias: List<Categorias>
)