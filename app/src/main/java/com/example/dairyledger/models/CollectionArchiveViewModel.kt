package com.example.dairyledger.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dairyledger.data.CollectionWithTotal
import com.example.dairyledger.data.DairyRepository
import kotlinx.coroutines.launch

class CollectionArchiveViewModel(
    private val repository: DairyRepository,
): ViewModel() {

    var collections by mutableStateOf<List<CollectionWithTotal>>(emptyList())
        private set


    init {
        viewModelScope.launch {
            collections = repository.getCurrentWeekCollections()
        }
    }
}