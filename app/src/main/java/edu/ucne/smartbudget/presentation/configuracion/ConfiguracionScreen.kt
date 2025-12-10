package edu.ucne.smartbudget.presentation.configuracion


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.smartbudget.presentation.configuracion.items.ConfiguracionContent

@Composable
fun ConfiguracionScreen(
    viewModel: ConfiguracionViewModel = hiltViewModel(),
    onClose: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onManageAccount: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ConfiguracionContent(
        state = state,
        onEvent = { event ->
            when (event) {
                is ConfiguracionUiEvent.OnClose -> onClose()
                is ConfiguracionUiEvent.OnManageCategories -> onNavigateToCategories()
                is ConfiguracionUiEvent.OnManageAccount -> onManageAccount()
                is ConfiguracionUiEvent.OnLogout -> {
                    viewModel.onEvent(ConfiguracionUiEvent.OnLogout)
                    onLogout()
                }
                else -> viewModel.onEvent(event)
            }
        }
    )
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 11.sp
            ),
            color = colors.outline,
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
        )
        content()
    }
}


@Preview(showBackground = true)
@Composable
fun ConfiguracionScreenPreview() {
    MaterialTheme {
        ConfiguracionContent(
            state = ConfiguracionUiState(
                isDarkMode = false,
                pushNotificationsEnabled = true,
                selectedCurrency = "DOP"
            ),
            onEvent = {}
        )
    }
}
