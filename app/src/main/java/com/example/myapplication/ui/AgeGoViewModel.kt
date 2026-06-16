package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AgeGoRepository
import com.example.myapplication.data.Announcement
import com.example.myapplication.data.RepositoryProvider
import com.example.myapplication.data.Student
import com.example.myapplication.data.Workout
import com.example.myapplication.data.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AgeGoUiState(
    val isLoading: Boolean = true,
    val students: List<Student> = emptyList(),
    val workouts: List<Workout> = emptyList(),
    val announcements: List<Announcement> = emptyList(),
    val events: List<Event> = emptyList(),
    val isDemo: Boolean = false,
    val message: String? = null
)

class AgeGoViewModel(
    private val repository: AgeGoRepository = RepositoryProvider.create()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AgeGoUiState())
    val uiState: StateFlow<AgeGoUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val data = repository.loadDashboard()
            _uiState.value = AgeGoUiState(
                students = data.students,
                workouts = data.workouts,
                announcements = data.announcements,
                events = data.events,
                isDemo = data.isDemo,
                message = data.message
            )
        }
    }
}
