package com.example.dairyledger.data

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.dairyledger.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val context: Context
) {
    companion object {
        val DEFAULT_PRICE =
            doublePreferencesKey("default_price")
    }

    suspend fun setDefaultPrice(price: Double) {
        context.dataStore.edit { prefs ->
            prefs[DEFAULT_PRICE] = price
        }
    }

    val defaultPrice: Flow<Double> =
        context.dataStore.data.map { prefs ->
            prefs[DEFAULT_PRICE] ?: 0.0
        }

}
