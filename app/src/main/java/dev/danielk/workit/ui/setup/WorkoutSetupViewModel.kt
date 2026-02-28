package dev.danielk.workit.ui.setup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.danielk.workit.data.db.entity.WorkoutSession
import dev.danielk.workit.data.repository.WorkoutRepository
import dev.danielk.workit.model.TtsStyle
import dev.danielk.workit.model.WorkoutPreset
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutSetupViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkoutRepository.getInstance(application)

    val selectedPreset = MutableLiveData<WorkoutPreset?>(null)
    val readySeconds = MutableLiveData(30)
    val workSeconds = MutableLiveData(60)
    val restSeconds = MutableLiveData(60)
    val repeatCount = MutableLiveData(6)
    val ttsStyle = MutableLiveData(TtsStyle.FRIEND)

    val createdSessionId = MutableLiveData<Long?>()

    fun applyPreset(preset: WorkoutPreset) {
        selectedPreset.value = preset
        readySeconds.value = preset.readySeconds
        workSeconds.value = preset.workSeconds
        restSeconds.value = preset.restSeconds
        repeatCount.value = preset.repeatCount
    }

    fun createAndStartSession() {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("M월 d일 인터벌 운동", Locale.KOREAN).format(Date())
            val session = WorkoutSession(
                title = dateStr,
                date = System.currentTimeMillis(),
                readySeconds = readySeconds.value ?: 30,
                workSeconds = workSeconds.value ?: 60,
                restSeconds = restSeconds.value ?: 60,
                repeatCount = repeatCount.value ?: 6,
                ttsStyle = ttsStyle.value ?: TtsStyle.FRIEND
            )
            val id = repository.createSession(session)
            createdSessionId.postValue(id)
        }
    }
}
