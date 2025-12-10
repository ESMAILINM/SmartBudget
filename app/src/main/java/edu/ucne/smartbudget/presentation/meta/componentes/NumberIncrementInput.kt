package edu.ucne.smartbudget.presentation.meta.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.smartbudget.presentation.ingreso.items.defaultTextFieldColors


@Composable
fun NumberIncrementInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val currentValue = value.toDoubleOrNull() ?: 0.0
    var baseIncrement by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(currentValue) {
        if (currentValue != 0.0) {
            if (baseIncrement == 0.0 || (currentValue % baseIncrement != 0.0)) {
                baseIncrement = currentValue
            }
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledIconButton(
            onClick = {
                val step = if (baseIncrement == 0.0) currentValue else baseIncrement
                val newValue = (currentValue - step).coerceAtLeast(0.0)
                val formattedValue = if (newValue % 1.0 == 0.0) {
                    newValue.toInt().toString()
                } else {
                    String.format("%.2f", newValue)
                }
                onValueChange(formattedValue)
            },
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colors.surfaceVariant,
                contentColor = colors.onSurfaceVariant
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Disminuir")
        }

        OutlinedTextField(
            value = value,
            onValueChange = { newValueStr ->
                onValueChange(newValueStr)
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = defaultTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            placeholder = {
                Text(
                    "0.00",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        )

        FilledIconButton(
            onClick = {
                val step = if (baseIncrement == 0.0) currentValue else baseIncrement
                val newValue = currentValue + step
                val formattedValue = if (newValue % 1.0 == 0.0) {
                    newValue.toInt().toString()
                } else {
                    String.format("%.2f", newValue)
                }
                onValueChange(formattedValue)
            },
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Aumentar")
        }
    }
}