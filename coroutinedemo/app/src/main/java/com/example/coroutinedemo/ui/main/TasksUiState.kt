package com.example.coroutinedemo.ui.main

import com.example.coroutinedemo.data.Task

data class TasksUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val announcement: String = "",
    val budgetLimit: Int? = null,
    val errorMessage: String? = null,
    val tapCount: Int = 0,
    val isSortedAsc: Boolean = false,
    val searchQuery: String = "",
    val loadDurationMs: Long? = null
) {
    val totalCheckedPrice: Int
        get() = tasks.filter { it.isCompleted }.sumOf { it.price }

    val remainingBudget: Int?
        get() = budgetLimit?.let { it - totalCheckedPrice }

    val filteredTasks: List<Task>
        get() = if (searchQuery.isBlank()) {
            tasks
        } else {
            tasks.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }
        }
}
