package com.example.coroutinedemo.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

class TaskRepository {

    suspend fun getTasks(shouldFail: Boolean = false): List<Task> =
        withContext(Dispatchers.IO) {
            delay(2000L)
            if (shouldFail) {
                throw IOException("Server is not reachable")
            }
            listOf(
                Task(1, "Wortel Segar", "Sayur & Buah", "1 kg", 12000, true),
                Task(2, "Bayam Hijau", "Sayur & Buah", "2 ikat", 5000, false),
                Task(3, "Minyak Goreng", "Bahan Dapur", "2 liter", 34000, false),
                Task(4, "Beras Premium", "Bahan Dapur", "5 kg", 68000, false),
                Task(5, "Susu UHT Kotak", "Minuman", "1 liter", 18000, false),
                Task(6, "Telur Ayam", "Bahan Dapur", "1 kg", 28000, false)
            )
        }

    suspend fun getAnnouncement(): String =
        withContext(Dispatchers.IO) {
            delay(1000L)
            "🎉 Promo Spesial: Diskon 10% untuk semua produk kategori Sayur & Buah!"
        }

    suspend fun getBudgetLimit(): Int =
        withContext(Dispatchers.IO) {
            delay(1500L)
            150000
        }

    suspend fun toggleTask(taskId: Int): List<Task> =
        withContext(Dispatchers.IO) {
            delay(300L)
            emptyList()
        }
}
