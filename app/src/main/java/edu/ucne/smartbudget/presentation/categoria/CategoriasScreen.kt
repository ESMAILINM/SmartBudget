package edu.ucne.smartbudget.presentation.categoria

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.smartbudget.domain.model.Categorias
import edu.ucne.smartbudget.presentation.categoria.components.AddEditCategoriaDialog
import edu.ucne.smartbudget.presentation.categoria.components.CategoriaItemRow
import edu.ucne.smartbudget.presentation.categoria.components.CategoryTabs
import edu.ucne.smartbudget.ui.components.AppPullRefresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    state: CategoriasUiState,
    onEvent: (CategoriaUiEvent) -> Unit,
    onCloseScreen: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val colorScheme = MaterialTheme.colorScheme

    var isSearching by remember { mutableStateOf(false) }
    var categoriaToEdit by remember { mutableStateOf<Categorias?>(null) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onEvent(CategoriaUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Manejar Categorías", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = onCloseScreen) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isSearching = !isSearching
                        if (!isSearching) {
                            searchText = ""
                            onEvent(CategoriaUiEvent.OnSearchQueryChange(""))
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.background,
                    titleContentColor = colorScheme.onBackground,
                    actionIconContentColor = colorScheme.onBackground,
                    navigationIconContentColor = colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    categoriaToEdit = null
                    onEvent(CategoriaUiEvent.ShowDialog)
                },
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar categoría")
            }
        }
    ) { padding ->

        AppPullRefresh(
            modifier = Modifier.padding(padding),
            isRefreshing = state.isLoading,
            onRefresh = { }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.background)
            ) {
                val selectedTabIndex = if (state.currentFilterTipoId == 1) 1 else 0

                CategoryTabs(
                    selectedIndex = selectedTabIndex,
                    onTabSelected = { index ->
                        val newTipoId = if (index == 0) 2 else 1
                        onEvent(CategoriaUiEvent.OnTipoFilterChange(newTipoId))
                    }
                )

                HorizontalDivider(color = colorScheme.outlineVariant, thickness = 1.dp)

                if (isSearching) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            onEvent(CategoriaUiEvent.OnSearchQueryChange(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        label = { Text("Buscar categoría") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.outline
                        )
                    )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.isLoading && state.filteredCategorias.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(
                                items = state.filteredCategorias,
                                key = { it.categoriaId }
                            ) { categoria ->
                                CategoriaItemRow(
                                    categoria = categoria,
                                    onEdit = {
                                        categoriaToEdit = categoria
                                        onEvent(CategoriaUiEvent.ShowDialog)
                                    },
                                    onDelete = {
                                        if (categoria.categoriaId.isNotBlank()) {
                                            onEvent(CategoriaUiEvent.DeleteCategoria(categoria))
                                        }
                                    }
                                )
                                HorizontalDivider(
                                    color = colorScheme.surfaceVariant,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(start = 72.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (state.showDialog) {
            AddEditCategoriaDialog(
                categoria = categoriaToEdit,
                currentTipoId = state.currentFilterTipoId,
                onDismiss = { onEvent(CategoriaUiEvent.HideDialog) },
                onSave = { categoria ->
                    if (categoria.categoriaId.isBlank()) {
                        onEvent(
                            CategoriaUiEvent.AddCategoria(
                                nombre = categoria.nombre,
                                tipoId = if (categoria.tipoId == 0) state.currentFilterTipoId else categoria.tipoId
                            )
                        )
                    } else {
                        onEvent(CategoriaUiEvent.EditCategoria(categoria))
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoriasScreen() {
    val dummyCategorias = listOf(
        Categorias(
            categoriaId = "1",
            nombre = "Comida",
            tipoId = 2,
            remoteId = 0
        ),
        Categorias(
            categoriaId = "2",
            nombre = "Salario",
            tipoId = 1,
            remoteId = 0
        ),
        Categorias(
            categoriaId = "3",
            nombre = "Transporte",
            tipoId = 2,
            remoteId = 0
        )
    )

    val dummyState = CategoriasUiState(
        isLoading = false,
        categorias = dummyCategorias,
        filteredCategorias = dummyCategorias,
        currentFilterTipoId = 2,
        showDialog = false,
        userMessage = null
    )

    MaterialTheme {
        CategoriasScreen(
            state = dummyState,
            onEvent = {},
            onCloseScreen = {}
        )
    }
}
