package dev.danielk.workit.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import dev.danielk.workit.MainActivity
import dev.danielk.workit.R
import dev.danielk.workit.WorkitApplication
import dev.danielk.workit.model.TtsStyle
import dev.danielk.workit.model.WorkoutState
import dev.danielk.workit.tts.BotScript
import dev.danielk.workit.tts.TtsManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WorkoutTimerService : Service() {

    // --- Shared state (observed by ChatViewModel) ---
    companion object {
        private val _timerState = MutableStateFlow<TimerState?>(null)
        val timerState = _timerState.asStateFlow()

        val timerEvents = MutableSharedFlow<TimerEvent>(extraBufferCapacity = 16)

        const val ACTION_START = "dev.danielk.workit.START"
        const val ACTION_STOP = "dev.danielk.workit.STOP"

        const val EXTRA_SESSION_ID = "session_id"
        const val EXTRA_READY_SECONDS = "ready_seconds"
        const val EXTRA_WORK_SECONDS = "work_seconds"
        const val EXTRA_REST_SECONDS = "rest_seconds"
        const val EXTRA_REPEAT_COUNT = "repeat_count"
        const val EXTRA_TTS_STYLE = "tts_style"

        const val NOTIFICATION_ID = 1001
    }

    data class TimerState(
        val workoutState: WorkoutState,
        val remainingSeconds: Int,
        val currentRound: Int,
        val totalRounds: Int,
        val elapsedSeconds: Int
    )

    sealed class TimerEvent {
        data class BotMessage(val chatText: String, val ttsText: String, val state: WorkoutState) : TimerEvent()
        object WorkoutComplete : TimerEvent()
        data class StreakMessage(val text: String) : TimerEvent()
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var timerJob: Job? = null
    private lateinit var ttsManager: TtsManager
    private lateinit var vibrator: Vibrator

    private var sessionId: Long = -1
    private var readySeconds: Int = 30
    private var workSeconds: Int = 60
    private var restSeconds: Int = 60
    private var repeatCount: Int = 6
    private var ttsStyle: TtsStyle = TtsStyle.INFO
    private var elapsedSeconds: Int = 0

    override fun onCreate() {
        super.onCreate()
        ttsManager = TtsManager(this)
        vibrator = getSystemService(Vibrator::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopWorkout()
            ACTION_START -> {
                sessionId = intent.getLongExtra(EXTRA_SESSION_ID, -1)
                readySeconds = intent.getIntExtra(EXTRA_READY_SECONDS, 30)
                workSeconds = intent.getIntExtra(EXTRA_WORK_SECONDS, 60)
                restSeconds = intent.getIntExtra(EXTRA_REST_SECONDS, 60)
                repeatCount = intent.getIntExtra(EXTRA_REPEAT_COUNT, 6)
                ttsStyle = TtsStyle.valueOf(
                    intent.getStringExtra(EXTRA_TTS_STYLE) ?: TtsStyle.INFO.name
                )
                startForegroundCompat(buildNotification("운동 준비 중..."))
                startTimer()
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            elapsedSeconds = 0
            // READY phase
            runPhase(WorkoutState.READY, readySeconds, onStart = {
                val (chat, tts) = BotScript.getReadyMessage(ttsStyle, readySeconds)
                emitBotMessage(chat, tts, WorkoutState.READY)
            })

            // RUNNING / REST cycles
            for (round in 1..repeatCount) {
                // RUNNING
                runPhase(WorkoutState.RUNNING, workSeconds, onStart = {
                    val (chat, tts) = BotScript.getRunningMessage(ttsStyle, round, repeatCount, workSeconds)
                    emitBotMessage(chat, tts, WorkoutState.RUNNING)
                    vibrate(longArrayOf(0, 300, 100, 300))
                })

                // REST (skip after last round)
                if (round < repeatCount) {
                    runPhase(WorkoutState.REST, restSeconds, onStart = {
                        val (chat, tts) = BotScript.getRestMessage(ttsStyle, round, repeatCount, restSeconds)
                        emitBotMessage(chat, tts, WorkoutState.REST)
                        vibrate(longArrayOf(0, 200))
                    })
                }
            }

            // DONE
            _timerState.value = TimerState(WorkoutState.DONE, 0, repeatCount, repeatCount, elapsedSeconds)
            WearableManager.sendWorkoutStatus(this@WorkoutTimerService, WorkoutState.DONE, 0, repeatCount, repeatCount)
            val (chat, tts) = BotScript.getDoneMessage(ttsStyle, repeatCount, elapsedSeconds)
            emitBotMessage(chat, tts, WorkoutState.DONE)
            ttsManager.speak(tts)
            vibrate(longArrayOf(0, 300, 100, 300, 100, 500))
            timerEvents.emit(TimerEvent.WorkoutComplete)
            updateNotification("🎉 운동 완료!")
        }
    }

    private suspend fun runPhase(
        state: WorkoutState,
        totalSeconds: Int,
        onStart: suspend () -> Unit
    ) {
        onStart()
        updateNotification(stateLabel(state))

        for (remaining in totalSeconds downTo 0) {
            val round = if (state == WorkoutState.RUNNING || state == WorkoutState.REST) currentRoundFromElapsed() else 1
            _timerState.value = TimerState(
                workoutState = state,
                remainingSeconds = remaining,
                currentRound = round,
                totalRounds = repeatCount,
                elapsedSeconds = elapsedSeconds
            )
            
            // Sync with Wear OS
            WearableManager.sendWorkoutStatus(
                this@WorkoutTimerService,
                state,
                remaining,
                round,
                repeatCount
            )

            if (remaining == 10 && state != WorkoutState.DONE) {
                val (chat, tts) = BotScript.getCountdownWarningMessage(ttsStyle, 10)
                emitBotMessage(chat, tts, state)
                ttsManager.speak(tts)
            }
            if (remaining == 0) break
            delay(1000)
            elapsedSeconds++
        }
    }

    private fun currentRoundFromElapsed(): Int {
        val cycleSeconds = workSeconds + restSeconds
        if (cycleSeconds == 0) return 1
        val afterReady = (elapsedSeconds - readySeconds).coerceAtLeast(0)
        return (afterReady / cycleSeconds) + 1
    }

    private suspend fun emitBotMessage(chat: String, tts: String, state: WorkoutState) {
        timerEvents.emit(TimerEvent.BotMessage(chat, tts, state))
    }

    private fun stopWorkout() {
        timerJob?.cancel()
        _timerState.value = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun vibrate(pattern: LongArray) {
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
    }

    private fun stateLabel(state: WorkoutState) = when (state) {
        WorkoutState.READY -> "준비 중..."
        WorkoutState.RUNNING -> "🏃 운동 중!"
        WorkoutState.REST -> "😮‍💨 휴식 중"
        WorkoutState.DONE -> "🎉 완료!"
    }

    private fun buildNotification(contentText: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 0,
            Intent(this, WorkoutTimerService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, WorkitApplication.CHANNEL_ID)
            .setContentTitle("Workit 운동 중")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_notification, "중지", stopIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(text))
    }

    private fun startForegroundCompat(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH else 0
            startForeground(NOTIFICATION_ID, notification, type)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        serviceScope.cancel()
        ttsManager.shutdown()
        _timerState.value = null
    }
}
