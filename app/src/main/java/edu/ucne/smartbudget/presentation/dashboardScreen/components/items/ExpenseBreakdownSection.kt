package edu.ucne.smartbudget.presentation.dashboardScreen.components.items

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.smartbudget.ui.components.formatCurrency

@Composable
fun ExpenseBreakdownSection(
    data: List<CategoryProgress>,
    currencyCode: String
) {
    val colors = MaterialTheme.colorScheme

    val cardBackground = colors.surfaceContainerLow
    val titleColor = colors.onSurface
    val subTitleColor = colors.onSurfaceVariant
    val barColor = colors.primary
    val totalLabelColor = colors.error
    val gridLineColor = colors.outlineVariant.copy(alpha = 0.5f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground,
            contentColor = titleColor
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Desglose de Gastos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        ),
                        color = titleColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Este mes",
                        style = MaterialTheme.typography.bodySmall,
                        color = subTitleColor
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    val total = data.sumOf { it.monto }
                    Text(
                        text = formatCurrency(total, currencyCode),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = titleColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = totalLabelColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier.height(180.dp)) {

                val maxDataValue = if (data.isEmpty()) 1000.0 else (data.maxOf { it.monto } * 1.2)

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 12.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    val steps = 4
                    for (i in 0..steps) {
                        val value = maxDataValue * (1.0 - (i.toDouble() / steps))
                        val label = formatCurrency(value, currencyCode).substringBeforeLast(".")

                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = subTitleColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()) {
                    if (data.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Sin datos", style = MaterialTheme.typography.bodySmall, color = subTitleColor)
                        }
                    } else {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val chartHeight = size.height - 20.dp.toPx()
                            val chartWidth = size.width

                            val columnWidth = chartWidth / data.size
                            val barWidth = columnWidth * 0.6f

                            val gridSteps = 4
                            val stepHeight = chartHeight / gridSteps
                            for (i in 0..gridSteps) {
                                val y = i * stepHeight
                                drawLine(
                                    color = gridLineColor,
                                    start = Offset(0f, y),
                                    end = Offset(chartWidth, y),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                            }

                            data.forEachIndexed { index, item ->
                                val barHeight = ((item.monto / maxDataValue) * chartHeight).toFloat()
                                val x = (index * columnWidth) + (columnWidth - barWidth) / 2
                                val y = chartHeight - barHeight

                                drawRoundRect(
                                    color = barColor,
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(12f, 12f)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            data.forEach { item ->
                                Text(
                                    text = item.categoria.take(10),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = titleColor,
                                    maxLines = 1,
                                    modifier = Modifier.width(60.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.take(3).forEachIndexed { index, item ->
                    val dotColor = when (index) {
                        0 -> colors.primary
                        1 -> colors.primaryContainer
                        else -> colors.tertiary
                    }

                    LegendItem(
                        dotColor = dotColor,
                        label = item.categoria,
                        percentage = "${item.porcentaje.toInt()}%",
                        textColor = titleColor,
                        subTextColor = subTitleColor
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    dotColor: Color,
    label: String,
    percentage: String,
    textColor: Color,
    subTextColor: Color
) {
    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(dotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = subTextColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = percentage,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            color = textColor,
            modifier = Modifier.padding(start = 18.dp)
        )
    }
}
