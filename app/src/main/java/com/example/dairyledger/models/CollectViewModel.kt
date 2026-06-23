package com.example.dairyledger.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dairyledger.data.Collection
import com.example.dairyledger.data.CollectionType
import com.example.dairyledger.data.DairyRepository
import com.example.dairyledger.data.Farmer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.collections.set

class CollectViewModel(
    private val repository: DairyRepository
): ViewModel() {
    sealed interface UiEvent {
        data object CollectionSaved : UiEvent
        data class Error(val message: String) : UiEvent
    }

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    var collectionId by mutableLongStateOf(-2)

    var collection by mutableStateOf<Collection?>(null)

    var activeFarmers by mutableStateOf<List<Farmer>>(emptyList())
        private set

    val volumes = mutableStateMapOf<Long, String>()

    fun loadCollection(id: Long = -1) {
        viewModelScope.launch {
            if (collectionId != id) {
                collectionId = id
                volumes.clear()
                if (id < 0) {
                    activeFarmers = repository.getActiveFarmers()
                    activeFarmers.forEach { volumes[it.id] = "0.0" }
                } else {
                    collection = repository.getCollection(collectionId)
                    val dairies = repository.getCollectionDairies(collectionId)
                    activeFarmers = dairies.map { Farmer(it.farmerId, it.farmerName, -1) }
                    dairies.forEach {
                        volumes[it.farmerId] =
                            String.format(Locale.US, "%.2f",  it.value)
                    }
                }
            }
        }
    }

    fun resetVolumes() {
        volumes.keys.forEach { id ->
            volumes[id] = "0.0"
        }
    }

    fun saveCollection(type: String) {
        viewModelScope.launch {
            try {
                val currentWeek = repository.currentWeekOrNull()
                if (currentWeek != null) {
                    val collectionType = if (type == "morning") CollectionType.MORNING else CollectionType.EVENING

                    if (collectionId < 0) {
                        collectionId = repository.addCollection(currentWeek.id, collectionType)
                        volumes.forEach { (farmerId, liters) ->
                            repository.addDairy(collectionId, farmerId, liters.toFloat())
                        }
                        resetVolumes()
                    } else {
                        volumes.forEach { (farmerId, liters) ->
                            liters.toFloatOrNull()?.let {
                                repository.updateDairy(collectionId, farmerId, it)
                            }
                        }
                    }

                    _events.emit(UiEvent.CollectionSaved)
                    repository.notifyDataChanged()
                } else {
                    _events.emit(UiEvent.Error("No current week found"))
                }
            } catch (e: Exception) {
                _events.emit(UiEvent.Error(e.message ?: "Failed to save collection"))
            }
        }
    }
}