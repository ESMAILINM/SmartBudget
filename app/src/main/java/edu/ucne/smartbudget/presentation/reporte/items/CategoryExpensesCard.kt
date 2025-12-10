package edu.ucne.smartbudget.presentation.reporte.items

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.smartbudget.presentation.reporte.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun CategoryExpensesCard(
    state: ReporteUiState,
) {
    val colors = MaterialTheme.colorScheme
    val cardBgColor = colors.surfaceVariant.copy(alpha = 0.3f)

    var expanded by remember { mutableStateOf(false) }

    val isIngreso = state.selectedTypeFilter == "Ingresos"

    val titulo = if (isIngreso) {
        "Ingresos por categoría"
    } else {
        "Gastos por categoría"
    }

    val total = if (isIngreso) state.totalIngresos else state.totalGastos

    val porcentaje = if (isIngreso) {
        state.ingresosPercentageChange
    } else {
        state.gastosPercentageChange
    }

    val porcentajeColor = if (porcentaje >= 0) colors.primary else colors.error

    val top3 = remember(state.categoriasData) {
        state.categoriasData.take(3)
    }

    val all = state.categoriasData

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                titulo,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "$${String.format("%,.0f", total)}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = colors.onSurface
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "${porcentaje.toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = porcentajeColor,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Text(
                "Este mes",
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            top3.forEach { item ->
                CategoryProgressRow(item)
                Spacer(Modifier.height(16.dp))
            }

            if (all.size > 3) {
                Text(
                    text = if (expanded) "Ver menos ▲" else "Ver más ▼",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.primary,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { expanded = !expanded }
                )
            }

            AnimatedVisibility(expanded) {
                Column {
                    all.drop(3).forEach { item ->
                        CategoryProgressRow(item)
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
