package dev.danielk.workit.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.danielk.workit.model.MessageType
import dev.danielk.workit.model.WorkoutState

@Entity(
    tableName = "chat_messages",
    foreignKeys = [ForeignKey(
        entity = WorkoutSession::class,
        parentColumns = ["id"],
        childColumns = ["sessionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("sessionId")]
)
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val type: MessageType,
    val content: String,
    val workoutState: WorkoutState,
    val timestamp: Long = System.currentTimeMillis()
)
