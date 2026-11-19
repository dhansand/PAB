//PIPELINE
data class MenuItem(val name: String, val price: Int, val category: String)

fun main() {
    val menuList = listOf(
        MenuItem("Espresso", 18000, "Coffee"),
        MenuItem("Americano", 22000, "Coffee"),
        MenuItem("Matcha Latte", 28000, "Non-Coffee"),
        MenuItem("Croissant", 25000, "Food"),
        MenuItem("Ice Tea", 12000, "Non-Coffee")
    )

    val pipelineResult = menuList
        .filter { it.price > 15000 }
        .sortedBy { it.price }
        .map { "${it.name} - Rp${it.price}" }

    println("Hasil Pipeline:")
    pipelineResult.forEach { println(it) }
}