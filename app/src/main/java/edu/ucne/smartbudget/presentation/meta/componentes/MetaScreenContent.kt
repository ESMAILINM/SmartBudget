package edu.ucne.smartbudget.presentation.meta.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import edu.ucne.smartbudget.domain.model.Metas
import edu.ucne.smartbudget.presentation.ingreso.items.defaultTextFieldColors
import edu.ucne.smartbudget.presentation.meta.MetaScreen.MetaUiEvent
import edu.ucne.smartbudget.presentation.meta.MetaScreen.MetaUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MetaScreenContent(
    state: MetaUiState,
    metas: List<Metas>,
    onEvent: (MetaUiEvent) -> Unit,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            scope.launch { snackbarHostState.showSnackbar("¡Meta guardada correctamente!") }
            delay(700)
            onClose()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(message = error, withDismissAction = true)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (state.metaId != null) "Editar Meta" else "Nueva Meta",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.background
                )
            )
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {

            if (state.isLoading || state.isSaving) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .background(Color.Black.copy(alpha = 0.2f))
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onEvent(MetaUiEvent.Refresh) },
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = state.emoji,
                            onValueChange = { onEvent(MetaUiEvent.EmojiChanged(it)) },
                            label = { Text("Icono") },
                            placeholder = { Text("🎯") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true,
                            colors = defaultTextFieldColors(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = state.nombre,
                            onValueChange = { onEvent(MetaUiEvent.NombreChanged(it)) },
                            label = { Text("Nombre de la meta") },
                            placeholder = { Text("Ej. Comprar carro") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = defaultTextFieldColors(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = state.monto,
                        onValueChange = { onEvent(MetaUiEvent.MontoChanged(it)) },
                        label = { Text("Monto Objetivo") },
                        placeholder = { Text("$ 0.00") },
                        leadingIcon = { Text("$", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = defaultTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text(
                        text = "Contribución Mensual",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.onSurfaceVariant
                    )

                    NumberIncrementInput(
                        value = state.contribucionMensual,
                        onValueChange = { onEvent(MetaUiEvent.ContribucionChanged(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState()
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val date = java.time.Instant.ofEpochMilli(millis)
                                            .atZone(java.time.ZoneId.systemDefault())
                                            .toLocalDate()
                                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        onEvent(MetaUiEvent.FechaChanged(date))
                                    }
                                    showDatePicker = false
                                }) { Text("Aceptar") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    Box(modifier = Modifier.clickable { showDatePicker = true }) {
                        OutlinedTextField(
                            value = state.fecha,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fecha límite") },
                            placeholder = { Text("Seleccionar fecha") },
                            trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = colors.onSurface,
                                disabledBorderColor = colors.outline,
                                disabledLabelColor = colors.onSurfaceVariant,
                                disabledTrailingIconColor = colors.onSurfaceVariant,
                                disabledContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Text(
                        text = "Portada de la meta",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.onSurfaceVariant
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colors.background),
                        border = null,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        ImageSelectionGridWithDefaults(
                            selectedImageIndex = state.selectedImageIndex,
                            onImageSelected = { index, uri ->
                                onEvent(MetaUiEvent.ImageSelected(index, uri))
                            },
                            images = state.imagenes,
                            onImagesChanged = { lista -> onEvent(MetaUiEvent.ImagenesChanged(lista)) }
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { onEvent(MetaUiEvent.Save) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        Text(
                            if (state.metaId != null) "Guardar Cambios" else "Crear Meta",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}
