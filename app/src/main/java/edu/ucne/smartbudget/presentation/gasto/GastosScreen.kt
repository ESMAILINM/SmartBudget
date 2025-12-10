package edu.ucne.smartbudget.presentation.gasto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.smartbudget.presentation.gasto.items.AddGastoScreen
import edu.ucne.smartbudget.presentation.gasto.items.FiltrosGastos
import edu.ucne.smartbudget.presentation.gasto.items.GastoItemRow
import edu.ucne.smartbudget.ui.components.AppPullRefresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(
    viewModel: GastoViewModel = hiltViewModel(),
    gastoId: String? = null,
    userId: String? = null,
    onAdd: () -> Unit = {},
    onEdit: (String) -> Unit = {},
    onClose: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(gastoId) {
        if (gastoId != null) {
            viewModel.onEvent(GastoUiEvent.Edit(gastoId))
        } else {
            viewModel.onEvent(GastoUiEvent.LoadGastos)
        }
    }

    if (gastoId != null) {
        AddGastoScreen(
            state = state,
            currencyCode = currency,
            onEvent = viewModel::onEvent,
            onClose = onClose,
            showDatePicker = showDatePicker,
            onShowDatePicker = { showDatePicker = true },
            onDismissDatePicker = { showDatePicker = false },
            isEditing = true,
            onDelete = {
                viewModel.onEvent(GastoUiEvent.Delete(gastoId))
                onClose()
            }
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Mis Gastos", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colors.background)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary,
                    shape = RoundedCornerShape(16.dp),
                    onClick = { onAdd() }
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(28.dp))
                }
            },
            containerColor = colors.background
        ) { padding ->
            AppPullRefresh(
                modifier = Modifier.padding(padding),
                isRefreshing = state.isLoading,
                onRefresh = {
                    viewModel.onEvent(GastoUiEvent.LoadGastos)
                }
            ) {
                Column(Modifier.fillMaxSize()) {
                    FiltrosGastos(
                        selectedCategoria = state.categoriaSeleccionadaNombre ?: "Todas",
                        selectedFecha = state.fecha,
                        selectedMonto = state.montoOrden,
                        categorias = state.categorias.filter { it.tipoId == 2 },
                        fechaOptions = listOf("Hoy", "Ayer", "Esta semana"),
                        montoOptions = listOf("Menor a Mayor", "Mayor a Menor"),
                        onCategoriaSelected = { viewModel.onEvent(GastoUiEvent.CategoriaSelected(it)) },
                        onFechaSelected = { viewModel.onEvent(GastoUiEvent.FechaChanged(it)) },
                        onMontoSelected = { viewModel.onEvent(GastoUiEvent.MontoOrdenChanged(it)) }
                    )

                    if (state.isLoading && state.gastosList.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = colors.primary)
                        }
                    } else {
                        LazyColumn(
                            Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(items = state.gastosList) { gasto ->
                                GastoItemRow(
                                    gasto = gasto,
                                    categorias = state.categorias.filter { it.tipoId == 2 },
                                    currencyCode = currency,
                                    colors = colors,
                                    onClick = {
                                        onEdit(gasto.gastoId)
                                    }
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = colors.outlineVariant.copy(alpha = 0.5f),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosListContent(
    state: GastoUiState,
    currencyCode: String,
    onEvent: (GastoUiEvent) -> Unit,
    onAdd: () -> Unit,
    onEdit: (String) -> Unit,
    onClose: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Gastos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colors.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                shape = RoundedCornerShape(16.dp),
                onClick = { onAdd() }
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(28.dp))
            }
        },
        containerColor = colors.background
    ) { padding ->
        AppPullRefresh(
            modifier = Modifier.padding(padding),
            isRefreshing = state.isLoading,
            onRefresh = {
                onEvent(GastoUiEvent.LoadGastos)
            }
        ) {
            Column(Modifier.fillMaxSize()) {
                FiltrosGastos(
                    selectedCategoria = state.categoriaSeleccionadaNombre ?: "Todas",
                    selectedFecha = state.fecha,
                    selectedMonto = state.montoOrden,
                    categorias = state.categorias.filter { it.tipoId == 2 },
                    fechaOptions = listOf("Hoy", "Ayer", "Esta semana"),
                    montoOptions = listOf("Menor a Mayor", "Mayor a Menor"),
                    onCategoriaSelected = { onEvent(GastoUiEvent.CategoriaSelected(it)) },
                    onFechaSelected = { onEvent(GastoUiEvent.FechaChanged(it)) },
                    onMontoSelected = { onEvent(GastoUiEvent.MontoOrdenChanged(it)) }
                )

                if (state.isLoading && state.gastosList.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colors.primary)
                    }
                } else {
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(items = state.gastosList) { gasto ->
                            GastoItemRow(
                                gasto = gasto,
                                categorias = state.categorias.filter { it.tipoId == 2 },
                                currencyCode = currencyCode,
                                colors = colors,
                                onClick = {
                                    onEdit(gasto.gastoId)
                                }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = colors.outlineVariant.copy(alpha = 0.5f),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddGastoScreenPreview() {
    val fakeState = GastoUiState(
        monto = "350",
        descripcion = "Supermercado Nacional",
        fecha = ""
    )
}
