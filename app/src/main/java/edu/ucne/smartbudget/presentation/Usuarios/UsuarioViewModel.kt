package edu.ucne.smartbudget.presentation.Usuarios

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.smartbudget.data.local.datastore.SessionDataStore
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Usuarios
import edu.ucne.smartbudget.domain.usecase.usuariousecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UsuarioViewModel @Inject constructor(
    private val insertUsuarioUseCase: InsertUsuarioUseCase,
    private val observeUsuarioUseCase: ObserveUsuarioUseCase,
    private val triggerSyncUseCase: TriggerSyncUseCase,
    private val sessionDataStore: SessionDataStore,
    private val validationAuthUseCase: ValidationAuthUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UsuarioUiState(isLoading = true))
    val state: StateFlow<UsuarioUiState> = _state.asStateFlow()

    init {
        observeUsuarios()
        triggerSync()
    }

    fun onEvent(event: UsuarioUiEvent) {
        when (event) {
            is UsuarioUiEvent.UserNameChanged -> _state.update {
                it.copy(userName = event.value, userNameError = null)
            }
            is UsuarioUiEvent.PasswordChanged -> _state.update {
                it.copy(password = event.value, passwordError = null)
            }
            is UsuarioUiEvent.ConfirmPasswordChanged -> _state.update {
                it.copy(confirmPassword = event.value, confirmPasswordError = null)
            }
            is UsuarioUiEvent.RememberMeChanged -> _state.update {
                it.copy(rememberMe = event.isChecked)
            }
            is UsuarioUiEvent.ToggleLoginMode -> _state.update {
                it.copy(
                    isLogin = !it.isLogin,
                    userMessage = null,
                    userNameError = null,
                    passwordError = null,
                    confirmPasswordError = null
                )
            }
            is UsuarioUiEvent.Login -> login(event.user, event.pass)
            is UsuarioUiEvent.Save -> saveUsuario()
            is UsuarioUiEvent.OnForgotPasswordClicked -> _state.update {
                it.copy(showRecoveryDialog = true, recoveryEmail = "")
            }
            is UsuarioUiEvent.OnRecoveryDialogDismissed -> _state.update {
                it.copy(showRecoveryDialog = false)
            }
            is UsuarioUiEvent.OnRecoveryEmailChanged -> _state.update {
                it.copy(recoveryEmail = event.value)
            }
            is UsuarioUiEvent.OnSendRecoveryEmail -> sendRecoveryEmail(event.context)
            is UsuarioUiEvent.HideDialog -> _state.update {
                it.copy(showDialog = false)
            }
            is UsuarioUiEvent.UserMessageShown -> _state.update {
                it.copy(userMessage = null)
            }
        }
    }

    private fun triggerSync() = viewModelScope.launch {
        try {
            triggerSyncUseCase()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun observeUsuarios() = viewModelScope.launch {
        observeUsuarioUseCase()
            .onStart { _state.update { it.copy(isLoading = true) } }
            .catch { e ->
                _state.update { it.copy(userMessage = "Error cargando usuarios: ${e.message}", isLoading = false, showDialog = false) }
            }
            .collect { usuarios ->
                _state.update { it.copy(usuarios = usuarios, isLoading = false) }
            }
    }

    private fun login(userName: String, password: String) = viewModelScope.launch {
        val nameResult = validationAuthUseCase.validateUser(userName)
        val passResult = validationAuthUseCase.validatePassword(password)
        val hasError = listOf(nameResult, passResult).any { !it.successful }

        if (hasError) {
            _state.update {
                it.copy(
                    showDialog = false,
                    userNameError = nameResult.errorMessage,
                    passwordError = passResult.errorMessage
                )
            }
            return@launch
        }
        _state.update { it.copy(isLoading = true) }

        try {
            val user = _state.value.usuarios.firstOrNull {
                it.userName == userName && it.password == password
            }

            if (user != null) {
                handleSuccessfulSession(user)
                triggerSync()
            } else {
                _state.update {
                    it.copy(
                        loginError = "Usuario o contraseña incorrectos",
                        successLogin = false,
                        showDialog = true,
                        userMessage = "Credenciales incorrectas",
                        isLoading = false
                    )
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, userMessage = "Error en login: ${e.message}", showDialog = true) }
        }
    }

    private fun saveUsuario() = viewModelScope.launch {
        val currentState = _state.value
        val nameResult = validationAuthUseCase.validateUser(currentState.userName)
        val passResult = validationAuthUseCase.validatePassword(currentState.password)
        val confirmResult = validationAuthUseCase.validateRepeatedPassword(
            currentState.password,
            currentState.confirmPassword
        )
        val availabilityResult = validationAuthUseCase.validateUserAvailability(
            currentState.userName,
            currentState.usuarios
        )
        val hasError = listOf(nameResult, passResult, confirmResult, availabilityResult).any { !it.successful }

        if (hasError) {
            _state.update {
                it.copy(
                    showDialog = false,
                    userNameError = nameResult.errorMessage ?: availabilityResult.errorMessage,
                    passwordError = passResult.errorMessage,
                    confirmPasswordError = confirmResult.errorMessage
                )
            }
            return@launch
        }

        _state.update { it.copy(isSaving = true) }

        val newUsuario = Usuarios(
            usuarioId = UUID.randomUUID().toString(),
            userName = currentState.userName,
            password = currentState.password,
            remoteId = null
        )

        try {
            when (val result = insertUsuarioUseCase(newUsuario)) {
                is Resource.Success -> {
                    val savedUser = result.data ?: newUsuario
                    handleSuccessfulSession(savedUser)
                    triggerSync()
                    _state.update { it.copy(userMessage = "Cuenta creada correctamente", isSaving = false, showDialog = false) }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            userMessage = result.message ?: "Error al guardar usuario",
                            showDialog = true
                        )
                    }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isSaving = true) }
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isSaving = false,
                    userMessage = "Error inesperado: ${e.message}",
                    showDialog = true
                )
            }
        }
    }

    private suspend fun handleSuccessfulSession(user: Usuarios) {
        sessionDataStore.saveUserId(user.usuarioId)
        _state.update {
            it.copy(
                usuarioActual = user,
                successLogin = true,
                userMessage = "Inicio de sesión exitoso",
                isLoading = false,
                isSaving = false,
                showDialog = false,
                userNameError = null,
                passwordError = null,
                confirmPasswordError = null
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionDataStore.clearUserId()
            _state.value = UsuarioUiState(isLoading = false)
            observeUsuarios()
        }
    }

    fun limpiarSuccessLogin() {
        _state.update { it.copy(successLogin = false) }
    }

    private fun sendRecoveryEmail(context: Context) {
        val emailUsuario = _state.value.recoveryEmail

        if (emailUsuario.isBlank()) {
            _state.update { it.copy(userMessage = "Ingresa tu correo para recuperar") }
            return
        }

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("esmailinmartinez05@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Recuperación de contraseña - SmartBudget")
            putExtra(
                Intent.EXTRA_TEXT,
                "Hola soporte,\n\nSoy el usuario con correo: $emailUsuario\nOlvidé mi contraseña y solicito asistencia."
            )
        }

        try {
            context.startActivity(intent)
            _state.update { it.copy(showRecoveryDialog = false) }
        } catch (e: Exception) {
            _state.update { it.copy(userMessage = "No se encontró una aplicación de correo instalada.") }
        }
    }
}