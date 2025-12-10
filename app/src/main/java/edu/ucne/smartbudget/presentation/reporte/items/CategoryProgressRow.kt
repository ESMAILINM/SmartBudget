package edu.ucne.smartbudget.presentation.reporte.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun CategoryProgressRow(item: CategoryProgress) {
    val colors = MaterialTheme.colorScheme

    val safeProgress = item.percentage.coerceIn(0.0, 1.0)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                item.name,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurface
            )

            Text(
                "${(safeProgress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurface
            )
        }

        Spacer(Modifier.height(6.dp))

        val linearProgress = item.percentage
            .coerceIn(0.01, 1.0)
            .toFloat()

        LinearProgressIndicator(
            progress = linearProgress,
            color = colors.primary,
            trackColor = colors.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}