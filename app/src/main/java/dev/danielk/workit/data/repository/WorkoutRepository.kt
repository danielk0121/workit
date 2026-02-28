package dev.danielk.workit.data.repository

import android.content.Context
import dev.danielk.workit.data.db.WorkitDatabase
import dev.danielk.workit.data.db.entity.ChatMessage
import dev.danielk.workit.data.db.entity.GrassRecord
import dev.danielk.workit.data.db.entity.WorkoutSession
import dev.danielk.workit.model.GrassGrade
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutRepository(context: Context) {

    private val db = WorkitDatabase.getInstance(context)
    private val sessionDao = db.workoutSessionDao()
    private val messageDao = db.chatMessageDao()
    private val grassDao = db.grassRecordDao()

    // --- Session ---
    suspend fun createSession(session: WorkoutSession): Long = sessionDao.insert(session)

    fun getAllSessions(): Flow<List<WorkoutSession>> = sessionDao.getAllSessions()

    suspend fun getSessionById(id: Long): WorkoutSession? = sessionDao.getSessionById(id)

    suspend fun completeSession(id: Long, durationSeconds: Int) {
        sessionDao.updateCompletion(id, true, durationSeconds)
    }

    suspend fun updateSessionTitle(id: Long, title: String) {
        sessionDao.updateTitle(id, title)
    }

    suspend fun deleteSession(session: WorkoutSession) {
        sessionDao.delete(session)
    }

    // --- Messages ---
    suspend fun addMessage(message: ChatMessage): Long = messageDao.insert(message)

    fun getMessages(sessionId: Long): Flow<List<ChatMessage>> =
        messageDao.getMessagesBySession(sessionId)

    // --- Grass ---
    suspend fun upsertGrassRecord(record: GrassRecord) = grassDao.upsert(record)

    fun getAllGrassRecords(): Flow<List<GrassRecord>> = grassDao.getAllRecords()

    fun getGrassRecordsSince(fromDate: String): Flow<List<GrassRecord>> =
        grassDao.getRecordsSince(fromDate)

    suspend fun getMaxStreak(): Int = grassDao.getMaxStreak() ?: 0

    suspend fun getTotalWorkoutDays(): Int = grassDao.getTotalWorkoutDays()

    suspend fun getCurrentStreak(): Int {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return calculateCurrentStreak(today)
    }

    suspend fun updateGrassAfterWorkout(sessionId: Long, isCompleted: Boolean) {
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentStreak = calculateCurrentStreak(dateStr)
        val currentSession = sessionDao.getSessionById(sessionId)
        
        val maxDuration = sessionDao.getMaxDuration() ?: 0
        val maxRepeat = sessionDao.getMaxRepeatCount() ?: 0
        
        val isBest = isCompleted && currentSession != null && 
                (currentSession.totalDurationSeconds >= maxDuration || currentSession.repeatCount >= maxRepeat)

        val grade = when {
            !isCompleted -> GrassGrade.PARTIAL
            isBest -> GrassGrade.BEST
            else -> GrassGrade.COMPLETE
        }
        grassDao.upsert(
            GrassRecord(
                date = dateStr,
                sessionId = sessionId,
                grade = grade,
                streakCount = currentStreak
            )
        )
    }

    private suspend fun calculateCurrentStreak(today: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var streak = 0
        val cal = java.util.Calendar.getInstance()
        cal.time = sdf.parse(today) ?: Date()
        while (true) {
            val dateStr = sdf.format(cal.time)
            val record = grassDao.getByDate(dateStr)
            if (record != null && record.grade != GrassGrade.NONE) {
                streak++
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            } else if (dateStr == today) {
                // Today not yet counted — count it
                streak++
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    companion object {
        @Volatile private var INSTANCE: WorkoutRepository? = null

        fun getInstance(context: Context): WorkoutRepository {
            return INSTANCE ?: synchronized(this) {
                WorkoutRepository(context).also { INSTANCE = it }
            }
        }
    }
}
