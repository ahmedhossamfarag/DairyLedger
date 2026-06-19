package com.example.dairyledger.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dairyledger.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
): ViewModel() {
    val defaultPrice =
        settingsRepository.defaultPrice
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                0.0
            )

    suspend fun setDefaultPrice(price: Double) {
        settingsRepository.setDefaultPrice(price)
    }
}