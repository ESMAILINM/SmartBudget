package edu.ucne.smartbudget.presentation.ingreso.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.MiscellaneousServices
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.smartbudget.domain.model.Categorias
import edu.ucne.smartbudget.domain.model.Ingresos
import edu.ucne.smartbudget.ui.components.formatCurrency


@Composable
fun IngresoItemRow(
    ingreso: Ingresos,
    onClick: () -> Unit,
    colors: ColorScheme,
    categorias: List<Categorias>,
    currencyCode: String
) {
    val categoriaName = categorias
        .firstOrNull { it.categoriaId == ingreso.categoriaId }
        ?.nombre ?: "Ingreso"

    val icon = when (categoriaName) {
        "Salario" -> Icons.Outlined.AttachMoney
        "Ventas" -> Icons.Outlined.ShoppingCart
        "Inversiones" -> Icons.Outlined.TrendingUp
        "Regalos" -> Icons.Outlined.CardGiftcard
        "Prestamo" -> Icons.Outlined.MonetizationOn
        "Ahorro" -> Icons.Outlined.Savings
        "Otros" -> Icons.Outlined.MiscellaneousServices
        else -> Icons.Outlined.AttachMoney
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(48.dp)
                .background(color = colors.primaryContainer, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(24.dp))
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ingreso.descripcion ?: "Ingreso",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                color = colors.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = categoriaName,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = colors.onSurfaceVariant
            )
        }

        Text(
            text = "+${formatCurrency(ingreso.monto, currencyCode)}",
            color = colors.primary,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}