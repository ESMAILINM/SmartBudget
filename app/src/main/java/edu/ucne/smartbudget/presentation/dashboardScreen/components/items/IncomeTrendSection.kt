package edu.ucne.smartbudget.presentation.dashboardScreen.components.items

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.smartbudget.presentation.dashboardScreen.model.TrendData
import edu.ucne.smartbudget.ui.components.formatCurrency

@Composable
fun IncomeTrendSection(
    trend: List<TrendData>,
    currencyCode: String
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val chartColor = colors.primary
    val cardBackgroundColor = colors.surfaceContainer
    val titleColor = colors.onSurface
    val subTitleColor = colors.onSurfaceVariant
    val gridLineColor = colors.outlineVariant.copy(alpha = 0.5f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor,
            contentColor = titleColor
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Tendencia de Ingresos",
                        style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = titleColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Últimos 3 meses",
                        style = typography.bodySmall,
                        color = subTitleColor
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    val totalAmount = trend.sumOf { it.monto }
                    Text(
                        text = "+${formatCurrency(totalAmount, currencyCode)}",
                        style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = titleColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "+10.8%",
                        style = typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = chartColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 12.dp, bottom = 20.dp)
                ) {
                    val labelsY = listOf("6k", "5k", "3k", "2k", "0k")
                    labelsY.forEach { label ->
                        Text(
                            text = label,
                            style = typography.labelSmall,
                            color = subTitleColor,
                            fontSize = 10.sp
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    val valores = if (trend.isEmpty()) listOf(2000.0, 3500.0, 4200.0) else trend.map { it.monto }

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height - 20.dp.toPx()

                        val gridSteps = 4
                        val stepHeight = height / gridSteps
                        for (i in 0..gridSteps) {
                            val y = i * stepHeight
                            drawLine(
                                color = gridLineColor,
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        }

                        val maxData = valores.maxOrNull() ?: 1.0
                        val maxVal = if (maxData == 0.0) 1.0 else maxData * 1.2
                        if (valores.isEmpty()) return@Canvas

                        val stepX = width / (valores.size - 1).coerceAtLeast(1)
                        val points = valores.mapIndexed { index, value ->
                            val x = index * stepX
                            val y = height - ((value / maxVal).toFloat() * height)
                            Offset(x, y)
                        }

                        val fillPath = Path().apply {
                            if (points.isNotEmpty()) {
                                moveTo(points.first().x, height)
                                points.forEach { lineTo(it.x, it.y) }
                                lineTo(points.last().x, height)
                                close()
                            }
                        }

                        val brush = Brush.verticalGradient(
                            colors = listOf(
                                chartColor.copy(alpha = 0.25f),
                                chartColor.copy(alpha = 0.0f)
                            ),
                            startY = points.minOfOrNull { it.y } ?: 0f,
                            endY = height
                        )
                        drawPath(path = fillPath, brush = brush)

                        val strokePath = Path().apply {
                            points.forEachIndexed { i, p ->
                                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                            }
                        }

                        drawPath(
                            path = strokePath,
                            color = chartColor,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val labels = if (trend.isEmpty()) listOf("Ene", "Feb", "Mar") else trend.map { it.label }
                        labels.forEachIndexed { index, label ->
                            Text(
                                text = label,
                                style = typography.labelMedium,
                                color = subTitleColor,
                                textAlign = when (index) {
                                    0 -> TextAlign.Start
                                    labels.size - 1 -> TextAlign.End
                                    else -> TextAlign.Center
                                },
                                modifier = Modifier.width(40.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
