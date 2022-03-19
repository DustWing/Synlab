package model.view

import androidx.compose.runtime.MutableState
import enums.Day

data class DayHour(
    val day: Day,
    val hours: MutableState<String>,
    val valid: MutableState<Boolean>,
    val readOnly: MutableState<Boolean>,
    val dropDownItem:MutableState<DropDownItem>
)
