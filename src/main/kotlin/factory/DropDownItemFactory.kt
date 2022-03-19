package factory

import androidx.compose.runtime.mutableStateOf
import model.view.DropDownItem

object DropDownItemFactory {

    const val EMPTY ="empty"

    fun create():DropDownItem{
        return  DropDownItem(
            id=EMPTY,
            label = mutableStateOf(""),
            value = mutableStateOf(""),
            addInTotal = mutableStateOf(false),
            addInDay = mutableStateOf(false)
        )
    }

    fun createList(): List<DropDownItem> {
        return listOf(
            DropDownItem(
                label = mutableStateOf("Night off"),
                value = mutableStateOf("00:00-08:00"),
                addInTotal = mutableStateOf(false),
                addInDay = mutableStateOf(true)

            ),
            DropDownItem(
                label = mutableStateOf("Day Off"),
                value = mutableStateOf(""),
                addInTotal = mutableStateOf(false),
                addInDay = mutableStateOf(false)
            ),
            DropDownItem(
                label = mutableStateOf("Holiday"),
                value = mutableStateOf("00:00-06:30"),
                addInTotal = mutableStateOf(true),
                addInDay = mutableStateOf(false)
            ),
        )
    }

}