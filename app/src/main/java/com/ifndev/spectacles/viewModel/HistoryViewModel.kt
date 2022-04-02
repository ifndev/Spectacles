package com.ifndev.spectacles.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import fr.patrickconti.specto.model.Painting

class HistoryViewModel : ViewModel() {
    val historyState = mutableStateListOf<Painting>()

    fun addPainting(painting: Painting) {
        historyState.add(painting)
    }
}