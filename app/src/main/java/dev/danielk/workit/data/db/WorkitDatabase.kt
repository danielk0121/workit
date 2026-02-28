package dev.danielk.workit.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.danielk.workit.data.db.dao.ChatMessageDao
import dev.danielk.workit.data.db.dao.GrassRecordDao
import dev.danielk.workit.data.db.dao.WorkoutSessionDao
import dev.danielk.workit.data.db.entity.ChatMessage
import dev.danielk.workit.data.db.entity.GrassRecord
import dev.danielk.workit.data.db.entity.WorkoutSession

@Database(
    entities = [WorkoutSession::class, ChatMessage::class, GrassRecord::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WorkitDatabase : RoomDatabase() {

    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun grassRecordDao(): GrassRecordDao

    companion object {
        @Volatile private var INSTANCE: WorkitDatabase? = null

        fun getInstance(context: Context): WorkitDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    WorkitDatabase::class.java,
                    "workit.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
