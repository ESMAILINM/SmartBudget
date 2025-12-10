package edu.ucne.smartbudget.presentation.dashboardScreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.smartbudget.presentation.dashboardScreen.model.SummaryData
import edu.ucne.smartbudget.ui.components.formatCurrency

@Composable
fun SummarySection(
    summary: SummaryData?,
    currencyCode: String,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit
) {
    val balance = summary?.balance ?: 0.0
    val ingresos = summary?.totalIngresos ?: 0.0
    val gastos = summary?.totalGastos ?: 0.0

    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard(
            title = "Balance",
            value = formatCurrency(balance, currencyCode),
            backgroundColor = colors.secondaryContainer,
            titleColor = colors.onSurfaceVariant,
            valueColor = colors.onSurface,
            modifier = Modifier.weight(1f)
        )

        SummaryCard(
            title = "Ingresos",
            value = formatCurrency(ingresos, currencyCode),
            backgroundColor = colors.secondaryContainer,
            titleColor = colors.onSurfaceVariant,
            valueColor = colors.onSurface,
            onClick = onIncomeClick,
            modifier = Modifier.weight(1f)
        )

        SummaryCard(
            title = "Gastos",
            value = formatCurrency(gastos, currencyCode),
            backgroundColor = colors.secondaryContainer,
            titleColor = colors.onSurfaceVariant,
            valueColor = colors.onSurface,
            onClick = onExpenseClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    backgroundColor: Color,
    titleColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val dynamicFontSize = when {
        value.length > 12 -> 12.sp
        value.length > 9 -> 14.sp
        value.length > 7 -> 16.sp
        else -> 19.sp
    }

    Card(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = valueColor
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 12.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = titleColor.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = dynamicFontSize
                ),
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
