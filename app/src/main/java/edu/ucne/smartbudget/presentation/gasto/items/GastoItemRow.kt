package edu.ucne.smartbudget.presentation.gasto.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.ucne.smartbudget.domain.model.Categorias
import edu.ucne.smartbudget.domain.model.Gastos
import edu.ucne.smartbudget.ui.components.categoryIcon
import edu.ucne.smartbudget.ui.components.formatCurrency


@Composable
fun GastoItemRow(
    gasto: Gastos,
    categorias: List<Categorias>,
    currencyCode: String,
    colors: ColorScheme,
    onClick: () -> Unit
) {
    val categoriaName = categorias
        .firstOrNull { it.categoriaId == gasto.categoriaId }
        ?.nombre ?: "Sin categoría"

    val iconVector = categoryIcon(categoriaName)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(colors.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = categoriaName,
                tint = colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = gasto.descripcion ?: "Sin descripción",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = categoriaName,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "-${formatCurrency(gasto.monto, currencyCode)}",
                style = MaterialTheme.typography.titleMedium,
                color = colors.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}