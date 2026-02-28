package dev.danielk.workit.ui.grass

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.danielk.workit.data.db.entity.GrassRecord
import dev.danielk.workit.data.repository.WorkoutRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GrassViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkoutRepository.getInstance(application)

    val grassRecords = repository.getAllGrassRecords().asLiveData()
    val totalWorkoutDays = MutableLiveData(0)
    val maxStreak = MutableLiveData(0)
    val currentStreak = MutableLiveData(0)

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            totalWorkoutDays.postValue(repository.getTotalWorkoutDays())
            maxStreak.postValue(repository.getMaxStreak())
        }
    }

    fun getCurrentStreak(records: List<GrassRecord>): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        var streak = 0
        val recordMap = records.associateBy { it.date }
        while (true) {
            val dateStr = sdf.format(cal.time)
            val record = recordMap[dateStr]
            if (record != null && record.grade.name != "NONE") {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    fun getOneYearAgoDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }
}
