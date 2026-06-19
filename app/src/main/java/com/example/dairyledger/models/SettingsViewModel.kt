package com.example.dairyledger.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dairyledger.data.SettingsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
): ViewModel() {
    sealed interface UiEvent {
        data object SettingsSaved : UiEvent
        data class Error(val message: String) : UiEvent
    }

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    val defaultPrice =
        settingsRepository.defaultPrice
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                0.0
            )

    fun setDefaultPrice(price: Double) {
        viewModelScope.launch {
            try{
                settingsRepository.setDefaultPrice(price)
                _events.emit(UiEvent.SettingsSaved)
            } catch (e: Exception) {
                _events.emit(UiEvent.Error(e.message ?: "Failed to save collection"))
            }
        }
    }
}