package edu.ucne.smartbudget.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.ucne.smartbudget.presentation.categoria.CategoriaViewModel
import edu.ucne.smartbudget.presentation.categoria.CategoriasScreen
import edu.ucne.smartbudget.presentation.configuracion.ConfiguracionScreen
import edu.ucne.smartbudget.presentation.configuracion.ConfiguracionViewModel
import edu.ucne.smartbudget.presentation.dashboardScreen.Screen.HomeScreen
import edu.ucne.smartbudget.presentation.dashboardScreen.Screen.HomeViewModel
import edu.ucne.smartbudget.presentation.gasto.AddGastoScreen
import edu.ucne.smartbudget.presentation.gasto.GastoUiEvent
import edu.ucne.smartbudget.presentation.gasto.GastoViewModel
import edu.ucne.smartbudget.presentation.gasto.GastosScreen
import edu.ucne.smartbudget.presentation.ingreso.IngresosScreen
import edu.ucne.smartbudget.presentation.meta.ListScreen.ListMetaScreen
import edu.ucne.smartbudget.presentation.meta.MetaScreen.MetaScreen
import edu.ucne.smartbudget.presentation.perfil.ProfileScreen
import edu.ucne.smartbudget.presentation.reporte.ReporteScreen

@Composable
fun MainAppScreen(
    onLogout: () -> Unit,
    currentUserId: String
) {
    val navController = rememberNavController()

    val bottomNavScreens = listOf(
        Screen.Home,
        Screen.Metas,
        Screen.Reports,
        Screen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val isBottomBarVisible = bottomNavScreens.any { screen ->
        val baseRoute = screen.route.substringBefore("/")
        currentRoute?.startsWith(baseRoute) == true
    }

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                BottomNavBar(
                    navController = navController,
                    items = bottomNavScreens,
                    currentUserId = currentUserId
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.createRoute(currentUserId),
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(
                route = "home/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onAddExpenseClicked = { navController.navigate(Screen.Gastos.createRoute(currentUserId)) },
                    onAddIncomeClicked = { navController.navigate(Screen.Ingresos.createRoute(currentUserId)) },
                    onViewReportsClicked = { navController.navigate(Screen.Reports.createRoute(currentUserId)) },
                    onViewGoalsClicked = { navController.navigate(Screen.Metas.createRoute(currentUserId)) },
                    onNavigateToConfig = { navController.navigate(Screen.Configuracion.createRoute(currentUserId)) },
                    onNavigateToAllTransactions = { navController.navigate(Screen.RecentTransactions.createRoute(currentUserId)) }
                )
            }

            composable(
                route = "metas/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) {
                ListMetaScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onAddMeta = {
                        navController.navigate(Screen.MetaEdit.createRoute(currentUserId, "0"))
                    },
                    onOpenMeta = { metaId ->
                        navController.navigate(Screen.MetaEdit.createRoute(currentUserId, metaId))
                    },
                    onEditMeta = { metaId ->
                        navController.navigate(Screen.MetaEdit.createRoute(currentUserId, metaId))
                    }
                )
            }

            composable(
                route = "reports/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) {
                ReporteScreen(onClose = { navController.popBackStack() })
            }

            composable(
                route = "profile/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) {
                ProfileScreen(
                    nNavigateBack = { navController.popBackStack() }
                    , onNavigateToLogin = onLogout
                )
            }

            composable(
                route = "categorias/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) {
                val vm: CategoriaViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()
                CategoriasScreen(
                    state = state,
                    onEvent = vm::onEvent,
                    onCloseScreen = { navController.popBackStack() }
                )
            }

            composable(
                route = "gastos/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) {
                GastosScreen(
                    onAdd = {
                        navController.navigate(Screen.GastosAdd.createRoute(currentUserId, null))
                    },
                    onEdit = { id ->
                        navController.navigate(Screen.GastosAdd.createRoute(currentUserId, id))
                    },
                    onClose = { navController.popBackStack() }
                )
            }

            composable(
                route = "ingresos/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) {
                IngresosScreen(onClose = { navController.popBackStack() })
            }

            composable(
                route = "configuracion/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) {
                val vm: ConfiguracionViewModel = hiltViewModel()
                ConfiguracionScreen(
                    viewModel = vm,
                    onClose = { navController.popBackStack() },
                    onManageAccount = { navController.navigate(Screen.Profile.createRoute(currentUserId)) },
                    onNavigateToCategories = { navController.navigate(Screen.Categorias.createRoute(currentUserId)) },
                    onLogout = onLogout
                )
            }

            composable(
                route = Screen.MetaEdit.route,
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("metaId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val metaId = backStackEntry.arguments?.getString("metaId")
                MetaScreen(
                    metaId = metaId,
                    onClose = { navController.popBackStack() }
                )
            }
            composable(
                route = "recent_transactions/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text("Historial Completo de Transacciones")
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Volver")
                    }
                }
            }

            composable(
                route = Screen.GastosAdd.route,
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("gastoId") { nullable = true; defaultValue = null }
                )
            ) { backStackEntry ->
                val rawId = backStackEntry.arguments?.getString("gastoId")
                val gastoId = if (rawId == "null" || rawId.isNullOrBlank()) null else rawId

                val viewModel: GastoViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
                var showDatePicker by remember { mutableStateOf(false) }

                LaunchedEffect(gastoId) {
                    if (gastoId != null) {
                        viewModel.onEvent(GastoUiEvent.Edit(gastoId))
                    }
                }

                AddGastoScreen(
                    state = state,
                    currencyCode = currency,
                    onEvent = viewModel::onEvent,
                    onClose = { navController.popBackStack() },
                    showDatePicker = showDatePicker,
                    onShowDatePicker = { showDatePicker = true },
                    onDismissDatePicker = { showDatePicker = false },
                    isEditing = gastoId != null,
                    onDelete = {
                        if (gastoId != null) {
                            viewModel.onEvent(GastoUiEvent.Delete(gastoId))
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(
    navController: NavHostController,
    items: List<Screen>,
    currentUserId: String
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        NavigationBar(
            modifier = Modifier.height(80.dp),
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp
        ) {
            items.forEach { screen ->
                val baseRoute = screen.route.substringBefore("/")
                val currentBase = currentRoute?.substringBefore("/")
                val isSelected = baseRoute == currentBase

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        val routeToNavigate = when (screen) {
                            Screen.Home -> Screen.Home.createRoute(currentUserId)
                            Screen.Metas -> Screen.Metas.createRoute(currentUserId)
                            Screen.Reports -> Screen.Reports.createRoute(currentUserId)
                            Screen.Profile -> Screen.Profile.createRoute(currentUserId)
                            else -> screen.route
                        }

                        navController.navigate(routeToNavigate) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        screen.icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = screen.label,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        screen.label?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    }
}
