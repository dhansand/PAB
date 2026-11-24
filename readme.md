# Coroutine Demo - Smart Shopping List (MVVM & Kotlin Coroutines)

Aplikasi Android berbasis **Jetpack Compose** dengan arsitektur **MVVM (Model-View-ViewModel)** yang mengimplementasikan pemrograman asinkron menggunakan **Kotlin Coroutines**, mencakup seluruh materi **Modul M17** serta penyelesaian **Latihan 9**.

---

## 1. Penjelasan Fungsi Tombol pada Aplikasi

Setiap tombol pada antarmuka utama dirancang untuk mendemonstrasikan konsep spesifik dari Kotlin Coroutines dan manajemen *thread* di Android:

| Tombol | Fungsi & Perilaku | Konsep Coroutine yang Didemonstrasikan |
| :--- | :--- | :--- |
| **Load** | Memuat daftar barang belanjaan ($2000\text{ ms}$), banner promo ($1000\text{ ms}$), dan batas anggaran ($1500\text{ ms}$) secara bersamaan. | **`launch`**, **`async`**, **`coroutineScope`**, dan **`withContext(Dispatchers.IO)`** (eksekusi konkuren & *main-safe*). |
| **Load (fail)** | Mensimulasikan kegagalan koneksi server (`IOException`) di tengah proses pemuatan data dan memunculkan tombol **Retry**. | **Structured Concurrency** & penanganan error **`try/catch`** melalui UI State (`StateFlow`). |
| **Cancel** | Menghentikan proses pemuatan data yang sedang berjalan. Tombol ini hanya aktif saat status `isLoading = true`. | Pembatalan kooperatif menggunakan **`Job.cancel()`** tanpa memicu error palsu. |
| **Tap: X** | Tombol penghitung (*counter*) interaktif yang dapat ditekan kapan saja. | Membuktikan bahwa **`suspend` function** tidak memblokir *main thread* (UI tetap responsif saat pemuatan berlangsung). |
| **Block UI** | Menjalankan `Thread.sleep(3000L)` secara langsung di *main thread* sehingga layar membeku (*freeze*) selama $3$ detik. | Contoh buruk (*anti-pattern*) pemblokiran *main thread* sebagai pembanding dengan mekanisme `delay()`. |
| **Sort (A-Z / Z-A)** | Mengurutkan daftar item berdasarkan nama secara *ascending* maupun *descending*. | Pemindahan komputasi CPU ke *background thread* menggunakan **`withContext(Dispatchers.Default)`**. |

---

## 2. Arsitektur Aplikasi (MVVM)

Aplikasi memisahkan tanggung jawab menjadi tiga lapisan utama:
1. **Data Layer (`Task.kt` & `TaskRepository.kt`):** Menyediakan model data dan fungsi `suspend` yang *main-safe* dengan memindahkan simulasi jaringan ke `Dispatchers.IO`.
2. **Domain / State Management (`TasksUiState.kt` & `TasksViewModel.kt`):** Mengelola `StateFlow`, menjalankan tugas paralel dalam `viewModelScope`, menghitung total belanja & sisa anggaran, serta menangani pembatalan `Job` dan pengecualian `IOException`.
3. **UI Layer (`TasksScreen.kt`):** Composable *stateless* yang mengamati perubahan state menggunakan `collectAsStateWithLifecycle()` tanpa menjalankan logika coroutine data secara langsung.

---

## 3. Pembahasan Jawaban Latihan 9

Berikut adalah analisis dan jawaban untuk setiap soal pada **Bagian 9 (Latihan)** di modul:

### 1. Menambah Permintaan Ketiga (`delay(1500L)`)
* **Apakah total waktu pemuatan masih sekitar 2 detik?**
  **Ya**, total waktu pemuatan tetap berada di kisaran **$2$ detik** ($\approx 2000\text{ ms}$), bukan penjumlahan seluruh durasi ($2 + 1 + 1,5 = 4,5$ detik).
* **Alasan:**
  Ketiga fungsi dijalankan secara **konkuren (bersamaan)** menggunakan blok `async` di dalam `coroutineScope`. Waktu total eksekusi konkuren ditentukan oleh tugas dengan durasi paling lama ($2000\text{ ms}$), sementara dua tugas lainnya ($1000\text{ ms}$ dan $1500\text{ ms}$) selesai lebih awal secara paralel di latar belakang.

### 2. Tombol Retry
* Tombol **Retry** ditempatkan di sebelah teks pesan error di dalam blok kondisional `state.errorMessage?.let { ... }`.
* Dengan struktur ini, tombol hanya tampil ketika `errorMessage` bernilai tidak `null` dan langsung memicu pemanggilan ulang `viewModel.loadData()` ketika ditekan.

### 3. Menangkap Terlalu Banyak (`catch (e: Exception)`)
* **Pesan yang muncul saat menekan Load lalu Cancel:**
  Pesan berubah menjadi `"Failed to load: Job was cancelled"` (atau pesan bawaan `JobCancellationException`), bukan `"Loading cancelled"`.
* **Penyebab:**
  Ketika `loadJob?.cancel()` dipanggil, titik suspensi (`delay()`) melempar `CancellationException` yang merupakan subkelas dari `Exception`. Karena blok `catch (e: Exception)` menangkap semua jenis pengecualian, proses pembatalan ikut tertangkap sebagai error dan menimpa state `errorMessage`. Solusinya adalah menangkap exception spesifik (`IOException`) atau melempar ulang `CancellationException` (`throw e`).

### 4. Di Mana Macetnya (`Thread.sleep(2000L)` vs `withContext(Dispatchers.IO)`)
* **Saat `Thread.sleep(2000L)` di dalam `withContext(Dispatchers.IO)`:**
  **Layar tidak macet** (tombol *Tap* tetap merespons sentuhan dengan lancar). Pemanggilan `withContext(Dispatchers.IO)` memindahkan eksekusi ke *worker thread* latar belakang sehingga pemblokiran *thread* oleh `Thread.sleep()` tidak mengganggu *main thread* yang menggambar UI.
* **Setelah `withContext` dihapus:**
  **Layar langsung macet (*freeze*) selama $2$ detik.** Secara bawaan, `viewModelScope` berjalan pada `Dispatchers.Main` (*main thread*). Tanpa `withContext(Dispatchers.IO)`, `Thread.sleep(2000L)` menghentikan *main thread* secara langsung sehingga tidak ada *frame* UI maupun input sentuhan yang dapat diproses selama durasi tersebut.

### 5. Pertanyaan Konsep
* **(a) Perbedaan `launch` dan `async`:**
  `launch` digunakan untuk menjalankan coroutine tanpa mengembalikan nilai hasil (*fire-and-forget*) dan menghasilkan objek `Job` untuk mengontrol pembatalan. Sebaliknya, `async` menghasilkan `Deferred<T>` yang mengembalikan nilai di masa depan melalui pemanggilan `.await()`, sehingga ideal untuk menjalankan beberapa tugas independen secara paralel.
* **(b) Alasan `GlobalScope` sebaiknya dihindari:**
  Coroutine pada `GlobalScope` hidup selama seluruh siklus hidup aplikasi dan tidak terikat pada lifecycle layar maupun `ViewModel`. Jika pengguna menutup layar saat proses masih berjalan, coroutine tidak dibatalkan secara otomatis sehingga berisiko menyebabkan kebocoran memori (*memory leak*), pemborosan sumber daya, atau *crash* saat memperbarui UI yang sudah tidak aktif.
* **(c) Dampak kegagalan `getTasks()` terhadap `getAnnouncement()`:**
  Sesuai prinsip **Structured Concurrency**, ketika salah satu *child coroutine* (`getTasks()`) gagal dan melempar `IOException`, parent scope (`coroutineScope`) segera membatalkan seluruh *sibling coroutine* lain yang masih berjalan (termasuk `getAnnouncement()`), kemudian meneruskan exception tersebut ke blok `try/catch` di luarnya.

### 6. Tantangan (Tombol Sort dengan `Dispatchers.Default`)
* Fitur pengurutan diimplementasikan di dalam `ViewModel` dengan membungkus proses pengurutan daftar (`sortedBy` / `sortedByDescending`) ke dalam `withContext(Dispatchers.Default)`.
* `Dispatchers.Default` dipilih karena dioptimalkan untuk operasi komputasi intensif CPU seperti pengurutan dan pemrosesan koleksi data sebelum hasilnya dikembalikan ke `Dispatchers.Main` untuk memperbarui UI State.
