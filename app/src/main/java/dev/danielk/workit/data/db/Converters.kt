package dev.danielk.workit.data.db

import androidx.room.TypeConverter
import dev.danielk.workit.model.GrassGrade
import dev.danielk.workit.model.MessageType
import dev.danielk.workit.model.TtsStyle
import dev.danielk.workit.model.WorkoutState

class Converters {
    @TypeConverter fun ttsStyleToString(v: TtsStyle): String = v.name
    @TypeConverter fun stringToTtsStyle(v: String): TtsStyle = TtsStyle.valueOf(v)

    @TypeConverter fun workoutStateToString(v: WorkoutState): String = v.name
    @TypeConverter fun stringToWorkoutState(v: String): WorkoutState = WorkoutState.valueOf(v)

    @TypeConverter fun messageTypeToString(v: MessageType): String = v.name
    @TypeConverter fun stringToMessageType(v: String): MessageType = MessageType.valueOf(v)

    @TypeConverter fun grassGradeToString(v: GrassGrade): String = v.name
    @TypeConverter fun stringToGrassGrade(v: String): GrassGrade = GrassGrade.valueOf(v)
}
