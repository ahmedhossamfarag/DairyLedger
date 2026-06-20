package com.example.dairyledger.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dairyledger.data.DairyRepository
import com.example.dairyledger.data.Farmer
import com.example.dairyledger.data.FarmerCollectionDetail
import kotlinx.coroutines.launch

class FarmerDetailsViewModel(
    private val repository: DairyRepository
) : ViewModel() {
    var farmerId by mutableLongStateOf(-1)
        private set
    var farmer by mutableStateOf<Farmer?>(null)
        private set
    var collectionDetails by mutableStateOf<List<FarmerCollectionDetail>>(emptyList())
        private set

    init {
        viewModelScope.launch {
            repository.refreshTrigger.collect {
                if (farmerId >= 0) {
                    collectionDetails = repository.getFarmerCurrentWeekDairies(farmerId)
                }
            }
        }
    }


    fun loadFarmer(id: Long) {
        viewModelScope.launch {
            farmerId = id
            farmer = repository.getFarmerById(farmerId)
            collectionDetails = repository.getFarmerCurrentWeekDairies(farmerId)
        }
    }

    fun setActive() {
        viewModelScope.launch {
            repository.setFarmerActive(farmerId, true)
            farmer = farmer?.copy(active = true)
        }
    }

    fun setInactive() {
        viewModelScope.launch {
            repository.setFarmerActive(farmerId, false)
            farmer = farmer?.copy(active = false)
        }
    }
}