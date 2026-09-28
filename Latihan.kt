enum class CourseStatus { ACTIVE, COMPLETED }

data class Course(val code: String, 
                  val name: String, 
                  val status: CourseStatus
)

fun Course.DisplayInfo() : String = "$code - $name - $status"

fun main() {
    val Courses = mutableListOf(
    Course("PAB101","Mobile App Development", CourseStatus.COMPLETED),
    Course("PABW102","Web App Development", CourseStatus.ACTIVE),
    Course("GIM103","Game Development", CourseStatus.ACTIVE))
    
    Courses.add(Course("SJK104","Network System", CourseStatus.ACTIVE))
    Courses.removeAt(1)
    
	for ((code, name, status) in Courses) {
    	println("$code - $name - $status")
	}
}