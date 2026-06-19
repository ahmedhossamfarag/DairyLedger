package com.example.dairyledger.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dairyledger.data.DairyRepository
import com.example.dairyledger.data.FarmerWeekTotal
import com.example.dairyledger.data.Week
import kotlinx.coroutines.launch

class ReportsViewModel(
    private val repository: DairyRepository,
): ViewModel() {
    var weekId by mutableLongStateOf(-1)
        private set
    var week by mutableStateOf<Week?>(null)
        private set
    var farmerWeekTotal by mutableStateOf<List<FarmerWeekTotal>>(emptyList())
        private set

    fun loadWeek(id: Long) {
        viewModelScope.launch {
            weekId = id
            week = if (weekId < 0){
                repository.currentWeekOrNull()
            } else {
                repository.getWeekById(weekId)
            }

            if (week != null) {
                farmerWeekTotal = repository.getWeekFarmerTotals(weekId)
            }
        }
    }
}