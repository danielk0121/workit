package dev.danielk.workit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dev.danielk.workit.data.PreferenceManager
import dev.danielk.workit.data.db.entity.ChatMessage
import dev.danielk.workit.data.db.entity.GrassRecord
import dev.danielk.workit.data.db.entity.WorkoutSession
import dev.danielk.workit.data.repository.WorkoutRepository
import dev.danielk.workit.model.GrassGrade
import dev.danielk.workit.model.MessageType
import dev.danielk.workit.model.TtsStyle
import dev.danielk.workit.model.WorkoutState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WorkitApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        PreferenceManager.getInstance(this).applySettings()
        insertSampleDataIfNeeded()
    }

    private fun insertSampleDataIfNeeded() {
        val prefs = PreferenceManager.getInstance(this)
        if (prefs.isSampleDataInserted) return

        CoroutineScope(Dispatchers.IO).launch {
            val repo = WorkoutRepository.getInstance(this@WorkitApplication)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()

            val presets = listOf(
                Triple(60, 60, 6),   // 입문자
                Triple(120, 60, 6),  // 초급자
                Triple(180, 60, 8)   // 중급자
            )
            val names = listOf("인터벌 런닝", "HIIT 운동", "줄넘기", "자전거", "수영")
            val styles = listOf(TtsStyle.COACH, TtsStyle.FRIEND, TtsStyle.INFO)
            val quickMsgs = listOf("😤 힘들어", "💪 괜찮아", "🎵 신난다", "😮‍💨 숨차", "💧 물마심")

            // 15일치 샘플 데이터 (오늘부터 15일 전까지)
            repeat(15) { i ->
                val dayCal = cal.clone() as Calendar
                dayCal.add(Calendar.DAY_OF_YEAR, -(i * 2 + 1)) // 격일 데이터
                dayCal.set(Calendar.HOUR_OF_DAY, (7..9).random())
                dayCal.set(Calendar.MINUTE, listOf(0, 15, 30).random())
                dayCal.set(Calendar.SECOND, 0)
                dayCal.set(Calendar.MILLISECOND, 0)

                val preset = presets[i % presets.size]
                val name = names[i % names.size]
                val dateLabel = SimpleDateFormat("M월 d일", Locale.KOREAN).format(dayCal.time)
                val title = "$dateLabel $name"
                val duration = (preset.first + preset.second) * preset.third + 30

                val session = WorkoutSession(
                    title = title,
                    date = dayCal.timeInMillis,
                    readySeconds = 30,
                    workSeconds = preset.first,
                    restSeconds = preset.second,
                    repeatCount = preset.third,
                    ttsStyle = styles[i % styles.size],
                    isCompleted = i % 5 != 3, // 일부는 중단
                    totalDurationSeconds = duration
                )
                val sessionId = repo.createSession(session)
                repo.addMessage(ChatMessage(sessionId = sessionId, type = MessageType.BOT,
                    content = "오늘 운동 시작할게요! 준비 운동 하면서 기다려주세요 🙆", workoutState = WorkoutState.READY))
                repeat((2..4).random()) {
                    repo.addMessage(ChatMessage(sessionId = sessionId, type = MessageType.USER_QUICK,
                        content = quickMsgs.random(), workoutState = WorkoutState.RUNNING))
                }
                repo.addMessage(ChatMessage(sessionId = sessionId, type = MessageType.BOT,
                    content = "🎉 오늘 운동 완료! 수고했어요 💪", workoutState = WorkoutState.DONE))

                val dateStr = sdf.format(dayCal.time)
                val grade = if (session.isCompleted) GrassGrade.COMPLETE else GrassGrade.PARTIAL
                repo.upsertGrassRecord(GrassRecord(date = dateStr, sessionId = sessionId,
                    grade = grade, streakCount = 1))
            }

            prefs.isSampleDataInserted = true
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "운동 타이머",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "운동 진행 중 표시되는 알림입니다"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "workout_timer_channel"
    }
}
