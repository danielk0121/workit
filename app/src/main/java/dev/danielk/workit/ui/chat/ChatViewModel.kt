package dev.danielk.workit.ui.chat

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.danielk.workit.data.db.entity.ChatMessage
import dev.danielk.workit.data.db.entity.WorkoutSession
import dev.danielk.workit.data.repository.WorkoutRepository
import dev.danielk.workit.model.MessageType
import dev.danielk.workit.model.WorkoutState
import dev.danielk.workit.service.WorkoutTimerService
import dev.danielk.workit.tts.BotScript
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkoutRepository.getInstance(application)

    val messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val currentState = MutableLiveData(WorkoutState.READY)
    val timerDisplay = MutableLiveData("--:--")
    val quickReactions = MutableLiveData<List<String>>(emptyList())
    val isInputEnabled = MutableLiveData(false)
    val isWorkoutActive = MutableLiveData(false)
    val workoutComplete = MutableLiveData(false)
    val sessionTitle = MutableLiveData("")

    private var sessionId: Long = -1
    private var session: WorkoutSession? = null
    private var startTimeMillis: Long = 0L
    private val messageBuffer = mutableListOf<ChatMessage>()

    fun init(sessionId: Long, autoStart: Boolean = false) {
        this.sessionId = sessionId
        viewModelScope.launch {
            session = repository.getSessionById(sessionId)
            sessionTitle.postValue(session?.title ?: "운동")

            if (autoStart) {
                // Session is now loaded — safe to start the service
                startWorkout()
            } else {
                // Viewing historical session: load messages from DB
                repository.getMessages(sessionId).onEach { list ->
                    if (isWorkoutActive.value == false) {
                        messages.postValue(list)
                        currentState.postValue(WorkoutState.DONE)
                        quickReactions.postValue(BotScript.getQuickReactions(WorkoutState.DONE))
                        isInputEnabled.postValue(true)
                    }
                }.launchIn(viewModelScope)
            }
        }

        // Observe timer state for display
        WorkoutTimerService.timerState.onEach { state ->
            if (state != null) {
                val mins = state.remainingSeconds / 60
                val secs = state.remainingSeconds % 60
                timerDisplay.postValue("%02d:%02d".format(mins, secs))
                currentState.postValue(state.workoutState)
                quickReactions.postValue(BotScript.getQuickReactions(state.workoutState))
                isInputEnabled.postValue(
                    state.workoutState == WorkoutState.REST ||
                    state.workoutState == WorkoutState.DONE
                )
            }
        }.launchIn(viewModelScope)

        // Observe timer events for bot messages
        WorkoutTimerService.timerEvents.onEach { event ->
            when (event) {
                is WorkoutTimerService.TimerEvent.BotMessage -> {
                    addMessage(ChatMessage(
                        sessionId = sessionId,
                        type = MessageType.BOT,
                        content = event.chatText,
                        workoutState = event.state
                    ))
                }
                is WorkoutTimerService.TimerEvent.WorkoutComplete -> {
                    onWorkoutComplete()
                }
                is WorkoutTimerService.TimerEvent.StreakMessage -> {
                    addMessage(ChatMessage(
                        sessionId = sessionId,
                        type = MessageType.BOT,
                        content = event.text,
                        workoutState = WorkoutState.DONE
                    ))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateSessionTitle(newTitle: String) {
        if (newTitle.isBlank()) return
        sessionTitle.postValue(newTitle)
        viewModelScope.launch {
            repository.updateSessionTitle(sessionId, newTitle)
        }
    }

    fun startWorkout() {
        isWorkoutActive.value = true
        startTimeMillis = System.currentTimeMillis()
        session?.let { s ->
            val intent = Intent(getApplication(), WorkoutTimerService::class.java).apply {
                action = WorkoutTimerService.ACTION_START
                putExtra(WorkoutTimerService.EXTRA_SESSION_ID, sessionId)
                putExtra(WorkoutTimerService.EXTRA_READY_SECONDS, s.readySeconds)
                putExtra(WorkoutTimerService.EXTRA_WORK_SECONDS, s.workSeconds)
                putExtra(WorkoutTimerService.EXTRA_REST_SECONDS, s.restSeconds)
                putExtra(WorkoutTimerService.EXTRA_REPEAT_COUNT, s.repeatCount)
                putExtra(WorkoutTimerService.EXTRA_TTS_STYLE, s.ttsStyle.name)
            }
            getApplication<Application>().startForegroundService(intent)
        }
    }

    val isTimerPaused = WorkoutTimerService.isPaused

    fun pauseWorkout() {
        val intent = Intent(getApplication(), WorkoutTimerService::class.java).apply {
            action = WorkoutTimerService.ACTION_PAUSE
        }
        getApplication<Application>().startService(intent)
    }

    fun resumeWorkout() {
        val intent = Intent(getApplication(), WorkoutTimerService::class.java).apply {
            action = WorkoutTimerService.ACTION_RESUME
        }
        getApplication<Application>().startService(intent)
    }

    fun resetWorkout() {
        val intent = Intent(getApplication(), WorkoutTimerService::class.java).apply {
            action = WorkoutTimerService.ACTION_RESET
        }
        getApplication<Application>().startService(intent)
    }

    fun stopWorkout() {
        val intent = Intent(getApplication(), WorkoutTimerService::class.java).apply {
            action = WorkoutTimerService.ACTION_STOP
        }
        getApplication<Application>().startService(intent)
        isWorkoutActive.value = false
        // Save partial session
        viewModelScope.launch {
            val elapsed = ((System.currentTimeMillis() - startTimeMillis) / 1000).toInt()
            repository.completeSession(sessionId, elapsed)
            repository.updateGrassAfterWorkout(sessionId, false)
            saveBufferedMessages()
        }
    }

    fun sendQuickReaction(reaction: String) {
        val state = currentState.value ?: WorkoutState.READY
        addMessage(ChatMessage(
            sessionId = sessionId,
            type = MessageType.USER_QUICK,
            content = reaction,
            workoutState = state
        ))
    }

    fun sendTextMessage(text: String) {
        val state = currentState.value ?: WorkoutState.READY
        addMessage(ChatMessage(
            sessionId = sessionId,
            type = MessageType.USER_TEXT,
            content = text,
            workoutState = state
        ))
    }

    private fun addMessage(message: ChatMessage) {
        val current = messages.value.orEmpty().toMutableList()
        current.add(message)
        messages.value = current
        messageBuffer.add(message)
        viewModelScope.launch {
            repository.addMessage(message)
        }
    }

    private fun onWorkoutComplete() {
        isWorkoutActive.postValue(false)
        workoutComplete.postValue(true)
        currentState.postValue(WorkoutState.DONE)
        quickReactions.postValue(BotScript.getQuickReactions(WorkoutState.DONE))
        isInputEnabled.postValue(true)

        viewModelScope.launch {
            val elapsed = ((System.currentTimeMillis() - startTimeMillis) / 1000).toInt()
            repository.completeSession(sessionId, elapsed)
            repository.updateGrassAfterWorkout(sessionId, true)

            // --- Workout Summary Logic ---
            val currentMessages = messages.value.orEmpty()
            val tiredCount = currentMessages.count { it.content == "😤 힘들어" }
            val cheerCount = currentMessages.count { it.content == "💪 괜찮아" }

            if (tiredCount > 0) {
                val summaryText = if (cheerCount > 0) {
                    "오늘 '힘들어'를 ${tiredCount}번이나 누르셨지만, '괜찮아'라고 스스로 다독이며 완주하셨네요! 정말 멋져요! 👍"
                } else {
                    "오늘 '힘들어'를 ${tiredCount}번이나 누르셨는데도 끝까지 완주하셨네요! 대단한 정신력이에요! 💪"
                }
                addMessage(ChatMessage(
                    sessionId = sessionId,
                    type = MessageType.BOT,
                    content = summaryText,
                    workoutState = WorkoutState.DONE
                ))
            }
            // -----------------------------

            // Check streak for special message
            val streak = repository.getCurrentStreak()
            if (streak >= 3) {
                val streakText = BotScript.getStreakMessage(streak)
                addMessage(ChatMessage(
                    sessionId = sessionId,
                    type = MessageType.BOT,
                    content = streakText,
                    workoutState = WorkoutState.DONE
                ))
            }
        }
    }

    private suspend fun saveBufferedMessages() {
        // Messages are saved individually on addMessage; buffer is for reference only
    }

    override fun onCleared() {
        super.onCleared()
        if (isWorkoutActive.value == true) {
            stopWorkout()
        }
    }
}
