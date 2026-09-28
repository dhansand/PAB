fun main() {
val scores = mutableMapOf(24523162 to 80, 24523175 to 81, 24523163 to 82)
scores[24523162] = 96
scores.remove(24523163)
    for ((nim, score) in scores) {
        println("NIM: $nim, Nilai: $score")
    }

    println("Nilai NIM 24253163: ${scores[24253163]}")
}