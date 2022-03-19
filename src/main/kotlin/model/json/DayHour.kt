package model.json

import enums.Day

data class DayHour(
    val day: Day,
    val hours: String,
    val valid: Boolean,
    val readOnly: Boolean,
    val dropDownItem:DropDownItem
)
