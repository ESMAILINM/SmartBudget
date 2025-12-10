package edu.ucne.smartbudget.presentation.gasto

sealed class GastoUiEvent {
    object LoadGastos : GastoUiEvent()
    object SyncGastos : GastoUiEvent()
    object ShowDialog : GastoUiEvent()
    object HideDialog : GastoUiEvent()
    object Save : GastoUiEvent()

    data class Edit(val gastoId: String) : GastoUiEvent()

    data class Delete(val gastoId: String) : GastoUiEvent()

    data class MontoChanged(val monto: String) : GastoUiEvent()

    data class DescripcionChanged(val descripcion: String) : GastoUiEvent()

    data class CategoriaIdChanged(val categoriaId: String) : GastoUiEvent()

    data class FechaChanged(val fecha: String) : GastoUiEvent()

    data class CategoriaSelected(val categoria: String?) : GastoUiEvent()

    data class MontoOrdenChanged(val orden: String) : GastoUiEvent()
}
