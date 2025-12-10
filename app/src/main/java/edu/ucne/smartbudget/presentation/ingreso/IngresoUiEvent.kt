package edu.ucne.smartbudget.presentation.ingreso

import edu.ucne.smartbudget.domain.model.Ingresos

sealed class IngresoUiEvent {
    data class DescripcionChanged(val descripcion: String) : IngresoUiEvent()

    data class FechaChanged(val fecha: String) : IngresoUiEvent()

    data class CategoriaIdChanged(val categoriaId: String) : IngresoUiEvent()

    data class MontoChanged(val monto: String) : IngresoUiEvent()

    data class Load(val usuarioId: String) : IngresoUiEvent()

    object Save : IngresoUiEvent()

    object ShowDialog : IngresoUiEvent()

    object HideDialog : IngresoUiEvent()

    data class Delete(val id: String) : IngresoUiEvent()

    data class Edit(val ingreso: Ingresos) : IngresoUiEvent()
    object Refresh : IngresoUiEvent()
}
