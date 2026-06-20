package com.example.dairyledger.data

import android.content.Context
import androidx.room.*
import java.util.Date

// ============================================================
// ENTITIES
// ============================================================

@Entity(tableName = "farmer")
data class Farmer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val note: String = "",
    val active: Boolean = true
)

@Entity(tableName = "week")
data class Week(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startDate: Date
)

enum class CollectionType {
    MORNING, EVENING
}

@Entity(
    tableName = "collection",
    foreignKeys = [
        ForeignKey(
            entity = Week::class,
            parentColumns = ["id"],
            childColumns = ["weekId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("weekId")]
)
data class Collection(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weekId: Long,
    val type: CollectionType,
    val timestamp: Date
)

@Entity(
    tableName = "dairy",
    primaryKeys = ["collectionId", "farmerId"],
    foreignKeys = [
        ForeignKey(
            entity = Collection::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Farmer::class,
            parentColumns = ["id"],
            childColumns = ["farmerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("collectionId"), Index("farmerId")]
)
data class Dairy(
    val collectionId: Long,
    val farmerId: Long,
    val value: Float
)

// ============================================================
// TYPE CONVERTERS
// ============================================================

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromCollectionType(value: String?): CollectionType? =
        value?.let { CollectionType.valueOf(it) }

    @TypeConverter
    fun collectionTypeToString(type: CollectionType?): String? = type?.name
}

// ============================================================
// DATA CLASSES FOR QUERY RESULTS
// ============================================================

data class FarmerWeekTotal(
    val farmerId: Long,
    val farmerName: String,
    val total: Float
)

data class WeekTotal(
    val weekId: Long,
    val startDate: Date,
    val total: Float
)

data class TodayCollectionSummary(
    val type: CollectionType,
    val totalAmount: Float,
    val farmerCount: Int
)

data class FarmerCollectionDetail(
    val collectionId: Long,
    val type: CollectionType,
    val timestamp: Date,
    val value: Float
)

// ============================================================
// DAOs
// ============================================================

@Dao
interface FarmerDao {

    @Insert
    suspend fun insert(farmer: Farmer): Long

    @Query("UPDATE farmer SET active = :active WHERE id = :farmerId")
    suspend fun setActive(farmerId: Long, active: Boolean)

    @Query("SELECT * FROM farmer WHERE active = 1 ORDER BY name ASC")
    suspend fun getActiveFarmers(): List<Farmer>

    @Query("SELECT * FROM farmer ORDER BY name ASC")
    suspend fun getAllFarmers(): List<Farmer>

    @Query("SELECT * FROM farmer WHERE id = :farmerId LIMIT 1")
    suspend fun getFarmerById(farmerId: Long): Farmer?
}

@Dao
interface WeekDao {

    @Insert
    suspend fun insert(week: Week): Long

    @Query("SELECT * FROM week ORDER BY startDate DESC LIMIT 1")
    suspend fun getCurrentWeek(): Week?

    @Query("SELECT * FROM week WHERE id = :weekId LIMIT 1")
    suspend fun getWeekById(weekId: Long): Week?

    @Query("SELECT * FROM week ORDER BY startDate DESC")
    suspend fun getAllWeeks(): List<Week>
}

@Dao
interface CollectionDao {

    @Insert
    suspend fun insert(collection: Collection): Long

    @Query("SELECT * FROM collection WHERE weekId = :weekId ORDER BY timestamp ASC")
    suspend fun getCollectionsForWeek(weekId: Long): List<Collection>

    @Query(
        """
        SELECT * FROM collection 
        WHERE weekId = :weekId 
        AND timestamp >= :dayStart AND timestamp < :dayEnd
        ORDER BY timestamp ASC
        """
    )
    suspend fun getCollectionsForDay(weekId: Long, dayStart: Date, dayEnd: Date): List<Collection>
}

@Dao
interface DairyDao {

    @Insert
    suspend fun insert(dairy: Dairy)

    @Query(
        """
        SELECT d.* FROM dairy d
        INNER JOIN collection c ON d.collectionId = c.id
        WHERE c.weekId = :weekId
        """
    )
    suspend fun getDairiesForWeek(weekId: Long): List<Dairy>

    // ---------- Today's collection summary (current week) ----------
    @Query(
        """
        SELECT c.type as type,
               COALESCE(SUM(d.value), 0.0) as totalAmount,
               COUNT(DISTINCT d.farmerId) as farmerCount
        FROM dairy d
        INNER JOIN collection c ON d.collectionId = c.id
        WHERE c.weekId = :weekId
        AND c.timestamp >= :dayStart AND c.timestamp < :dayEnd
        GROUP BY c.id
        """
    )
    suspend fun getTodayCollectionSummary(
        weekId: Long,
        dayStart: Date,
        dayEnd: Date
    ): List<TodayCollectionSummary>

    // ---------- Current week total dairies amount ----------
    @Query(
        """
        SELECT COALESCE(SUM(d.value), 0.0) FROM dairy d
        INNER JOIN collection c ON d.collectionId = c.id
        WHERE c.weekId = :weekId
        """
    )
    suspend fun getWeekTotal(weekId: Long): Float

    // ---------- Sum of dairies per farmer for a given week ----------
    @Query(
        """
        SELECT f.id as farmerId, f.name as farmerName, COALESCE(SUM(d.value), 0.0) as total
        FROM farmer f
        INNER JOIN dairy d ON d.farmerId = f.id
        INNER JOIN collection c ON d.collectionId = c.id
        WHERE c.weekId = :weekId
        GROUP BY f.id, f.name
        ORDER BY f.name ASC
        """
    )
    suspend fun getFarmerTotalsForWeek(weekId: Long): List<FarmerWeekTotal>

    // ---------- All weeks total dairies amount ----------
    @Query(
        """
        SELECT w.id as weekId, w.startDate as startDate, COALESCE(SUM(d.value), 0.0) as total
        FROM week w
        LEFT JOIN collection c ON c.weekId = w.id
        LEFT JOIN dairy d ON d.collectionId = c.id
        GROUP BY w.id, w.startDate
        ORDER BY w.startDate DESC
        LIMIT :limit
        """
    )
    suspend fun getAllWeeksTotals(limit: Int = 5): List<WeekTotal>

    // ---------- A farmer's dairies for current week, with collection type ----------
    @Query(
        """
        SELECT c.id as collectionId, c.type as type, c.timestamp as timestamp, d.value as value
        FROM dairy d
        INNER JOIN collection c ON d.collectionId = c.id
        WHERE c.weekId = :weekId AND d.farmerId = :farmerId
        ORDER BY c.timestamp ASC
        """
    )
    suspend fun getFarmerDairiesForWeek(weekId: Long, farmerId: Long): List<FarmerCollectionDetail>
}

// ============================================================
// DATABASE
// ============================================================

@Database(
    entities = [Farmer::class, Week::class, Collection::class, Dairy::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun farmerDao(): FarmerDao
    abstract fun weekDao(): WeekDao
    abstract fun collectionDao(): CollectionDao
    abstract fun dairyDao(): DairyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // Return the instance if it already exists
            return INSTANCE ?: synchronized(this) {
                // Create a local variable to help the compiler with null-safety
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dairy_app.db"
                ).build()

                INSTANCE = instance
                instance
            }
        }


    }
}
