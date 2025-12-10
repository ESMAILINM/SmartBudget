package edu.ucne.smartbudget.presentation.ingreso.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun FiltrosIngresos(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    monthOptions: List<String>
) {
    val colors = MaterialTheme.colorScheme
    val basicFilters = listOf("Todo", "Este mes")
    var expanded by remember { mutableStateOf(false) }

    Row(
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        basicFilters.forEach { label ->
            val isSelected = selectedFilter == label
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(label) },
                label = { Text(label) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colors.primary,
                    selectedLabelColor = colors.onPrimary,
                    containerColor = colors.surfaceVariant.copy(alpha = 0.3f),
                    labelColor = colors.onSurfaceVariant
                ),
                border = if (!isSelected)
                    FilterChipDefaults.filterChipBorder(enabled = true, selected = false) else null
            )
        }

        Box {
            val isCustomMonth = selectedFilter in monthOptions
            FilterChip(
                selected = isCustomMonth,
                onClick = { expanded = !expanded },
                label = { Text(selectedFilter.takeIf { it in monthOptions } ?: "Mes pasado") },
                trailingIcon = {
                    Icon(Icons.Default.KeyboardArrowDown, null, modifier = Modifier.size(16.dp))
                },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colors.primary,
                    selectedLabelColor = colors.onPrimary,
                    containerColor = colors.surfaceVariant.copy(alpha = 0.3f),
                    labelColor = colors.onSurfaceVariant
                ),
                border = if (!isCustomMonth)
                    FilterChipDefaults.filterChipBorder(
                        enabled = true, selected = false,
                        borderColor = colors.outline
                    ) else null
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                monthOptions.forEach { monthLabel ->
                    DropdownMenuItem(
                        text = { Text(monthLabel) },
                        onClick = {
                            onFilterSelected(monthLabel)
                            expanded = false
                        }
                    )
                }
                DropdownMenuItem(text = { Text("Todo") }, onClick = {
                    onFilterSelected("Todo")
                    expanded = false
                })
            }
        }
    }
}