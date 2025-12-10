package edu.ucne.smartbudget.presentation.ingreso.items

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import edu.ucne.smartbudget.presentation.ingreso.IngresoUiEvent
import edu.ucne.smartbudget.presentation.ingreso.IngresoUiState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngresoScreen(
    state: IngresoUiState,
    currencyCode: String,
    onEvent: (IngresoUiEvent) -> Unit,
    onClose: () -> Unit,
    showDatePicker: Boolean,
    onShowDatePicker: () -> Unit,
    onDismissDatePicker: () -> Unit,
    isEditing: Boolean,
    onDelete: () -> Unit
) {
    BackHandler(enabled = true) { onClose() }
    val colors = MaterialTheme.colorScheme
    val inputShape = RoundedCornerShape(12.dp)
    val currencySymbol = if (currencyCode == "EUR") "€" else "$"

    Scaffold(
        containerColor = colors.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isEditing) "Editar Ingreso" else "Nuevo Ingreso",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.background
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                value = state.monto,
                onValueChange = { onEvent(IngresoUiEvent.MontoChanged(it)) },
                label = { Text("Monto") },
                placeholder = { Text("0.00") },
                leadingIcon = { Text(currencySymbol, fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                shape = inputShape,
                colors = defaultTextFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = state.descripcion,
                onValueChange = { onEvent(IngresoUiEvent.DescripcionChanged(it)) },
                label = { Text("Descripción") },
                placeholder = { Text("Ej. Proyecto Freelance") },
                modifier = Modifier.fillMaxWidth(),
                shape = inputShape,
                colors = defaultTextFieldColors()
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = state.categoriaSeleccionada?.nombre ?: "Selecciona categoría",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = inputShape,
                    colors = defaultTextFieldColors(),
                )
                val categoriasFiltradas = state.categorias.filter { it.tipoId == 1 }

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(colors.surface)
                ) {
                    if (categoriasFiltradas.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay categorías de ingresos disponibles") },
                            onClick = { expanded = false }
                        )
                    } else {
                        categoriasFiltradas.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre, color = colors.onSurface) },
                                onClick = {
                                    onEvent(IngresoUiEvent.CategoriaIdChanged(categoria.categoriaId))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.clickable { onShowDatePicker() }) {
                OutlinedTextField(
                    value = state.fecha,
                    onValueChange = {},
                    label = { Text("Fecha") },
                    placeholder = { Text("Seleccionar fecha") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = inputShape,
                    colors = defaultTextFieldColors(),
                    enabled = false,
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isEditing) {
                Button(
                    onClick = { onDelete() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.errorContainer,
                        contentColor = colors.onErrorContainer
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar Ingreso", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(0.dp))
            }

            Button(
                onClick = { onEvent(IngresoUiEvent.Save) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text(
                    text = if (isEditing) "Guardar Cambios" else "Guardar Ingreso",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = onDismissDatePicker,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        onEvent(IngresoUiEvent.FechaChanged(date))
                    }
                    onDismissDatePicker()
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = onDismissDatePicker) { Text("Cancelar") }
            }
        ) {
            DatePicker(datePickerState)
        }
    }
}
