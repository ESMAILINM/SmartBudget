package edu.ucne.smartbudget.presentation.meta.MetaScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.smartbudget.domain.model.Metas
import edu.ucne.smartbudget.presentation.meta.componentes.MetaScreenContent


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MetaScreen(
    metaId: String? = null,
    viewModel: MetaViewModel = hiltViewModel(),
    onClose: () -> Unit
) {
    LaunchedEffect(metaId) {
        metaId?.let { viewModel.loadMeta(it) }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val metas by viewModel.metas.collectAsStateWithLifecycle(initialValue = emptyList())

    MetaScreenContent(
        state = state,
        metas = metas,
        onEvent = viewModel::onEvent,
        onClose = onClose
    )
}

@Preview(showBackground = true)
@Composable
fun MetaScreenPreview() {
    val fakeState = MetaUiState(
        nombre = "Vacaciones",
        monto = "5000",
        contribucionMensual = "500",
        emoji = "🏖️",
        fecha = "2025-12-01",
        imagenes = listOf(),
        selectedImageIndex = 0,
        isSuccess = false,
        isRefreshing = false
    )
    val fakeMetas = listOf<Metas>()

    MetaScreenContent(
        state = fakeState,
        metas = fakeMetas,
        onEvent = {},
        onClose = {}
    )
}
