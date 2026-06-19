package com.example.dairyledger.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dairyledger.data.CollectionType
import com.example.dairyledger.data.DairyRepository
import com.example.dairyledger.data.Farmer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CollectViewModel(
    private val repository: DairyRepository
): ViewModel() {
    sealed interface UiEvent {
        data object CollectionSaved : UiEvent
        data class Error(val message: String) : UiEvent
    }

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    var activeFarmers by mutableStateOf<List<Farmer>>(emptyList())
        private set

    init {
        viewModelScope.launch {
            activeFarmers = repository.getActiveFarmers()
        }
    }

    fun saveCollection(type: String, data: Map<Int, Double>) {
        viewModelScope.launch {
            try {
                val currentWeek = repository.currentWeekOrNull()
                if (currentWeek != null) {
                    val collectionType = if (type == "morning") CollectionType.MORNING else CollectionType.EVENING
                    val collectionId = repository.addCollection(currentWeek.id, collectionType)

                    data.forEach { (farmerId, liters) ->
                        repository.addDairy(collectionId, farmerId.toLong(), liters.toFloat())
                    }

                    _events.emit(UiEvent.CollectionSaved)
                } else {
                    _events.emit(UiEvent.Error("No current week found"))
                }
            } catch (e: Exception) {
                _events.emit(UiEvent.Error(e.message ?: "Failed to save collection"))
            }
        }
    }
}