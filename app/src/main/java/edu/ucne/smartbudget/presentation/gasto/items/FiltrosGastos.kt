package edu.ucne.smartbudget.presentation.gasto.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.ucne.smartbudget.domain.model.Categorias


@Composable
fun FiltrosGastos(
    selectedCategoria: String,
    selectedFecha: String,
    selectedMonto: String,
    categorias: List<Categorias>,
    fechaOptions: List<String>,
    montoOptions: List<String>,
    onCategoriaSelected: (String) -> Unit,
    onFechaSelected: (String) -> Unit,
    onMontoSelected: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Row(
        Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomFilterChip(
            label = selectedCategoria.takeIf { it.isNotEmpty() && it != "Todas" } ?: "Categoría",
            isSelected = selectedCategoria != "Todas" && selectedCategoria.isNotEmpty(),
            options = categorias.map { it.nombre } + "Todas",
            onOptionSelected = onCategoriaSelected,
            colors = colors
        )

        CustomFilterChip(
            label = selectedFecha.takeIf { it.isNotEmpty() && it != "Todo" } ?: "Fecha",
            isSelected = selectedFecha != "Todo" && selectedFecha.isNotEmpty(),
            options = fechaOptions + "Todo",
            onOptionSelected = onFechaSelected,
            colors = colors
        )

        CustomFilterChip(
            label = selectedMonto.takeIf { it.isNotEmpty() && it != "Todo" } ?: "Monto",
            isSelected = selectedMonto != "Todo" && selectedMonto.isNotEmpty(),
            options = montoOptions + "Todo",
            onOptionSelected = onMontoSelected,
            colors = colors
        )
    }
}