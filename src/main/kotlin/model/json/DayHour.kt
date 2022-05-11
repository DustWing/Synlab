package model.json

import enums.Day

data class DayHour(
    val day: Day? = null,
    val hours: String = "",
    val valid: Boolean = false,
    val readOnly: Boolean = false,
    val dropDownItem:DropDownItem = DropDownItem()
)
