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

    private val _isReminderEnabled = MutableLiveData<Boolean>()
    val isReminderEnabled: LiveData<Boolean> = _isReminderEnabled

    private val _monthlyGoal = MutableLiveData<Int>()
    val monthlyGoal: LiveData<Int> = _monthlyGoal

    private val _monthlyWorkoutCount = MutableLiveData<Int>()
    val monthlyWorkoutCount: LiveData<Int> = _monthlyWorkoutCount

    init {
        loadProfileData()
        _ttsStyle.value = prefs.ttsStyle
        _isDarkMode.value = prefs.isDarkMode
        _isReminderEnabled.value = prefs.isReminderEnabled
        _monthlyGoal.value = prefs.monthlyGoal
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            val current = repository.getCurrentStreak()
            val max = repository.getMaxStreak()
            val totalDays = repository.getTotalWorkoutDays()
            
            val monthPrefix = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault()).format(java.util.Date())
            val monthCount = repository.getWorkoutCountByMonth(monthPrefix)

            _currentStreak.value = current
            _maxStreak.value = max
            _monthlyWorkoutCount.value = monthCount
            
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

    fun setMonthlyGoal(goal: Int) {
        _monthlyGoal.value = goal
        prefs.monthlyGoal = goal
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.isDarkMode = enabled
        prefs.applySettings()
    }

    fun setReminderEnabled(enabled: Boolean) {
        _isReminderEnabled.value = enabled
        prefs.isReminderEnabled = enabled
        if (enabled) {
            dev.danielk.workit.service.ReminderManager.scheduleDailyReminder(getApplication())
        } else {
            dev.danielk.workit.service.ReminderManager.cancelDailyReminder(getApplication())
        }
    }
}
