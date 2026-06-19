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
    }

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    var farmers by mutableStateOf<List<Farmer>>(emptyList())
        private set

    init {
        viewModelScope.launch {
            farmers = repository.getAllFarmers()
        }
    }

    fun addFarmer(farmer: Farmer) {
        viewModelScope.launch {
            try {
                repository.addFarmer(farmer.name, farmer.phone, farmer.note, farmer.active)
                _events.emit(UiEvent.FarmerAdded)
                farmers = repository.getAllFarmers()
            } catch (e: Exception) {
                _events.emit(UiEvent.Error(e.message ?: "Failed to add farmer"))
            }
        }
    }
}