package services

import androidx.compose.runtime.mutableStateOf
import model.json.*
import utils.FileUtils
import utils.JsonUtils
import kotlin.streams.toList

class SessionService {

    private val mAppdata = System.getenv("APPDATA")

    private val mFilePath = "$mAppdata/synlab/lastsession.json"

    suspend fun save(
        data: List<model.view.User>,
        dateList: List<model.view.DateVal>,
        preSelectDdItems: List<model.view.DropDownItem>
    ) {

        val users = userToJson(data)

        val dates = datesToJson(dateList)

        val dropDownItems = dropDownItemsToJson(preSelectDdItems)


        val session = Session(
            users = users,
            dates,
            dropDownItems
        )

        val json = JsonUtils.GSON.toJson(session)

        FileUtils.write(filePath = mFilePath, value = json)
    }


    private fun userToJson(data: List<model.view.User>): List<User> {
        return data.stream().map {
            User(
                id = it.id,
                firstName = it.firstName.value,
                lastName = it.lastName.value,
                dayHours = it.dayHours.stream().map { dayHour ->
                    DayHour(
                        day = dayHour.day,
                        hours = dayHour.hours.value,
                        valid = dayHour.valid.value,
                        readOnly = dayHour.readOnly.value,
                        dropDownItem = DropDownItem(
                            id = dayHour.dropDownItem.value.id,
                            label = dayHour.dropDownItem.value.label.value,
                            value = dayHour.dropDownItem.value.value.value,
                            addInTotal = dayHour.dropDownItem.value.addInTotal.value,
                            addInDay = dayHour.dropDownItem.value.addInDay.value
                        )

                    )
                }.toList(),
                totalHours = it.totalHours.value
            )
        }.toList()

    }

    private fun datesToJson(dateList: List<model.view.DateVal>): List<DateVal> {
        return dateList.stream().map {
            DateVal(
                date = it.date.value
            )
        }.toList()
    }

    private fun dropDownItemsToJson(preSelectDdItems: List<model.view.DropDownItem>): List<DropDownItem> {
        return preSelectDdItems.stream().map {
            DropDownItem(
                id = it.id,
                label = it.label.value,
                value = it.value.value,
                addInTotal = it.addInTotal.value,
                addInDay = it.addInDay.value
            )
        }.toList()
    }


    suspend fun load(): model.view.Session {

        val json = FileUtils.read(mFilePath)

        val session = JsonUtils.GSON.fromJson(json, Session::class.java)

        return model.view.Session(
            jsonToUser(session.users),
            jsonToDates(session.dateList),
            jsonToDropDownItems(session.preSelectDdItems)
        )

    }

    private fun jsonToUser(data: List<User>): List<model.view.User> {

        return data.stream().map {
            model.view.User(
                id = it.id,
                firstName = mutableStateOf(it.firstName),
                lastName = mutableStateOf(it.lastName),
                dayHours = it.dayHours.stream().map { dayHour ->
                    model.view.DayHour(
                        day = dayHour.day,
                        hours = mutableStateOf(dayHour.hours),
                        valid = mutableStateOf(dayHour.valid),
                        readOnly = mutableStateOf(dayHour.readOnly),
                        dropDownItem = mutableStateOf(
                            model.view.DropDownItem(
                                label = mutableStateOf(dayHour.dropDownItem.label),
                                value = mutableStateOf(dayHour.dropDownItem.value),
                                addInTotal = mutableStateOf(dayHour.dropDownItem.addInTotal),
                                addInDay = mutableStateOf(dayHour.dropDownItem.addInDay)
                            )
                        )

                    )
                }.toList(),
                totalHours = mutableStateOf(it.totalHours)
            )
        }.toList()

    }

    private fun jsonToDates(dateList: List<DateVal>): List<model.view.DateVal> {
        return dateList.stream().map {
            model.view.DateVal(
                date = mutableStateOf(it.date)
            )
        }.toList()
    }

    private fun jsonToDropDownItems(preSelectDdItems: List<DropDownItem>): List<model.view.DropDownItem> {
        return preSelectDdItems.stream().map {
            model.view.DropDownItem(
                label = mutableStateOf(it.label),
                value = mutableStateOf(it.value),
                addInTotal = mutableStateOf(it.addInTotal),
                addInDay = mutableStateOf(it.addInDay)
            )
        }.toList()
    }
}