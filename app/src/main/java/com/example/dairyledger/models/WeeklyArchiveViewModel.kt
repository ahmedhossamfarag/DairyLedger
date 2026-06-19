package com.example.dairyledger.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dairyledger.data.DairyRepository
import com.example.dairyledger.data.WeekTotal
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class WeeklyArchiveViewModel(
    private val repository: DairyRepository
): ViewModel() {
    sealed interface UiEvent {
        data object NoMoreWeeks : UiEvent
        data class Error(val message: String) : UiEvent
    }

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    var weeks by mutableStateOf<List<WeekTotal>>(emptyList())
        private set

    var activeFarmersCount by mutableIntStateOf(0)

    init {
        viewModelScope.launch {
            weeks = repository.getAllWeeksTotals()
            activeFarmersCount = repository.getActiveFarmers().size
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            try{
                val currentSize = weeks.size
                weeks = repository.getAllWeeksTotals(limit = weeks.size + 5)
                if (weeks.size == currentSize) {
                    _events.emit(UiEvent.NoMoreWeeks)
                }
            } catch (e: Exception) {
                _events.emit(UiEvent.Error(e.message ?: "Failed to load more weeks"))
            }
        }
    }
}