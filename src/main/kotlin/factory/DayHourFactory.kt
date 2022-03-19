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
                    mutableStateOf(
                        DropDownItem(
                            label = mutableStateOf(""),
                            value = mutableStateOf(""),
                            addInTotal = mutableStateOf(false),
                            addInDay = mutableStateOf(false)
                        )
                    ),

                    )
            )


        }
        return list;

    }

}