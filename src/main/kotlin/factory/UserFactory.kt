package factory

import androidx.compose.runtime.mutableStateOf
import model.view.User
import java.util.*

object UserFactory {


    fun createUser(firstName: String, lastName: String): User {
        return User(
            id = UUID.randomUUID().toString(),
            firstName = mutableStateOf(firstName),
            lastName = mutableStateOf(lastName),
            dayHours = DayHourFactory.create(),
            totalHours = mutableStateOf("")
        )
    }

}