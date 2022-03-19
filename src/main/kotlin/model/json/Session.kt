package model.json


data class Session(

    val users: List<User>,
    val dateList: List<DateVal>,
    val preSelectDdItems: List<DropDownItem>

)