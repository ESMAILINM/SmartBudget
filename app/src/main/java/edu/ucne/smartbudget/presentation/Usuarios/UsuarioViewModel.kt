package edu.ucne.smartbudget.presentation.Usuarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Usuarios
import edu.ucne.smartbudget.domain.usecase.GetUsuarioUseCase
import edu.ucne.smartbudget.domain.usecase.InsertUsuarioUseCase
import edu.ucne.smartbudget.domain.usecase.ObserveUsuarioUseCase
import edu.ucne.smartbudget.domain.usecase.UpdateUsuarioUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class UsuarioViewModel @Inject constructor(
    private val insertUsuarioUseCase: InsertUsuarioUseCase,
    private val updateUsuarioUseCase: UpdateUsuarioUseCase,
    private val getUsuarioUseCase: GetUsuarioUseCase,
    private val observeUsuarioUseCase: ObserveUsuarioUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UsuarioUiState(isLoading = true))
    val state: StateFlow<UsuarioUiState> = _state.asStateFlow()

    init {
        observeUsuarios()
    }

    fun onEvent(event: UsuarioUiEvent) {
        when (event) {
            is UsuarioUiEvent.userNameChanged ->
                _state.update { it.copy(userName = event.usuario) }

            is UsuarioUiEvent.passwordChanged ->
                _state.update { it.copy(password = event.password) }

            is UsuarioUiEvent.load ->
                loadUsuario(event.usuarioId)

            is UsuarioUiEvent.Save ->
                saveUsuario()

            is UsuarioUiEvent.Login ->
                login(event.userName, event.password)

            is UsuarioUiEvent.showDialog ->
                _state.update { it.copy(showDialog = true) }

            is UsuarioUiEvent.hideDialog ->
                _state.update { it.copy(showDialog = false) }

            is UsuarioUiEvent.userMessageShown ->
                clearMessage()

            is UsuarioUiEvent.ToggleLoginMode -> {
                _state.update { current ->
                    current.copy(isLogin = !current.isLogin)
                }
            }
        }
    }

    private fun observeUsuarios() = viewModelScope.launch {
        observeUsuarioUseCase().collect { usuarios ->
            _state.update {
                it.copy(
                    usuarios = usuarios,
                    isLoading = false
                )
            }
        }
    }

    private fun loadUsuario(id: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val result = getUsuarioUseCase(id)) {
            is Resource.Success -> {
                val user = result.data!!
                _state.update {
                    it.copy(
                        usuarioId = user.usuarioId ?: 0,
                        userName = user.userName,
                        password = user.password,
                        isLoading = false
                    )
                }
            }

            is Resource.Error ->
                _state.update { it.copy(isLoading = false, userMessage = result.message) }

            else -> {}
        }
    }

    private fun login(userName: String, password: String) = viewModelScope.launch {
        val user = _state.value.usuarios.firstOrNull {
            it.userName == userName && it.password == password
        }

        if (user != null) {
            _state.update {
                it.copy(
                    usuarioActual = user,
                    successLogin = true,
                    userMessage = "Inicio de sesión exitoso"
                )
            }
        } else {
            _state.update {
                it.copy(
                    loginError = "Usuario o contraseña incorrectos",
                    successLogin = false
                )
            }
        }
    }

    private fun saveUsuario() = viewModelScope.launch {
        val ui = _state.value

        val usuario = Usuarios(
            usuarioId = ui.usuarioId,
            userName = ui.userName,
            password = ui.password
        )

        _state.update { it.copy(isSaving = true) }

        val isNew = (usuario.usuarioId ?: 0) == 0

        val result = if (isNew)
            insertUsuarioUseCase(usuario)
        else
            updateUsuarioUseCase(usuario)

        when (result) {
            is Resource.Success -> {
                val created = result.data ?: usuario

                _state.update {
                    it.copy(
                        isSaving = false,
                        userMessage = if (isNew)
                            "Usuario registrado correctamente"
                        else
                            "Usuario actualizado",
                        usuarioActual = created,
                        successLogin = isNew || it.successLogin,
                        userName = "",
                        password = "",
                        usuarioId = 0
                    )
                }
            }

            is Resource.Error ->
                _state.update { it.copy(isSaving = false, userMessage = result.message) }

            else -> {}

        }
    }

    fun limpiarSuccessLogin() {
        _state.update { it.copy(successLogin = false) }
    }

    fun logout() {
        _state.update {
            it.copy(
                usuarioActual = null,
                userName = "",
                password = "",
                usuarioId = 0,
                successLogin = false,
                userMessage = null,
                isLoading = false,
                isSaving = false
            )
        }
    }

    private fun clearMessage() {
        _state.update { it.copy(userMessage = null) }
    }
}