package com.example.dairyledger

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.dairyledger.data.DairyRepository
import com.example.dairyledger.data.SettingsRepository

val Context.dataStore by preferencesDataStore(
    name = "settings"
)

class DairyLedgerApp: Application() {
    val repository by lazy { DairyRepository(applicationContext) }
    val settingsRepository by lazy { SettingsRepository(applicationContext) }
}