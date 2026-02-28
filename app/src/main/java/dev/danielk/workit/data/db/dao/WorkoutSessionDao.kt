package dev.danielk.workit.data.db.dao

import androidx.room.*
import dev.danielk.workit.data.db.entity.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {

    @Insert
    suspend fun insert(session: WorkoutSession): Long

    @Update
    suspend fun update(session: WorkoutSession)

    @Delete
    suspend fun delete(session: WorkoutSession)

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): WorkoutSession?

    @Query("SELECT * FROM workout_sessions WHERE date >= :fromDate ORDER BY date DESC")
    fun getSessionsSince(fromDate: Long): Flow<List<WorkoutSession>>

    @Query("UPDATE workout_sessions SET isCompleted = :completed, totalDurationSeconds = :duration WHERE id = :id")
    suspend fun updateCompletion(id: Long, completed: Boolean, duration: Int)
}
