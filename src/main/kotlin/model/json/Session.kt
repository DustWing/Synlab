package model.json


data class Session(

    val users: List<User> = listOf(),
    val dateList: List<DateVal> = listOf(),
    val preSelectDdItems: List<DropDownItem> = listOf()

)