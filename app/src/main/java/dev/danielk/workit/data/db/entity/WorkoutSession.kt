package dev.danielk.workit.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.danielk.workit.model.TtsStyle

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: Long,
    val readySeconds: Int,
    val workSeconds: Int,
    val restSeconds: Int,
    val repeatCount: Int,
    val ttsStyle: TtsStyle,
    val isCompleted: Boolean = false,
    val totalDurationSeconds: Int = 0
)
