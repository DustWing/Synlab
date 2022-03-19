package services

import enums.Day
import excel.ExcelUtil
import model.view.DateVal
import model.view.User
import kotlin.streams.toList

class ExcelService {


    fun export(
        users: List<User>,
        dateList: List<DateVal>,
        path: String
    ) {


        val rows = mutableListOf<List<String>>()

        rows.add(getDayList())

        rows.add(getDateList(dateList))

        rows.addAll(getUserList(users))


        ExcelUtil.save(
            rows = rows,
            path = path
        )

    }

    private fun getDateList(dateList: List<DateVal>): List<String> {
        val list = mutableListOf<String>()
        list.add("")
        list.addAll(dateList.stream().map { it.date.value }.toList())
        list.add("")
        return list
    }

    private fun getDayList(): List<String> {
        val daysList = mutableListOf<String>()
        daysList.add("")// need to empty column
        Day.values().forEach {
            daysList.add(it.toString())
        }
        return daysList
    }

    private fun getUserList(users: List<User>): List<List<String>> {

        val result = mutableListOf<List<String>>()
        users.forEach { user ->

            val userList = mutableListOf<String>()

            userList.add(user.name())

            user.dayHours.forEach {
                if(it.readOnly.value){
                    userList.add(it.dropDownItem.value.label.value)
                }else{
                    userList.add(it.hours.value)

                }
            }

            userList.add(user.totalHours.value)
            result.add(userList)

        }

        return result
    }


}