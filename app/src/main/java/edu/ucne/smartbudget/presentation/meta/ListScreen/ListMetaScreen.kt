package edu.ucne.smartbudget.presentation.meta.ListScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.smartbudget.domain.model.Metas
import edu.ucne.smartbudget.presentation.metas.list.ListMetaViewModel
import edu.ucne.smartbudget.ui.components.formatCurrency
import edu.ucne.smartbudget.ui.theme.SmartBudgetTheme

@Composable
fun ListMetaScreen(
    viewModel: ListMetaViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onAddMeta: () -> Unit,
    onOpenMeta: (String) -> Unit,
    onEditMeta: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    ListMetaContent(
        uiState = uiState,
        currencyCode = currency,
        onNavigateBack = onNavigateBack,
        onAddMeta = onAddMeta,
        onOpenMeta = onOpenMeta,
        onDeleteMeta = { id -> viewModel.deleteMeta(id) },
        onEditMeta = onEditMeta,
        onRefresh = { viewModel.refresh() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListMetaContent(
    uiState: ListMetaUiState,
    currencyCode: String,
    onNavigateBack: () -> Unit,
    onAddMeta: () -> Unit,
    onOpenMeta: (String) -> Unit,
    onDeleteMeta: (String) -> Unit,
    onEditMeta: (String) -> Unit,
    onRefresh: () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Metas Guardadas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = contentColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cerrar",
                            tint = contentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMeta,
                containerColor = primaryColor,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Meta", modifier = Modifier.size(28.dp))
            }
        }
    ) { padding ->

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading && uiState.metas.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.metas.isEmpty()) {
                EmptyStateView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.metas, key = { it.metaId }) { meta ->
                        MetaCard(
                            meta = meta,
                            currencyCode = currencyCode,
                            onClick = { onOpenMeta(meta.metaId) },
                            onDeleteMeta = onDeleteMeta,
                            onEditMeta = onEditMeta
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetaCard(
    meta: Metas,
    currencyCode: String,
    onClick: () -> Unit,
    onDeleteMeta: (String) -> Unit,
    onEditMeta: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    val porcentajeReal = if (meta.monto > 0) (meta.contribucionMensual / meta.monto * 100) else 0.0
    val progresoNormalizado = (porcentajeReal / 100).coerceIn(0.0, 1.0).toFloat()
    val isCompleted = porcentajeReal >= 100

    val themeColor = if (isCompleted) colors.tertiary else colors.primary
    val themeBg = if (isCompleted) colors.tertiaryContainer else colors.primaryContainer
    val themeText = if (isCompleted) colors.onTertiaryContainer else colors.onPrimaryContainer

    val cardContainerColor = colors.background
    val mainTextColor = colors.onSurface
    val subTextColor = colors.onSurfaceVariant

    val savedFormatted = formatCurrency(meta.contribucionMensual, currencyCode)
    val targetFormatted = formatCurrency(meta.monto, currencyCode)
    val metaIcon = getMetaIcon(meta.emoji)

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = themeBg, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = metaIcon,
                        contentDescription = null,
                        tint = themeText,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = meta.nombre,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = mainTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    if (isCompleted) {
                        Text(
                            text = "¡Completada!",
                            style = MaterialTheme.typography.bodySmall,
                            color = themeColor
                        )
                    } else if (meta.fecha.isNotEmpty()) {
                        Text(
                            text = "Meta: ${meta.fecha}",
                            style = MaterialTheme.typography.bodySmall,
                            color = subTextColor
                        )
                    }
                }

                Box {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = subTextColor
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(colors.surfaceContainer)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = { expanded = false; onEditMeta(meta.metaId) },
                            leadingIcon = { Icon(Icons.Outlined.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = colors.error) },
                            onClick = { expanded = false; onDeleteMeta(meta.metaId) },
                            leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = colors.error) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = savedFormatted,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = mainTextColor.copy(alpha = 0.9f)
                        )
                    )
                    Text(
                        text = " / $targetFormatted",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = subTextColor
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Text(
                    text = "${porcentajeReal.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progresoNormalizado },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = themeColor,
                trackColor = colors.surfaceVariant,
                strokeCap = StrokeCap.Round,
            )
        }
    }
}

@Composable
fun EmptyStateView() {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.Savings,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = colors.outlineVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No tienes metas aún",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurfaceVariant
            )
            Text(
                text = "¡Toca el + para crear una!",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

fun getMetaIcon(iconName: String?): ImageVector {
    return when (iconName?.lowercase()) {
        "car", "auto", "coche" -> Icons.Outlined.DirectionsCar
        "casa", "house", "hogar" -> Icons.Outlined.Home
        "viaje", "travel", "avión" -> Icons.Outlined.Flight
        "estudios", "educación" -> Icons.Outlined.School
        "tecnología", "pc" -> Icons.Outlined.Computer
        "moto" -> Icons.Outlined.TwoWheeler
        else -> Icons.Outlined.Savings
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewListMetaScreen() {
    val fakeMetas = listOf(
        Metas(metaId = "1", nombre = "New Car Fund", monto = 5000.0, contribucionMensual = 1500.0, fecha = "May 2025", emoji = "car", usuarioId = "", imagenes = emptyList()),
        Metas(metaId = "2", nombre = "House Down Payment", monto = 50000.0, contribucionMensual = 22100.0, fecha = "Dec 2026", emoji = "house", usuarioId = "", imagenes = emptyList()),
        Metas(metaId = "3", nombre = "Japan Trip", monto = 3000.0, contribucionMensual = 3000.0, fecha = "", emoji = "travel", usuarioId = "", imagenes = emptyList())
    )

    SmartBudgetTheme {
        ListMetaContent(
            uiState = ListMetaUiState(metas = fakeMetas, isRefreshing = false, isLoading = false),
            currencyCode = "USD",
            onNavigateBack = {},
            onAddMeta = {},
            onOpenMeta = {},
            onDeleteMeta = {},
            onEditMeta = {},
            onRefresh = {}
        )
    }
}
