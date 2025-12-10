package edu.ucne.smartbudget.presentation.reporte.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.smartbudget.presentation.reporte.ReporteUiState
import java.util.Locale

@Composable
fun IncomeVsExpenseCard(state: ReporteUiState) {
    val colors = MaterialTheme.colorScheme
    val ingresos = state.totalIngresos
    val gastos = state.totalGastos
    val rangoTexto = obtenerRangoTexto(state.selectedDateFilter)
    val maxValue = maxOf(ingresos, gastos, 1.0)

    val ingresosRatio = (ingresos / maxValue).toFloat().coerceIn(0f, 1f)
    val gastosRatio = (gastos / maxValue).toFloat().coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                "Ingresos vs. Gastos",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Bottom) {

                Text(
                    "$${String.format(Locale.getDefault(), "%,.0f", state.totalNeto)}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = colors.onSurface
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    "${state.netoPercentageChange.toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (state.netoPercentageChange >= 0) colors.primary else colors.error,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Text(
                rangoTexto,
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.Bottom
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(ingresosRatio)
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.primary)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(gastosRatio)
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.error)
                )
            }
        }
    }
}
