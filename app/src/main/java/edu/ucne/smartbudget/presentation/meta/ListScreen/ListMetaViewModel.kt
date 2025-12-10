package edu.ucne.smartbudget.presentation.metas.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.smartbudget.data.local.datastore.SessionDataStore
import edu.ucne.smartbudget.domain.usecase.metasusecase.DeleteMetaUseCase
import edu.ucne.smartbudget.domain.usecase.metasusecase.ObserveMetasUseCase
import edu.ucne.smartbudget.domain.usecase.metasusecase.TriggerSyncMetaUseCase
import edu.ucne.smartbudget.presentation.meta.ListScreen.ListMetaUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListMetaViewModel @Inject constructor(
    private val observeMetasUseCase: ObserveMetasUseCase,
    private val deleteMetaUseCase: DeleteMetaUseCase,
    private val triggerSyncMetaUseCase: TriggerSyncMetaUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListMetaUiState())
    val uiState: StateFlow<ListMetaUiState> = _uiState.asStateFlow()

    val selectedCurrency: StateFlow<String> = sessionDataStore.currencyFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "USD"
        )

    private var syncJob: Job? = null
    private var currentUserId: String = ""

    init {
        viewModelScope.launch {
            sessionDataStore.userIdFlow.collectLatest { userId ->
                if (!userId.isNullOrBlank()) {
                    currentUserId = userId
                    observeLocalData(userId)
                    syncRemoteData()
                } else {
                    _uiState.update { it.copy(metas = emptyList()) }
                }
            }
        }
    }

    private fun observeLocalData(userId: String) {
        viewModelScope.launch {
            observeMetasUseCase(userId)
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = "Error cargando datos locales: ${e.message}") }
                }
                .collect { metas ->
                    _uiState.update { it.copy(metas = metas, isLoading = false, errorMessage = null) }
                }
        }
    }

    private fun syncRemoteData() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            try {
                if (_uiState.value.metas.isEmpty()) {
                    _uiState.update { it.copy(isLoading = true) }
                }
                triggerSyncMetaUseCase()
            } catch (e: Exception) {
                _uiState.update {
                    if (it.metas.isEmpty()) {
                        it.copy(errorMessage = "Modo Offline: No se pudo sincronizar.")
                    } else {
                        it.copy(errorMessage = null)
                    }
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteMeta(metaId: String) {
        viewModelScope.launch {
            try {
                deleteMetaUseCase(metaId)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al eliminar: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                triggerSyncMetaUseCase()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }
}
