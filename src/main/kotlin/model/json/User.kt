package model.json

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val dayHours: List<DayHour> = listOf(),
    val totalHours: String = ""
)
