package com.example.dairyledger.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dairyledger.data.DairyRepository
import com.example.dairyledger.data.SettingsRepository

class ViewModelFactory(private val repository: DairyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(CollectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CollectViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportsViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(FarmersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FarmersViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(WeeklyArchiveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeeklyArchiveViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(CurrentWeekViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurrentWeekViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(FarmerDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FarmerDetailsViewModel(repository) as T
        }
        // Add other ViewModels here as you create them
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SettingsViewModelFactory(private val settingsRepository: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}