package factory

import androidx.compose.runtime.mutableStateOf
import model.view.DateVal

object DateValFactory {

    fun create(): MutableList<DateVal> {
        return mutableListOf(
            DateVal(mutableStateOf("")),
            DateVal(mutableStateOf("")),
            DateVal(mutableStateOf("")),
            DateVal(mutableStateOf("")),
            DateVal(mutableStateOf("")),
            DateVal(mutableStateOf("")),
            DateVal(mutableStateOf("")),
        )
    }
}