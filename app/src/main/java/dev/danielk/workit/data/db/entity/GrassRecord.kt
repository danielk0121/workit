package dev.danielk.workit.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.danielk.workit.model.GrassGrade

@Entity(tableName = "grass_records")
data class GrassRecord(
    @PrimaryKey val date: String, // "yyyy-MM-dd"
    val sessionId: Long,
    val grade: GrassGrade,
    val streakCount: Int
)
