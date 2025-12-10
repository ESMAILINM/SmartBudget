package edu.ucne.smartbudget.presentation.reporte

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.ucne.smartbudget.presentation.reporte.items.CategoryProgress
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.smartbudget.presentation.reporte.items.CategoryExpensesCard
import edu.ucne.smartbudget.presentation.reporte.items.IncomeVsExpenseCard

@Composable
fun ReporteScreen(
    viewModel: ReporteViewModel = hiltViewModel(),
    onClose: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReporteContent(
        state = state,
        onEvent = { event ->
            if (event is ReporteUiEvent.OnClose) onClose()
            else viewModel.onEvent(event)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteContent(
    state: ReporteUiState,
    onEvent: (ReporteUiEvent) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colors.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Reportes",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(ReporteUiEvent.OnClose) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = colors.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Todo", "Ingresos", "Gastos").forEach { filter ->
                            CustomChip(
                                label = filter,
                                selected = state.selectedTypeFilter == filter,
                                onClick = { onEvent(ReporteUiEvent.SelectTypeFilter(filter)) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Este mes", "Mes pasado", "Este año").forEach { filter ->
                            CustomChip(
                                label = filter,
                                selected = state.selectedDateFilter == filter,
                                onClick = { onEvent(ReporteUiEvent.SelectDateFilter(filter)) }
                            )
                        }
                    }
                }
            }
            item { CategoryExpensesCard(state) }
            item { IncomeVsExpenseCard(state) }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun CustomChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val containerColor = if (selected) colors.primary else colors.surfaceVariant
    val contentColor = if (selected) colors.onPrimary else colors.onSurfaceVariant

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReporteScreenPreview() {
    val fakeState = ReporteUiState(
        categoriasData = listOf(
            CategoryProgress("Comida", 0.3),
            CategoryProgress("Transporte", 0.5),
            CategoryProgress("Entretenimiento", 0.2)
        ),
        totalGastos = 1500.0,
        gastosPercentageChange = -10.0,
        totalIngresos = 2000.0,
        ingresosPercentageChange = 20.0,
        totalNeto = 3500.0,
        netoPercentageChange = 10.0,
        selectedTypeFilter = "Todo",
        selectedDateFilter = "Este mes",
        isIngreso = true
    )

    MaterialTheme {
        ReporteContent(state = fakeState, onEvent = {})
    }
}