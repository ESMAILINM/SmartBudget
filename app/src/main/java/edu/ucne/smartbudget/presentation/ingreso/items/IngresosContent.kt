package edu.ucne.smartbudget.presentation.ingreso.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.ucne.smartbudget.domain.model.Ingresos
import edu.ucne.smartbudget.presentation.ingreso.IngresoUiEvent
import edu.ucne.smartbudget.presentation.ingreso.IngresoUiState
import edu.ucne.smartbudget.ui.components.AppPullRefresh


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresosContent(
    state: IngresoUiState,
    filteredIngresos: List<Ingresos>,
    currencyCode: String,
    monthOptions: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    onEvent: (IngresoUiEvent) -> Unit,
    onClose: () -> Unit,
    onEditClick: (String) -> Unit,
    onAddClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ingresos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.background,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                shape = RoundedCornerShape(50),
                onClick = onAddClick
            ) { Icon(Icons.Default.Add, null) }
        },
    ) { padding ->
        AppPullRefresh(
            modifier = Modifier.padding(padding),
            isRefreshing = state.isLoading,
            onRefresh = {
                onEvent(IngresoUiEvent.Refresh)
            }
        ) {
            Column(Modifier.fillMaxSize()) {
                FiltrosIngresos(
                    selectedFilter = selectedFilter,
                    onFilterSelected = onFilterSelected,
                    monthOptions = monthOptions
                )

                Spacer(Modifier.height(8.dp))

                if (state.isLoading && filteredIngresos.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LinearProgressIndicator(color = colors.primary)
                    }
                } else {
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredIngresos) { ingreso ->
                            IngresoItemRow(
                                ingreso = ingreso,
                                onClick = { onEditClick(ingreso.ingresoId) },
                                colors = colors,
                                categorias = state.categorias,
                                currencyCode = currencyCode
                            )
                            Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}