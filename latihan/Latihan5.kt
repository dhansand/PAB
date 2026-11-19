enum class CourseStatus { ACTIVE, COMPLETED, DROPPED }

object AppConfig {
    const val MAX_COURSES = 5
}

data class Course(
    val code: String,
    val name: String,
    val status: CourseStatus
) {
    companion object {
        const val PREFIX = "PAB"
    }
}

fun MutableList<Course>.addCourse(course: Course): Boolean {
    if (size < AppConfig.MAX_COURSES &&
        course.code.startsWith(Course.PREFIX)
    ) {
        add(course)
        return true
    }
    return false
}

fun main() {
    val courses = mutableListOf(
        Course("PAB101", "Mobile App Development", CourseStatus.COMPLETED),
        Course("PABW102", "Web App Development", CourseStatus.ACTIVE),
        Course("GIM103", "Game Development", CourseStatus.ACTIVE)
    )
    
    val course1 = Course("PAB104", "App Development", CourseStatus.ACTIVE)
    println("Course 1 berhasil ditambahkan: ${courses.addCourse(course1)}")

    val course2 = Course("SJK104", "Network System", CourseStatus.ACTIVE)
    println("Course 2 berhasil ditambahkan: ${courses.addCourse(course2)}")

    println("\nDaftar Mata Kuliah:")

    for (course in courses) {
        println("${course.code} - ${course.name} - ${course.status}")
    }
}