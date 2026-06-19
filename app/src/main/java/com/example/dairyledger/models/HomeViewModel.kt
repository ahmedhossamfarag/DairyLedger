package com.example.dairyledger.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dairyledger.data.DairyRepository
import com.example.dairyledger.data.TodayCollectionSummary
import com.example.dairyledger.data.Week
import kotlinx.coroutines.launch
import java.util.Date

class HomeViewModel(
    private val repository: DairyRepository
) : ViewModel() {
    var currentWeek by mutableStateOf<Week?>(null)
        private set
    var currentWeekTotal by mutableFloatStateOf(0.0f)
        private set
    var todayCollectionSummary by mutableStateOf<TodayCollectionSummary?>(null)
        private set

    init {
        viewModelScope.launch {
            currentWeek = repository.currentWeekOrNull()

            if (currentWeek != null) {
                currentWeekTotal = repository.getCurrentWeekTotal()
                todayCollectionSummary = repository.getCurrentWeekTodaysCollectionSummary()
            }
        }
    }

    fun createNewWeek() {
        viewModelScope.launch {
            repository.addWeek(Date())
            currentWeek = repository.currentWeekOrNull()
            currentWeekTotal = repository.getCurrentWeekTotal()
            todayCollectionSummary = repository.getCurrentWeekTodaysCollectionSummary()
        }
    }
}
