package dev.danielk.workit.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.danielk.workit.R
import dev.danielk.workit.data.repository.WorkoutRepository
import dev.danielk.workit.model.Badge
import dev.danielk.workit.model.TtsStyle
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkoutRepository.getInstance(application)
    private val prefs = dev.danielk.workit.data.PreferenceManager.getInstance(application)

    private val _currentStreak = MutableLiveData<Int>()
    val currentStreak: LiveData<Int> = _currentStreak

    private val _maxStreak = MutableLiveData<Int>()
    val maxStreak: LiveData<Int> = _maxStreak

    private val _badges = MutableLiveData<List<Badge>>()
    val badges: LiveData<List<Badge>> = _badges

    private val _ttsStyle = MutableLiveData<TtsStyle>()
    val ttsStyle: LiveData<TtsStyle> = _ttsStyle

    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    init {
        loadProfileData()
        _ttsStyle.value = prefs.ttsStyle
        _isDarkMode.value = prefs.isDarkMode
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            val current = repository.getCurrentStreak()
            val max = repository.getMaxStreak()
            val totalDays = repository.getTotalWorkoutDays()

            _currentStreak.value = current
            _maxStreak.value = max
            
            _badges.value = listOf(
                Badge("streak_7", "7일 연속", "7일 동안 꾸준히 운동했어요!", R.drawable.ic_grass, max >= 7),
                Badge("first_workout", "첫 걸음", "첫 운동을 완료했어요!", R.drawable.ic_home, totalDays >= 1),
                Badge("month_workit", "한 달 워킷러", "한 달 동안 꾸준히 워킷을 사용했어요!", R.drawable.ic_grass, totalDays >= 30)
            )
        }
    }

    fun setTtsStyle(style: TtsStyle) {
        _ttsStyle.value = style
        prefs.ttsStyle = style
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.isDarkMode = enabled
        prefs.applySettings()
    }
}
