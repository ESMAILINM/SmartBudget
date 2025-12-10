package edu.ucne.smartbudget.presentation.dashboardScreen

import edu.ucne.smartbudget.presentation.dashboardScreen.components.items.CategoryProgress
import edu.ucne.smartbudget.presentation.dashboardScreen.model.DashboardUiData
import edu.ucne.smartbudget.presentation.dashboardScreen.model.SummaryData
import edu.ucne.smartbudget.presentation.dashboardScreen.model.TransactionItem
import edu.ucne.smartbudget.presentation.dashboardScreen.model.TrendData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetDashboardDataUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    operator fun invoke(usuarioId: String): Flow<DashboardUiData> {
        return repository.getDashboardData(usuarioId).map { raw ->

            val safeDateParser: (String?) -> LocalDate? = { dateString ->
                runCatching {
                    dateString?.let { LocalDate.parse(it) }
                }.getOrNull()
            }

            val transacciones = buildList {
                addAll(
                    raw.ingresos.map { ingreso ->
                        TransactionItem(
                            id = ingreso.ingresoId,
                            description = ingreso.descripcion ?: "Ingreso",
                            amount = ingreso.monto,
                            date = ingreso.fecha,
                            categoryName = "Ingreso",
                            isExpense = false,
                            categoryColor = null,
                            categoryIcon = null
                        )
                    }
                )
                addAll(
                    raw.gastos.map { gasto ->
                        val nombreCategoria = raw.categorias
                            .find { it.categoriaId == gasto.categoriaId }?.nombre ?: "Gasto"

                        TransactionItem(
                            id = gasto.gastoId,
                            description = gasto.descripcion ?: "Gasto",
                            amount = gasto.monto * -1,
                            date = gasto.fecha,
                            categoryName = nombreCategoria,
                            isExpense = true,
                            categoryColor = null,
                            categoryIcon = null
                        )
                    }
                )
            }.sortedByDescending { it.date }

            val totalIngresos = raw.ingresos.sumOf { it.monto }
            val totalGastos = raw.gastos.sumOf { it.monto }
            val balance = totalIngresos - totalGastos

            val summary = SummaryData(
                totalIngresos = totalIngresos,
                totalGastos = totalGastos,
                balance = balance,
                transaccionesRecientes = transacciones.take(4)
            )

            val today = LocalDate.now()
            val months = (0..2).map { today.minusMonths((2 - it).toLong()) }
            val formatter = DateTimeFormatter.ofPattern("MMM")

            val trend = months.map { month ->
                val montoMes = raw.ingresos
                    .filter { ingreso ->
                        val ingresoDate = safeDateParser(ingreso.fecha)
                        ingresoDate?.month == month.month && ingresoDate.year == month.year
                    }
                    .sumOf { it.monto }

                TrendData(
                    label = month.format(formatter),
                    monto = montoMes
                )
            }

            val totalGastosBreakdown = raw.gastos.sumOf { it.monto }.takeIf { it > 0 } ?: 1.0

            val breakdown = raw.categorias
                .filter { it.tipoId == 2 }
                .mapNotNull { cat ->
                    val montoCategoria = raw.gastos
                        .filter { it.categoriaId == cat.categoriaId }
                        .sumOf { it.monto }

                    if (montoCategoria > 0) {
                        CategoryProgress(
                            categoria = cat.nombre,
                            monto = montoCategoria,
                            porcentaje = ((montoCategoria / totalGastosBreakdown) * 100)
                        )
                    } else null
                }

            DashboardUiData(
                summary = summary,
                trend = trend,
                breakdown = breakdown,
                recentTransactions = transacciones
            )
        }
    }
}
