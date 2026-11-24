package com.example.coroutinedemo.data

data class Task(
    val id: Int,
    val title: String,
    val category: String,
    val quantity: String,
    val price: Int,
    val isCompleted: Boolean = false
)

typealias ShoppingItem = Task
