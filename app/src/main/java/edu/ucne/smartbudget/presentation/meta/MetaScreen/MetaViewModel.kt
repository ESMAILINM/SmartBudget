package edu.ucne.smartbudget.presentation.meta.MetaScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.smartbudget.data.notifications.NotificationService
import edu.ucne.smartbudget.data.local.datastore.SessionDataStore
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Imagenes
import edu.ucne.smartbudget.domain.model.Metas
import edu.ucne.smartbudget.domain.usecase.metasusecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MetaViewModel @Inject constructor(
    private val getMetaUseCase: GetMetaUseCase,
    private val insertMetaUseCase: InsertMetaUseCase,
    private val updateMetaUseCase: UpdateMetaUseCase,
    private val deleteMetaUseCase: DeleteMetaUseCase,
    private val triggerSyncMetaUseCase: TriggerSyncMetaUseCase,
    private val sessionDataStore: SessionDataStore,
    private val notificationService: NotificationService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(MetaUiState(isLoading = true))
    val state: StateFlow<MetaUiState> = _state.asStateFlow()

    private val _metas = MutableStateFlow<List<Metas>>(emptyList())
    val metas: StateFlow<List<Metas>> = _metas.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            sessionDataStore.userIdFlow.collect { id ->
                currentUserId = id
            }
        }

        val metaId = savedStateHandle.get<String>("metaId")
        if (metaId != null && metaId != "0") {
            loadMeta(metaId)
        } else {
            reduce { it.copy(isLoading = false, isNew = true) }
        }

        triggerInitialSync()
    }

    private fun triggerInitialSync() = viewModelScope.launch {
        try { triggerSyncMetaUseCase() } catch (_: Exception) {}
    }

    fun onEvent(event: MetaUiEvent) {
        when (event) {
            is MetaUiEvent.Load -> loadMeta(event.metaId)
            is MetaUiEvent.NombreChanged -> reduce { it.copy(nombre = event.value) }
            is MetaUiEvent.MontoChanged -> reduce { it.copy(monto = event.value) }
            is MetaUiEvent.ContribucionChanged -> reduce { it.copy(contribucionMensual = event.value) }
            is MetaUiEvent.EmojiChanged -> reduce { it.copy(emoji = event.value ?: "") }
            is MetaUiEvent.FechaChanged -> reduce { it.copy(fecha = event.value ?: "") }
            is MetaUiEvent.ImagenesChanged -> reduce {
                it.copy(imagenes = event.lista.filterNotNull().map { url -> url.toString() })
            }
            is MetaUiEvent.ImageSelected ->
                reduce { it.copy(selectedImageIndex = event.selectedIndex, selectedImageUri = event.selectedUri) }
            is MetaUiEvent.Refresh -> refreshMetas()
            MetaUiEvent.Save -> saveMeta()
            MetaUiEvent.Delete -> deleteMeta()
        }
    }

    private fun reduce(transform: (MetaUiState) -> MetaUiState) {
        _state.update(transform)
    }

    fun loadMeta(metaId: String?) {
        if (metaId.isNullOrBlank() || metaId == "0") {
            reduce { it.copy(isNew = true, isLoading = false, errorMessage = null) }
            return
        }

        viewModelScope.launch {
            reduce { it.copy(isLoading = true) }

            when (val result = getMetaUseCase(metaId)) {
                is Resource.Success -> {
                    val meta = result.data
                    if (meta != null) {
                        val urls = meta.imagenes.mapNotNull { it.url }
                        reduce {
                            it.copy(
                                metaId = meta.metaId,
                                nombre = meta.nombre.orEmpty(),
                                monto = meta.monto.toString(),
                                contribucionMensual = meta.contribucionMensual.toString(),
                                fecha = meta.fecha.orEmpty(),
                                emoji = meta.emoji.orEmpty(),
                                imagenes = urls,
                                isNew = false,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                }
                is Resource.Error -> reduce { it.copy(isLoading = false, errorMessage = result.message) }
                else -> reduce { it.copy(isLoading = false) }
            }
        }
    }

    private fun saveMeta() = viewModelScope.launch {
        val current = _state.value

        if (current.nombre.isBlank()) {
            reduce { it.copy(errorMessage = "El nombre no puede estar vacío") }
            return@launch
        }

        val montoDouble = current.monto.toDoubleOrNull()
        if (montoDouble == null || montoDouble <= 0) {
            reduce { it.copy(errorMessage = "Ingresa un monto válido") }
            return@launch
        }

        val userId = currentUserId
        if (userId.isNullOrBlank()) {
            reduce { it.copy(errorMessage = "Usuario no identificado. Intente nuevamente.") }
            return@launch
        }

        reduce { it.copy(isSaving = true, errorMessage = null) }

        try {
            val metaIdLocal = current.metaId ?: UUID.randomUUID().toString()

            val existingMeta = if (!current.isNew && current.metaId != null) {
                when (val result = getMetaUseCase(current.metaId)) {
                    is Resource.Success -> result.data
                    else -> null
                }
            } else null

            val existingImagesMap = existingMeta?.imagenes?.associateBy { it.url } ?: emptyMap()

            val imagenesFinal = current.imagenes.map { url ->
                val existing = existingImagesMap[url]
                if (existing != null) {
                    existing.copy(metaId = metaIdLocal, isPendingCreate = existing.remoteId == null)
                } else {
                    Imagenes(
                        imagenId = UUID.randomUUID().toString(),
                        metaId = metaIdLocal,
                        localUrl = url,
                        url = url,
                        remoteId = null,
                        isPendingCreate = true
                    )
                }
            }

            val meta = Metas(
                metaId = metaIdLocal,
                usuarioId = userId,
                nombre = current.nombre,
                monto = montoDouble,
                contribucionMensual = current.contribucionMensual.toDoubleOrNull() ?: 0.0,
                fecha = current.fecha,
                emoji = current.emoji,
                imagenes = imagenesFinal
            )

            if (current.isNew) insertMetaUseCase(meta)
            else updateMetaUseCase(meta)

            if (meta.contribucionMensual >= meta.monto) {
                try {
                    notificationService.showMetaCompletedNotification(meta.nombre)
                } catch (_: Exception) {}
            }

            reduce { it.copy(isSaving = false, isSuccess = true) }

            try { triggerSyncMetaUseCase() } catch (_: Exception) {}

        } catch (e: Exception) {
            reduce { it.copy(isSaving = false, errorMessage = "Error local: ${e.message}") }
        }
    }

    private fun deleteMeta() = viewModelScope.launch {
        _state.value.metaId?.let { id ->
            try {
                deleteMetaUseCase(id)
                reduce { it.copy(deleted = true, isSuccess = true) }
                try { triggerSyncMetaUseCase() } catch (_: Exception) {}
            } catch (e: Exception) {
                reduce { it.copy(errorMessage = "Error al eliminar: ${e.message}") }
            }
        }
    }

    private fun refreshMetas() = viewModelScope.launch {
        reduce { it.copy(isRefreshing = true) }
        try { triggerSyncMetaUseCase() } catch (_: Exception) {}
        reduce { it.copy(isRefreshing = false) }
    }
}
