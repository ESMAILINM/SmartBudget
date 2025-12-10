package edu.ucne.smartbudget.presentation.meta.MetaScreen

data class MetaUiState(
    val metaId: String? = null,
    val nombre: String = "",
    val monto: String = "",
    val contribucionMensual: String = "",
    val fecha: String = "",
    val emoji: String = "",
    val imagenes: List<String> = emptyList(),
    val selectedImageIndex: Int = 0,
    val selectedImageUri: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val isNew: Boolean = true,
    val deleted: Boolean = false,
    val isRefreshing: Boolean = false,
    val montoAhorrado : Double = 0.0
)
