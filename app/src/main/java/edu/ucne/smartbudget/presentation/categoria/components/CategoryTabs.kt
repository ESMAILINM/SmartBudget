package edu.ucne.smartbudget.presentation.categoria.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun CategoryTabs(selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = colorScheme.background,
        contentColor = colorScheme.primary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.Companion.tabIndicatorOffset(tabPositions[selectedIndex]),
                color = colorScheme.primary,
                height = 3.dp
            )
        },
        divider = {}
    ) {
        Tab(
            selected = selectedIndex == 0,
            onClick = { onTabSelected(0) },
            text = { Text("Gastos", fontWeight = if (selectedIndex == 0) FontWeight.Bold else FontWeight.Normal) }
        )
        Tab(
            selected = selectedIndex == 1,
            onClick = { onTabSelected(1) },
            text = { Text("Ingresos", fontWeight = if (selectedIndex == 1) FontWeight.Bold else FontWeight.Normal) }
        )
    }
}