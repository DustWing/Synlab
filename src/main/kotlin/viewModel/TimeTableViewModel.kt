package viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import factory.DateValFactory
import factory.DropDownItemFactory
import factory.UserFactory
import kotlinx.coroutines.delay
import model.view.DateVal
import model.view.DayHour
import model.view.DropDownItem
import model.view.User
import model.view.enums.AppStateEnum
import services.ExcelService
import services.SessionService
import services.timetable.TimeTableService

class TimeTableViewModel {

    val userList = mutableStateListOf<User>()
    val dateList = mutableListOf<DateVal>()
    val preSelectDdItems = mutableStateListOf<DropDownItem>()

    val dialogMsg = mutableStateOf("")
    val dialogTitle = mutableStateOf("")
    val openDialog = mutableStateOf(false)
    val appState = mutableStateOf(AppStateEnum.IDLE)

    val showSettingsDialog = mutableStateOf(false)

    private val mTimeTableService = TimeTableService()
    private val mExcelService = ExcelService()
    private val mSessionService = SessionService()


    suspend fun load() {

        appState.value = AppStateEnum.LOADING

        try {
            val session = mSessionService.load()
            userList.addAll(session.users)
            dateList.addAll(session.dateList)
            preSelectDdItems.addAll(session.preSelectDdItems)
            reInitUserList()
        } catch (e: Exception) {
            e.printStackTrace()
            dateList.addAll(DateValFactory.create())
            preSelectDdItems.addAll(DropDownItemFactory.createList())

        }

        appState.value = AppStateEnum.IDLE


    }

    suspend fun saveSession() {
        appState.value = AppStateEnum.LOADING
        try {
            mSessionService.save(userList, dateList, preSelectDdItems)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        delay(200)//need delay because UI is not updating for some reason
        appState.value = AppStateEnum.IDLE

    }

    fun editPreSelectDdItems(list: List<DropDownItem>) {
        preSelectDdItems.clear()
        preSelectDdItems.addAll(list)


        reInitUserList()

    }

    private fun reInitUserList() {
        //check dropdownItem if it still exists and update values
        userList.forEach { user ->

            user.dayHours.forEach { dayHour ->

                if (dayHour.dropDownItem.value.id != DropDownItemFactory.EMPTY) {
                    val found = preSelectDdItems.find { dayHour.dropDownItem.value.id == it.id }
                    if (found == null) {
                        dayHour.dropDownItem.value = DropDownItemFactory.create()
                        dayHour.readOnly.value = false
                        dayHour.hours.value = ""
                    } else {
                        dayHour.dropDownItem.value = found
                    }
                }

            }

        }

        //calculate all users
        mTimeTableService.calculateUsersTotalHours(userList)
    }

    fun addBtn() {
        userList.add(
            UserFactory.createUser("", "")
        )

        println(userList)
    }

    fun removeUserBtn(user: User) {
        userList.remove(user)
        println(userList)

    }

    fun clear() {

        userList.forEach { user ->
            user.dayHours.forEach {
                it.hours.value = ""
            }
        }

    }

    fun toExcel(path: String) {
        mExcelService.export(userList, dateList, path)
    }

    fun validate() {
        mTimeTableService.validateDays(userList)
    }

    fun handleException(e: Exception) {

        openDialog.value = true
        dialogTitle.value = "Error"
        dialogMsg.value = e.message.toString()
    }

    fun validateCell(dayHour: DayHour) {

        if (dayHour.hours.value.isBlank()) {
            dayHour.valid.value = true
        } else {
            try {
                mTimeTableService.validateHour(dayHour.hours.value)
            } catch (e: Exception) {
                dayHour.valid.value = false
            }

        }
    }

    fun calculateUserTotalHours(user: User) {
        try {
            user.totalHours.value = mTimeTableService.calculateUserTotalHours(user)
        } catch (e: Exception) {
            user.totalHours.value = "Invalid"
        }
    }

}