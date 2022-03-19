package model.view

import androidx.compose.runtime.MutableState
import java.util.UUID

data class DropDownItem(
    val id:String = UUID.randomUUID().toString(),
    val label: MutableState<String>,
    val value: MutableState<String>,
    val addInTotal: MutableState<Boolean>,
    val addInDay: MutableState<Boolean>,

    ) {
    override fun toString(): String {
        return label.value
    }
}
