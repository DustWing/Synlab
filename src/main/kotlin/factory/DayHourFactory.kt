package factory

import androidx.compose.runtime.mutableStateOf
import enums.Day
import model.view.DayHour
import model.view.DropDownItem

object DayHourFactory {


    fun create(): MutableList<DayHour> {

        val list = mutableListOf<DayHour>()

        Day.values().forEach {

            list.add(
                DayHour(
                    it,
                    mutableStateOf(""),
                    mutableStateOf(true),
                    mutableStateOf(false),
                    mutableStateOf(DropDownItemFactory.create()),

                    )
            )


        }
        return list;

    }

}