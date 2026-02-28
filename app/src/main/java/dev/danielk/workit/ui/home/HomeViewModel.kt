package dev.danielk.workit.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.danielk.workit.data.db.entity.GrassRecord
import dev.danielk.workit.data.db.entity.WorkoutSession
import dev.danielk.workit.data.repository.WorkoutRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkoutRepository.getInstance(application)

    // 정렬: true = 내림차순(최신순), false = 오름차순(과거순)
    val sortDescending = MutableStateFlow(true)
    // 필터: null = 전체, 그 외 "yyyy-MM" 형태의 월 문자열
    val filterMonth = MutableStateFlow<String?>(null)

    val grassRecords = repository.getAllGrassRecords().asLiveData()

    val filteredSessions = combine(
        repository.getAllSessions(),
        filterMonth,
        sortDescending
    ) { list, month, desc ->
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val filtered = if (month == null) list
        else list.filter { sdf.format(Date(it.date)) == month }

        if (desc) filtered.sortedByDescending { it.date }
        else filtered.sortedBy { it.date }
    }.asLiveData()

    val allSessions = repository.getAllSessions().asLiveData()

    fun deleteSession(session: WorkoutSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }

    /** 현재 세션 목록에서 월 목록 반환 (중복 없음, 최신순) */
    fun getAvailableMonths(): List<String> {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return (allSessions.value ?: emptyList())
            .map { sdf.format(Date(it.date)) }
            .distinct()
            .sortedDescending()
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
