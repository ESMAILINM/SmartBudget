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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsuarioViewModel @Inject constructor(
    private val updateUsuarioUseCase: UpdateUsuarioUseCase,
    private val getUsuarioUseCase: GetUsuarioUseCase,
    private val insertUsuarioUseCase: InsertUsuarioUseCase,
    private val observeUsuarioUseCase: ObserveUsuarioUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UsuarioUiState())
    val state: StateFlow<UsuarioUiState> = _state.asStateFlow()

    init { observeUsuarios() }

    private fun observeUsuarios() = viewModelScope.launch {
        observeUsuarioUseCase().collect { usuarios ->
            _state.update { it.copy(usuarios = usuarios, isLoading = false) }
        }
    }

    fun onEvent(event: UsuarioUiEvent) {
        when (event) {
            is UsuarioUiEvent.userNameChanged ->
                _state.update { it.copy(userName = event.usuario) }

            is UsuarioUiEvent.passwordChanged ->
                _state.update { it.copy(password = event.password) }

            is UsuarioUiEvent.Login ->
                login(event.userName, event.password)

            is UsuarioUiEvent.Save ->
                saveUsuario()

            is UsuarioUiEvent.showDialog ->
                _state.update { it.copy(showDialog = true) }

            is UsuarioUiEvent.hideDialog ->
                _state.update { it.copy(showDialog = false) }

            is UsuarioUiEvent.userMessageShown ->
                clearMessage()

            is UsuarioUiEvent.load -> loadUsuario(event.usuarioId)

        }
    }
    private fun loadUsuario(usuarioId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        val res = getUsuarioUseCase(usuarioId)
        when (res) {
            is Resource.Success<*> -> {
                val u = res.data as Usuarios
                _state.update {
                    it.copy(
                        usuarioId = u.usuarioId ?: 0,
                        userName = u.userName,
                        password = u.password,
                        isLoading = false
                    )
                }
            }
            is Resource.Error<*> -> {
                _state.update {
                    it.copy(userMessage = res.message ?: "Error", isLoading = false)
                }
            }
        }
    }


    private fun login(userName: String, password: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        val user = _state.value.usuarios.firstOrNull {
            it.userName == userName && it.password == password
        }

        if (user != null) {
            _state.update {
                it.copy(
                    usuarioActual = user,
                    successLogin = true,
                    isLoading = false,
                    userMessage = "Inicio de sesión exitoso"
                )
            }
        } else {
            _state.update {
                it.copy(
                    isLoading = false,
                    userMessage = "Usuario o contraseña incorrectos"
                )
            }
        }
    }

    private fun saveUsuario() = viewModelScope.launch {
        val current = _state.value
        val usuario = Usuarios(
            usuarioId = current.usuarioId,
            userName = current.userName,
            password = current.password
        )

        _state.update { it.copy(isSaving = true) }

        val isNew = (usuario.usuarioId ?: 0) == 0

        val result = try {
            if (isNew) insertUsuarioUseCase(usuario)
            else updateUsuarioUseCase(usuario)
        } catch (e: Exception) {
            Resource.Error<Usuarios>(e.message ?: "Error desconocido")
        }

        when (result) {
            is Resource.Loading<*> -> {
                _state.update { it.copy(isSaving = true) }
            }

            is Resource.Success<*> -> {
                val createdUser = result.data as? Usuarios ?: usuario
                _state.update {
                    it.copy(
                        isSaving = false,
                        userMessage = if (isNew) "Usuario registrado correctamente" else "Usuario actualizado",
                        successLogin = if (isNew) true else it.successLogin,
                        usuarioActual = if (isNew) createdUser else it.usuarioActual,
                        userName = "",
                        password = "",
                        usuarioId = 0
                    )
                }
            }

            is Resource.Error<*> -> {
                _state.update {
                    it.copy(isSaving = false, userMessage = result.message)
                }
            }
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