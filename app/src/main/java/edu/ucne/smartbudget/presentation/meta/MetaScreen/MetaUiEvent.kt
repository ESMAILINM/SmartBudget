package edu.ucne.smartbudget.presentation.meta.MetaScreen

import android.net.Uri

sealed interface MetaUiEvent {

    data class Load(val metaId: String) : MetaUiEvent
    data class NombreChanged(val value: String) : MetaUiEvent
    data class MontoChanged(val value: String) : MetaUiEvent
    data class ContribucionChanged(val value: String) : MetaUiEvent
    data class EmojiChanged(val value: String) : MetaUiEvent
    data class FechaChanged(val value: String) : MetaUiEvent
    data class ImagenesChanged(val lista: List<String>) : MetaUiEvent
    data class ImageSelected(val selectedIndex: Int, val selectedUri: String) : MetaUiEvent
    object Save : MetaUiEvent
    object Delete : MetaUiEvent
    object Refresh : MetaUiEvent

}
