package com.example.dairyledger.data

import android.content.Context
import java.util.Calendar
import java.util.Date

/**
 * Repository exposing all the operations needed by the app.
 * All functions are `suspend` — call them from a coroutine (e.g. viewModelScope.launch { }).
 */
class DairyRepository(context: Context) {

    private val db = AppDatabase.Companion.getInstance(context)
    private val farmerDao = db.farmerDao()
    private val weekDao = db.weekDao()
    private val collectionDao = db.collectionDao()
    private val dairyDao = db.dairyDao()

    // ============================================================
    // Helpers
    // ============================================================

    /** Returns the start (inclusive) and end (exclusive) of "today" as Dates. */
    private fun todayRange(): Pair<Date, Date> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.time
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val end = cal.time
        return start to end
    }

    /** Fetches the most recently created week, or null if none exist yet. */
    private suspend fun currentWeekOrNull(): Week? = weekDao.getCurrentWeek()

    // ============================================================
    // Add Farmer
    // ============================================================

    suspend fun addFarmer(name: String, phone: String, note: String = "", active: Boolean = true): Long {
        val farmer = Farmer(name = name, phone = phone, note = note, active = active)
        return farmerDao.insert(farmer)
    }

    // ============================================================
    // Mark Farmer Active / Inactive
    // ============================================================

    suspend fun setFarmerActive(farmerId: Long, active: Boolean) {
        farmerDao.setActive(farmerId, active)
    }

    // ============================================================
    // Add Week
    // ============================================================

    suspend fun addWeek(startDate: Date): Long {
        return weekDao.insert(Week(startDate = startDate))
    }

    // ============================================================
    // Add Collection
    // ============================================================

    suspend fun addCollection(weekId: Long, type: CollectionType, timestamp: Date = Date()): Long {
        val collection = Collection(weekId = weekId, type = type, timestamp = timestamp)
        return collectionDao.insert(collection)
    }

    // ============================================================
    // Add Dairy
    // ============================================================

    suspend fun addDairy(collectionId: Long, farmerId: Long, value: Float) {
        dairyDao.insert(Dairy(collectionId = collectionId, farmerId = farmerId, value = value))
    }

    // ============================================================
    // Get Current Week Today's Collections
    // (Total Dairies amount, and Count of Farmers)
    // ============================================================

    suspend fun getCurrentWeekTodaysCollectionSummary(): TodayCollectionSummary? {
        val week = currentWeekOrNull() ?: return null
        val (start, end) = todayRange()
        return dairyDao.getTodayCollectionSummary(week.id, start, end)
    }

    // ============================================================
    // Get Current Week Total Dairies amount
    // ============================================================

    suspend fun getCurrentWeekTotal(): Float {
        val week = currentWeekOrNull() ?: return 0f
        return dairyDao.getWeekTotal(week.id)
    }

    // ============================================================
    // Get Current Active Farmers
    // ============================================================

    suspend fun getActiveFarmers(): List<Farmer> {
        return farmerDao.getActiveFarmers()
    }

    // ============================================================
    // Get All Farmers
    // ============================================================

    suspend fun getAllFarmers(): List<Farmer> {
        return farmerDao.getAllFarmers()
    }

    // ============================================================
    // Get a Week (Sum of Dairies Per Farmer)
    // ============================================================

    suspend fun getWeekFarmerTotals(weekId: Long): List<FarmerWeekTotal> {
        return dairyDao.getFarmerTotalsForWeek(weekId)
    }

    // ============================================================
    // Get all Weeks Total Dairies amount
    // ============================================================

    suspend fun getAllWeeksTotals(): List<WeekTotal> {
        return dairyDao.getAllWeeksTotals()
    }

    // ============================================================
    // Get a Farmer's Dairies of Current Week, along with Collection Type
    // ============================================================

    suspend fun getFarmerCurrentWeekDairies(farmerId: Long): List<FarmerCollectionDetail> {
        val week = currentWeekOrNull() ?: return emptyList()
        return dairyDao.getFarmerDairiesForWeek(week.id, farmerId)
    }
}
