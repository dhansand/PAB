//LAMBDA & FUNCTION n COLLECTION
data class MenuItem(val name: String, val price: Int, val category: String)

fun main() {
    val menuList = listOf(
        MenuItem("Espresso", 18000, "Coffee"),
        MenuItem("Americano", 22000, "Coffee"),
        MenuItem("Matcha Latte", 28000, "Non-Coffee"),
        MenuItem("Croissant", 25000, "Food"),
        MenuItem("Ice Tea", 12000, "Non-Coffee")
    )

    val isAffordable: (Int) -> Boolean = { price -> price <= 20000 }

    menuList.forEach { item ->
        if (isAffordable(item.price)) {
            println("${item.name} terjangkau (${item.price})")
        }
    }

    val coffeeNames = menuList
        .filter { it.category == "Coffee" }
        .map { it.name }

    println("Menu Kategori Coffee: $coffeeNames")
}