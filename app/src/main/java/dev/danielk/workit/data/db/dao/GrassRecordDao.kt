package dev.danielk.workit.data.db.dao

import androidx.room.*
import dev.danielk.workit.data.db.entity.GrassRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface GrassRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: GrassRecord)

    @Query("SELECT * FROM grass_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<GrassRecord>>

    @Query("SELECT * FROM grass_records WHERE date >= :fromDate ORDER BY date ASC")
    fun getRecordsSince(fromDate: String): Flow<List<GrassRecord>>

    @Query("SELECT * FROM grass_records WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): GrassRecord?

    @Query("SELECT MAX(streakCount) FROM grass_records")
    suspend fun getMaxStreak(): Int?

    @Query("SELECT COUNT(*) FROM grass_records WHERE date LIKE :monthPrefix || '%' AND grade != 'NONE'")
    suspend fun getWorkoutCountByMonth(monthPrefix: String): Int

    @Query("SELECT COUNT(*) FROM grass_records WHERE grade != 'NONE'")
    suspend fun getTotalWorkoutDays(): Int
}
