package edu.ucne.smartbudget.presentation.dashboardScreen.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.smartbudget.presentation.dashboardScreen.components.*
import edu.ucne.smartbudget.presentation.dashboardScreen.components.items.ExpenseBreakdownSection
import edu.ucne.smartbudget.presentation.dashboardScreen.components.items.IncomeTrendSection
import edu.ucne.smartbudget.presentation.dashboardScreen.components.items.RecentTransactionsSection
import edu.ucne.smartbudget.presentation.dashboardScreen.components.items.SummarySection
import edu.ucne.smartbudget.presentation.dashboardScreen.model.*
import edu.ucne.smartbudget.ui.theme.SmartBudgetTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddExpenseClicked: () -> Unit,
    onAddIncomeClicked: () -> Unit,
    onViewReportsClicked: () -> Unit,
    onViewGoalsClicked: () -> Unit,
    onNavigateToConfig: () -> Unit,
    onNavigateToAllTransactions: () -> Unit
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    HomeScreenContent(
        state = uiState,
        currencyCode = currency,
        onIntent = { event -> viewModel.onIntent(event) },
        onAddExpenseClicked = onAddExpenseClicked,
        onAddIncomeClicked = onAddIncomeClicked,
        onViewReportsClicked = onViewReportsClicked,
        onViewGoalsClicked = onViewGoalsClicked,
        onNavigateToConfig = onNavigateToConfig,
        onNavigateToAllTransactions = onNavigateToAllTransactions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeUiState,
    currencyCode: String,
    onIntent: (HomeUiEvent) -> Unit,
    onAddExpenseClicked: () -> Unit,
    onAddIncomeClicked: () -> Unit,
    onViewReportsClicked: () -> Unit,
    onViewGoalsClicked: () -> Unit,
    onNavigateToConfig: () -> Unit,
    onNavigateToAllTransactions: () -> Unit
    
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Resumen",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 24.sp,
                                color = contentColor.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToConfig) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Configuración",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = contentColor,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { paddingValues ->

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    SummarySection(
                        summary = state.summary,
                        currencyCode = currencyCode,
                        onIncomeClick = onAddIncomeClicked,
                        onExpenseClick = onAddExpenseClicked
                    )
                }

                item {
                    IncomeTrendSection(
                        trend = state.trend,
                        currencyCode = currencyCode
                    )
                }

                item {
                    ExpenseBreakdownSection(
                        data = state.breakdown,
                        currencyCode = currencyCode
                    )
                }

                item {
                    RecentTransactionsSection(
                        items = state.recentTransactions,
                        currencyCode = currencyCode,
                    )
                }

                item {
                    ActionButtons(
                        onAddExpenseClicked = {
                            onIntent(HomeUiEvent.OpenAddTransaction(true))
                            onAddExpenseClicked()
                        },
                        onAddIncomeClicked = {
                            onIntent(HomeUiEvent.OpenAddTransaction(false))
                            onAddIncomeClicked()
                        },
                        onViewReportsClicked = {
                            onIntent(HomeUiEvent.ViewReports)
                            onViewReportsClicked()
                        },
                        onViewGoalsClicked = {
                            onIntent(HomeUiEvent.ViewGoals)
                            onViewGoalsClicked()
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeScreenPreview() {
    val fakeState = HomeUiState(
        isLoading = false,
        summary = SummaryData(
            totalIngresos = 5678.0,
            totalGastos = 3456.0,
        ),
        trend = emptyList(),
        breakdown = emptyList(),
        recentTransactions = emptyList()
    )

    SmartBudgetTheme {
        HomeScreenContent(
            state = fakeState,
            currencyCode = "USD",
            onIntent = {},
            onAddExpenseClicked = {},
            onAddIncomeClicked = {},
            onViewReportsClicked = {},
            onViewGoalsClicked = {},
            onNavigateToConfig = {},
            onNavigateToAllTransactions = {}
        )
    }
}
