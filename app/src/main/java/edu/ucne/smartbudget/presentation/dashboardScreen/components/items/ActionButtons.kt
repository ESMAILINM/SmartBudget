package edu.ucne.smartbudget.presentation.dashboardScreen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActionButtons(
    onAddExpenseClicked: () -> Unit,
    onAddIncomeClicked: () -> Unit,
    onViewReportsClicked: () -> Unit,
    onViewGoalsClicked: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardButton(
                text = "Agregar Gasto",
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                onClick = onAddExpenseClicked,
                modifier = Modifier.weight(1f)
            )

            DashboardButton(
                text = "Agregar Ingreso",
                containerColor = colors.primaryContainer,
                contentColor = colors.onPrimaryContainer,
                onClick = onAddIncomeClicked,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardButton(
                text = "Ver Reportes",
                containerColor = colors.primaryContainer,
                contentColor = colors.onPrimaryContainer,
                onClick = onViewReportsClicked,
                modifier = Modifier.weight(1f)
            )

            DashboardButton(
                text = "Ver Metas",
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                onClick = onViewGoalsClicked,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DashboardButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
