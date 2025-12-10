package edu.ucne.smartbudget.presentation.gasto.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun CustomFilterChip(
    label: String,
    isSelected: Boolean,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    colors: ColorScheme
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        FilterChip(
            selected = isSelected,
            onClick = { expanded = !expanded },
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (isSelected) colors.onPrimary else colors.onSurfaceVariant
                )
            },
            shape = RoundedCornerShape(50),
            border =
                if (isSelected) null
                else FilterChipDefaults.filterChipBorder(
                    borderColor = colors.outline,
                    borderWidth = 1.dp,
                    enabled = true,
                    selected = false
                ),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = colors.primary,
                selectedLabelColor = colors.onPrimary,
                containerColor = colors.surfaceVariant.copy(alpha = 0.3f),
                labelColor = colors.onSurfaceVariant
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colors.surface)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = colors.onSurface) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}