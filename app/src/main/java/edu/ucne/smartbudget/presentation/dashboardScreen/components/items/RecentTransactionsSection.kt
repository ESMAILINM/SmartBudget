package edu.ucne.smartbudget.presentation.dashboardScreen.components.items

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edu.ucne.smartbudget.presentation.dashboardScreen.model.TransactionItem
import edu.ucne.smartbudget.ui.components.formatCurrency
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun RecentTransactionsSection(
    items: List<TransactionItem>,
    currencyCode: String,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val visibleItems = if (isExpanded) items else items.take(4)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Transacciones Recientes",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (items.size > 4) {
                TextButton(
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Text(
                        text = if (isExpanded) "Ver menos" else "Ver más",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay movimientos recientes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                modifier = Modifier.animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                visibleItems.forEach { item ->
                    TransactionItemRow(item = item, currencyCode = currencyCode)
                }
            }
        }
    }
}

@Composable
fun TransactionItemRow(
    item: TransactionItem,
    currencyCode: String
) {
    val isExpense = item.isExpense

    val icon: ImageVector
    val iconTint: Color
    val iconBackground: Color
    val amountColor: Color
    val amountPrefix: String

    if (isExpense) {
        icon = Icons.Rounded.ArrowDownward
        iconTint = MaterialTheme.colorScheme.error
        iconBackground = MaterialTheme.colorScheme.errorContainer
        amountColor = MaterialTheme.colorScheme.onSurface
        amountPrefix = ""
    } else {
        icon = Icons.Rounded.ArrowUpward
        iconTint = MaterialTheme.colorScheme.primary
        iconBackground = MaterialTheme.colorScheme.primaryContainer
        amountColor = MaterialTheme.colorScheme.primary
        amountPrefix = "+"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${item.categoryName} • ${formatRelativeTime(item.date)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "$amountPrefix${formatCurrency(item.amount, currencyCode)}",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = amountColor
        )
    }
}

fun formatRelativeTime(dateString: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val transactionDate = LocalDate.parse(dateString.take(10), formatter)
        val today = LocalDate.now()

        when (ChronoUnit.DAYS.between(transactionDate, today)) {
            0L -> "Hoy"
            1L -> "Ayer"
            in 2L..6L -> "${ChronoUnit.DAYS.between(transactionDate, today)} días"
            else -> transactionDate.format(DateTimeFormatter.ofPattern("dd MMM"))
        }
    } catch (e: Exception) {
        dateString
    }
}
