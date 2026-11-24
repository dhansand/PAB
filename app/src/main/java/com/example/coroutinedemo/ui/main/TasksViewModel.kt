package com.example.coroutinedemo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coroutinedemo.data.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.system.measureTimeMillis

class TasksViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadData()
    }

    fun loadData(shouldFail: Boolean = false) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null, loadDurationMs = null)
            }
            try {
                var loadedTasks = emptyList<com.example.coroutinedemo.data.Task>()
                var loadedAnnouncement = ""
                var loadedBudgetLimit = 0

                val duration = measureTimeMillis {
                    coroutineScope {
                        val tasksDeferred = async { repository.getTasks(shouldFail) }
                        val announcementDeferred = async { repository.getAnnouncement() }
                        val budgetLimitDeferred = async { repository.getBudgetLimit() }

                        loadedTasks = tasksDeferred.await()
                        loadedAnnouncement = announcementDeferred.await()
                        loadedBudgetLimit = budgetLimitDeferred.await()
                    }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        tasks = loadedTasks,
                        announcement = loadedAnnouncement,
                        budgetLimit = loadedBudgetLimit,
                        isSortedAsc = false,
                        loadDurationMs = duration
                    )
                }
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat data: ${e.message}"
                    )
                }
            }
        }
    }

    fun cancelLoading() {
        loadJob?.cancel()
        _uiState.update {
            it.copy(isLoading = false, errorMessage = "Pemuatan dibatalkan")
        }
    }

    fun onTap() {
        _uiState.update { it.copy(tapCount = it.tapCount + 1) }
    }

    fun blockMainThread() {
        Thread.sleep(3000L)
    }

    fun sortTasksByTitle() {
        val currentTasks = _uiState.value.tasks
        if (currentTasks.isEmpty()) return

        viewModelScope.launch {
            val nextSortOrder = !_uiState.value.isSortedAsc
            val sortedList = withContext(Dispatchers.Default) {
                if (nextSortOrder) {
                    currentTasks.sortedBy { it.title.lowercase() }
                } else {
                    currentTasks.sortedByDescending { it.title.lowercase() }
                }
            }
            _uiState.update {
                it.copy(
                    tasks = sortedList,
                    isSortedAsc = nextSortOrder
                )
            }
        }
    }

    fun toggleTaskCompletion(taskId: Int) {
        _uiState.update { state ->
            val updatedTasks = state.tasks.map { task ->
                if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
            }
            state.copy(tasks = updatedTasks)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
