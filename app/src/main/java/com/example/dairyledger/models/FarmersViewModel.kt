package com.example.dairyledger.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dairyledger.data.DairyRepository
import com.example.dairyledger.data.Farmer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FarmersViewModel(
    private val repository: DairyRepository
): ViewModel() {
    sealed interface UiEvent {
        data object FarmerAdded : UiEvent
        data class Error(val message: String) : UiEvent
        data object NameLengthError : UiEvent
    }

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    var farmers by mutableStateOf<List<Farmer>>(emptyList())
        private set

    var activeFarmer by mutableStateOf<Farmer?>(null)

    init {
        viewModelScope.launch {
            farmers = repository.getAllFarmers()
        }
    }

    fun loadFarmer(id: Long) {
        viewModelScope.launch {
            if (id == -1L) {
                activeFarmer = null
            } else {
                activeFarmer = repository.getFarmerById(id)
            }
        }
    }

    fun addFarmer(farmer: Farmer) {
        viewModelScope.launch {
            try {
                if (farmer.name.trim().length < 3) {
                    _events.emit(UiEvent.NameLengthError)
                    return@launch
                }
                if (farmers.any { it.name.trim() == farmer.name.trim() && it.id != activeFarmer?.id }) {
                    _events.emit(UiEvent.Error("Farmer with the same name already exists"))
                    return@launch
                }
                if (activeFarmer != null) {
                    repository.updateFarmer(
                        activeFarmer!!.id,
                        farmer.name.trim(),
                        farmer.order,
                        farmer.phone.trim(),
                        farmer.note.trim(),
                        farmer.active
                    )
                } else {
                    repository.addFarmer(
                        farmer.name.trim(),
                        farmer.order,
                        farmer.phone.trim(),
                        farmer.note.trim(),
                        farmer.active
                    )
                }
                _events.emit(UiEvent.FarmerAdded)
                farmers = repository.getAllFarmers()
            } catch (e: Exception) {
                _events.emit(UiEvent.Error(e.message ?: "Failed to add farmer"))
            }
        }
    }
}