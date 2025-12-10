package edu.ucne.smartbudget.presentation.configuracion.items

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import edu.ucne.smartbudget.presentation.configuracion.ConfiguracionUiEvent
import edu.ucne.smartbudget.presentation.configuracion.ConfiguracionUiState
import edu.ucne.smartbudget.presentation.configuracion.SettingsSection


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionContent(
    state: ConfiguracionUiState,
    onEvent: (ConfiguracionUiEvent) -> Unit
) {
    val scrollState = rememberScrollState()
    val colors = MaterialTheme.colorScheme
    var showCurrencyDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onEvent(ConfiguracionUiEvent.OnTogglePushNotifications(true))
            }
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Configuración",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.onSurface.copy(alpha = 0.8f)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(ConfiguracionUiEvent.OnClose) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = colors.onSurface,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.background,
                    scrolledContainerColor = colors.background
                )
            )
        },
        containerColor = colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            SettingsSection(title = "APARIENCIA") {
                SettingsCard {
                    SettingsSwitchRow(
                        icon = Icons.Outlined.DarkMode,
                        label = "Modo Oscuro",
                        checked = state.isDarkMode,
                        onCheckedChange = { isChecked ->
                            onEvent(ConfiguracionUiEvent.OnToggleDarkMode(isChecked))
                        }
                    )
                }
            }

            val context = LocalContext.current

            SettingsSection(title = "NOTIFICACIONES") {
                SettingsCard {
                    SettingsSwitchRow(
                        icon = Icons.Outlined.Notifications,
                        label = "Notificaciones Push",
                        checked = state.pushNotificationsEnabled,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasPermission =
                                        NotificationManagerCompat.from(context).areNotificationsEnabled()

                                    if (!hasPermission) {
                                        notificationPermissionLauncher.launch(
                                            Manifest.permission.POST_NOTIFICATIONS
                                        )
                                    } else {
                                        onEvent(
                                            ConfiguracionUiEvent.OnTogglePushNotifications(true)
                                        )
                                    }
                                } else {
                                    onEvent(
                                        ConfiguracionUiEvent.OnTogglePushNotifications(true)
                                    )
                                }
                            } else {
                                onEvent(
                                    ConfiguracionUiEvent.OnTogglePushNotifications(false)
                                )
                            }
                        }
                    )
                }
            }

            SettingsSection(title = "GENERAL") {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colors.background,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingsNavigationRow(
                            icon = Icons.Outlined.AttachMoney,
                            label = "Moneda",
                            value = state.selectedCurrency,
                            onClick = { showCurrencyDialog = true }
                        )
                        HorizontalDivider(
                            color = colors.outlineVariant,
                            thickness = 1.dp
                        )
                        SettingsNavigationRow(
                            icon = Icons.Outlined.PersonOutline,
                            label = "Administrar Cuenta",
                            onClick = { onEvent(ConfiguracionUiEvent.OnManageAccount) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                SettingsCard {
                    SettingsNavigationRow(
                        icon = Icons.Outlined.GridView,
                        label = "Manejar Categorias",
                        onClick = { onEvent(ConfiguracionUiEvent.OnManageCategories) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onEvent(ConfiguracionUiEvent.OnLogout) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.error,
                    contentColor = colors.onError
                )
            ) {
                Text(
                    text = "Cerrar Sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showCurrencyDialog) {
        val currencies = listOf("USD", "DOP", "EUR", "MXN")
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Seleccionar Moneda") },
            text = {
                Column {
                    currencies.forEach { currency ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (currency == state.selectedCurrency),
                                    onClick = {
                                        onEvent(ConfiguracionUiEvent.OnCurrencyChanged(currency))
                                    }
                                )
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (currency == state.selectedCurrency),
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colors.primary,
                                    unselectedColor = colors.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currency,
                                color = colors.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Cancelar", color = colors.primary)
                }
            },
            containerColor = colors.surfaceContainer,
            titleContentColor = colors.onSurface,
            textContentColor = colors.onSurfaceVariant
        )
    }
}