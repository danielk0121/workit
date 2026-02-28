package dev.danielk.workit.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.danielk.workit.data.db.entity.WorkoutSession
import dev.danielk.workit.data.repository.WorkoutRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkoutRepository.getInstance(application)

    val sessions = repository.getAllSessions().asLiveData()

    fun deleteSession(session: WorkoutSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }
}
