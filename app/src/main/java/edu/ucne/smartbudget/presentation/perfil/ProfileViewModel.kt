package edu.ucne.smartbudget.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.smartbudget.data.local.datastore.SessionDataStore
import edu.ucne.smartbudget.domain.model.Gastos
import edu.ucne.smartbudget.domain.model.Ingresos
import edu.ucne.smartbudget.domain.model.Metas
import edu.ucne.smartbudget.domain.repository.UsuarioRepository
import edu.ucne.smartbudget.domain.usecase.gastosusecase.ObserveGastosUseCase
import edu.ucne.smartbudget.domain.usecase.ingresosusecase.ObserveIngresosUseCase
import edu.ucne.smartbudget.domain.usecase.metasusecase.ObserveMetasUseCase
import edu.ucne.smartbudget.presentation.perfil.components.FinancialData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val observeIngresosUseCase: ObserveIngresosUseCase,
    private val observeGastosUseCase: ObserveGastosUseCase,
    private val observeMetasUseCase: ObserveMetasUseCase,
    private val usuarioRepository: UsuarioRepository,
    private val sessionDataStore: SessionDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState(isLoading = true))
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    val selectedCurrency = sessionDataStore.currencyFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "USD"
    )

    init {
        viewModelScope.launch {
            sessionDataStore.userIdFlow
                .filterNotNull()
                .collect { idGuardado ->

                    val result = usuarioRepository.getUsuario(idGuardado)
                    val usuarioActual = result.data

                    if (usuarioActual == null) {
                        _state.update { it.copy(isLoading = false) }
                        return@collect
                    }

                    combine(
                        observeIngresosUseCase(idGuardado),
                        observeGastosUseCase(idGuardado),
                        observeMetasUseCase(idGuardado)
                    ) { ingresos, gastos, metas ->
                        FinancialData(
                            balance = calcularBalance(ingresos, gastos),
                            metaName = metas.firstOrNull()?.nombre ?: "Sin meta activa",
                            metaGoal = metas.firstOrNull()?.monto ?: 1.0,
                            progress = metas.firstOrNull()?.let { obtenerProgress(it) } ?: 0f
                        )
                    }.onEach { financialData ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                usuarioActual = usuarioActual,
                                userName = usuarioActual.userName,
                                password = usuarioActual.password,
                                totalSaved = financialData.balance,
                                netWorthGrowth = financialData.balance,
                                metaName = financialData.metaName,
                                metaGoal = financialData.metaGoal,
                                metaProgress = financialData.progress
                            )
                        }
                    }.launchIn(viewModelScope)
                }
        }
    }

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.NameChanged -> _state.update { it.copy(userName = event.userName) }
            is ProfileUiEvent.PasswordChanged -> _state.update { it.copy(password = event.password) }
            ProfileUiEvent.SaveChanges -> saveChanges()
            ProfileUiEvent.DeleteAccount -> deleteAccount() // Nuevo evento
            ProfileUiEvent.ClearMessage -> _state.update { it.copy(successMessage = null) }
            else -> Unit
        }
    }

    private fun saveChanges() {
        val currentState = state.value
        val usuarioActual = currentState.usuarioActual ?: return

        if (currentState.userName.isBlank() || currentState.password.isBlank()) {
            _state.update { it.copy(successMessage = "El nombre y la contraseña no pueden estar vacíos") }
            return
        }

        viewModelScope.launch {
            try {
                val updatedUser = usuarioActual.copy(
                    userName = currentState.userName,
                    password = currentState.password
                )
                usuarioRepository.updateUsuario(updatedUser)

                _state.update {
                    it.copy(
                        usuarioActual = updatedUser,
                        successMessage = "Perfil actualizado correctamente"
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(successMessage = "Error al actualizar: ${e.message}") }
            }
        }
    }

    private fun deleteAccount() {
        val usuarioActual = state.value.usuarioActual ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                usuarioRepository.deleteUsuario(usuarioActual.usuarioId)
                sessionDataStore.saveUserId("")

                _state.update {
                    it.copy(
                        isLoading = false,
                        isAccountDeleted = true,
                        successMessage = "Cuenta eliminada correctamente"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Error al eliminar la cuenta: ${e.message}"
                    )
                }
            }
        }
    }


    private fun calcularBalance(
        ingresos: List<Ingresos>,
        gastos: List<Gastos>
    ): Double {
        val totalIngresos = ingresos.sumOf { it.monto }
        val totalGastos = gastos.sumOf { it.monto }
        return totalIngresos - totalGastos
    }

    private fun obtenerProgress(meta: Metas): Float {
        return if (meta.monto > 0)
            (meta.contribucionMensual / meta.monto).toFloat().coerceIn(0f, 1f)
        else 0f
    }
}
