package dev.danielk.workit.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.danielk.workit.data.db.entity.GrassRecord
import dev.danielk.workit.data.db.entity.WorkoutSession
import dev.danielk.workit.data.repository.WorkoutRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkoutRepository.getInstance(application)

    val sessions = repository.getAllSessions().asLiveData()
    val grassRecords = repository.getAllGrassRecords().asLiveData()

    fun deleteSession(session: WorkoutSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
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
}
