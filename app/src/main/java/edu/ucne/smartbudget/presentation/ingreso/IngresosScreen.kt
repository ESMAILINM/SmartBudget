package edu.ucne.smartbudget.presentation.ingreso


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.smartbudget.domain.model.Categorias
import edu.ucne.smartbudget.domain.model.Ingresos
import edu.ucne.smartbudget.presentation.ingreso.items.AddIngresoScreen
import edu.ucne.smartbudget.presentation.ingreso.items.IngresosContent
import edu.ucne.smartbudget.ui.theme.SmartBudgetTheme
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresosScreen(
    viewModel: IngresoViewModel = hiltViewModel(),
    onClose: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Todo") }
    var editingIngresoId by remember { mutableStateOf<String?>(null) }

    val nowYearMonth = remember { YearMonth.now() }
    val locale = Locale.getDefault()
    val monthFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy", locale) }
    val monthOptions = remember {
        (0..11).map {
            val ym = nowYearMonth.minusMonths(it.toLong())
            val label = ym.format(monthFormatter)
            label to ym
        }
    }
    val monthMap = remember(monthOptions) { monthOptions.toMap() }

    val filteredIngresos = remember(state.ingresos, selectedFilter) {
        fun filterByYearMonth(ym: YearMonth) =
            state.ingresos.filter { ingreso ->
                ingreso.fecha?.startsWith("${ym.year}-${ym.monthValue.toString().padStart(2, '0')}") ?: false
            }
        when (selectedFilter) {
            "Todo" -> state.ingresos
            "Este mes" -> filterByYearMonth(nowYearMonth)
            else -> monthMap[selectedFilter]?.let { filterByYearMonth(it) } ?: state.ingresos
        }
    }

    IngresosContent(
        state = state,
        filteredIngresos = filteredIngresos,
        currencyCode = currency,
        monthOptions = monthOptions.map { it.first },
        selectedFilter = selectedFilter,
        onFilterSelected = { selectedFilter = it },
        onEvent = viewModel::onEvent,
        onClose = onClose,
        onEditClick = { id ->
            editingIngresoId = id
            val ingreso = state.ingresos.first { it.ingresoId == id }
            viewModel.onEvent(IngresoUiEvent.Edit(ingreso))
        },
        onAddClick = {
            editingIngresoId = null
            viewModel.onEvent(IngresoUiEvent.ShowDialog)
        }
    )

    if (state.showDialog) {
        AddIngresoScreen(
            state = state,
            currencyCode = currency,
            onEvent = viewModel::onEvent,
            onDelete = {
                editingIngresoId?.let { id ->
                    viewModel.onEvent(IngresoUiEvent.Delete(id))
                    viewModel.onEvent(IngresoUiEvent.HideDialog)
                    editingIngresoId = null
                }
            },
            onClose = { viewModel.onEvent(IngresoUiEvent.HideDialog) },
            showDatePicker = showDatePicker,
            onShowDatePicker = { showDatePicker = true },
            onDismissDatePicker = { showDatePicker = false },
            isEditing = editingIngresoId != null
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun IngresosScreenPreview() {
    val fakeCategorias = listOf(
        Categorias("1", 1, "Salario", 1),
        Categorias("2", 1, "Ventas", 1)
    )
    val fakeIngresos = listOf(
        Ingresos("1", 1, 50000.00, "2025-11-01", "Quincena", "1", "2"),
        Ingresos("2", 2, 5500.00, "2025-11-05", "Venta Camisa", "1", "2")
    )
    val fakeState = IngresoUiState(
        ingresos = fakeIngresos,
        categorias = fakeCategorias
    )

    SmartBudgetTheme {
        IngresosContent(
            state = fakeState,
            filteredIngresos = fakeIngresos,
            currencyCode = "DOP",
            monthOptions = listOf("Noviembre 2025"),
            selectedFilter = "Todo",
            onFilterSelected = {},
            onEvent = {},
            onClose = {},
            onEditClick = {},
            onAddClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddIngresoScreenPreview() {
    val fakeCategorias = listOf(
        Categorias("1", 1, "Salario", 1),
        Categorias("2", 1, "Ventas", 1)
    )
    val fakeState = IngresoUiState(
        categorias = fakeCategorias,
        monto = "25000",
        descripcion = "Pago de Quincena",
        fecha = "2025-12-15"
    )

    SmartBudgetTheme {
        AddIngresoScreen(
            state = fakeState,
            currencyCode = "DOP",
            onEvent = {},
            onClose = {},
            showDatePicker = false,
            onShowDatePicker = {},
            onDismissDatePicker = {},
            isEditing = false,
            onDelete = {}
        )
    }
}
