//GENERICS
fun <T> cetakElemenPertama(list: List<T>): T? {
    if (list.isEmpty()) return null
    return list[0]
}

fun main() {
    
    val daftarAngka = listOf(10, 20, 30, 40, 50)
    val daftarMenu = listOf("Espresso", "Americano", "Matcha")

    val angkaPertama = cetakElemenPertama(daftarAngka)
    val menuPertama = cetakElemenPertama(daftarMenu)

    println("Elemen pertama Angka  : $angkaPertama")
    println("Elemen pertama Menu   : $menuPertama")
}