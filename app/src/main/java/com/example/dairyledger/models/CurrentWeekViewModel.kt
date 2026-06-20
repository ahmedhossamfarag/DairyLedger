package com.example.dairyledger.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dairyledger.data.DairyRepository
import com.example.dairyledger.data.Week
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Date

class CurrentWeekViewModel(
    private val repository: DairyRepository,
): ViewModel() {
    sealed interface UiEvent {
        data object WeekClosed : UiEvent
        data class Error(val message: String) : UiEvent
    }

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    var week by mutableStateOf<Week?>(null)
        private set

    var weekTotal by mutableFloatStateOf(0.0f)
        private set

    init {
        viewModelScope.launch {
            week = repository.currentWeekOrNull()

            if (week != null) {
                weekTotal = repository.getCurrentWeekTotal()
            }

            repository.refreshTrigger.collect {
                weekTotal = repository.getCurrentWeekTotal()
            }
        }
    }

    fun closeWeek() {
        viewModelScope.launch {
            try {
                repository.addWeek(Date())
                week = repository.currentWeekOrNull()
                weekTotal = repository.getCurrentWeekTotal()
                _events.emit(UiEvent.WeekClosed)
            } catch (e: Exception) {
                _events.emit(UiEvent.Error(e.message ?: "Failed to close week"))
            }
        }
    }
}