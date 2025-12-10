package edu.ucne.smartbudget.presentation.categoria.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.ucne.smartbudget.domain.model.Categorias


@Composable
fun AddEditCategoriaDialog(
    categoria: Categorias?,
    currentTipoId: Int,
    onDismiss: () -> Unit,
    onSave: (Categorias) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var nombre by remember { mutableStateOf(categoria?.nombre ?: "") }
    var selectedTipoId by remember { mutableIntStateOf(categoria?.tipoId ?: currentTipoId) }
    var errorNombre by remember { mutableStateOf(false) }

    AlertDialog(
        containerColor = colorScheme.surfaceContainerHigh,
        titleContentColor = colorScheme.onSurface,
        textContentColor = colorScheme.onSurfaceVariant,
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        errorNombre = true
                    } else {
                        onSave(
                            Categorias(
                                categoriaId = categoria?.categoriaId ?: "",
                                nombre = nombre,
                                tipoId = selectedTipoId,
                                remoteId = categoria?.remoteId ?: 0
                            )
                        )
                        onDismiss()
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text(if (categoria == null) "Nueva Categoría" else "Editar Categoría") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        errorNombre = false
                    },
                    label = { Text("Nombre") },
                    isError = errorNombre,
                    supportingText = { if (errorNombre) Text("Requerido") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Tipo de transacción:", style = MaterialTheme.typography.labelLarge)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedTipoId == 2,
                        onClick = { selectedTipoId = 2 },
                        label = { Text("Gasto") },
                        leadingIcon = if (selectedTipoId == 2) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                    FilterChip(
                        selected = selectedTipoId == 1,
                        onClick = { selectedTipoId = 1 },
                        label = { Text("Ingreso") },
                        leadingIcon = if (selectedTipoId == 1) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                }
            }
        }
    )
}
