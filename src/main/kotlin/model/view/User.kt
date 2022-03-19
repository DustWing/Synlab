package model.view

import androidx.compose.runtime.MutableState

data class User(
    val id: String,
    val firstName: MutableState<String>,
    val lastName: MutableState<String>,
    val dayHours: List<DayHour>,
    val totalHours: MutableState<String>
) {
    fun name(): String {
        return "${firstName.value} ${lastName.value}"
    }
}
