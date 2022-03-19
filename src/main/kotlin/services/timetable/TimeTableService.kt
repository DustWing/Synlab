package services.timetable

import enums.Day
import model.view.User
import time.HourMinute
import time.HourMinutePair
import kotlin.streams.toList

class TimeTableService(

) {


    fun validateHour(string: String) {
        HourMinutePair.parse(string)
    }

    fun calculateUserTotalHours(user: User): String {
        return totalHoursWorked(

            user.dayHours.stream().filter {

                if(it.readOnly.value){
                    return@filter it.dropDownItem.value.addInTotal.value && it.hours.value.isNotBlank()
                }
                return@filter it.hours.value.isNotBlank()


            }.map {
                HourMinutePair.parse(it.hours.value)
            }.toList()

        ).toString()
    }

    fun calculateUsersTotalHours(users: List<User>): Map<String, String> {
        return buildMap {
            users.forEach { user ->

                this[user.id] = calculateUserTotalHours(user)

            }
        }
    }


    fun validateDays(users: List<User>) {

        val map = buildDayHoursMap(users)


        map.forEach {
            try {
                validateDay(it.value)
            } catch (e: Exception) {
                throw TimeTableException("${it.key}:  ${e.message}")
            }

        }


    }

    private fun buildDayHoursMap(users: List<User>): Map<Day, MutableList<String>> {
        return buildMap {
            users.forEach { user ->

                user.dayHours.stream().filter{
                    if(it.readOnly.value){
                        return@filter it.dropDownItem.value.addInDay.value
                    }
                    return@filter it.hours.value.isNotBlank()
                }.forEach {

                    if (this.containsKey(it.day)) {
                        this[it.day]?.add(it.hours.value)
                    } else {
                        this[it.day] = mutableListOf()
                        this[it.day]?.add(it.hours.value)
                    }
                }

            }
        }
    }


    private fun validateDay(list: List<String>) {

        val hourMinutePairs = list.stream()
            .filter {
                it.isNotBlank()
            }
            .map {
                HourMinutePair.parse(it)
            }.toList()

        checkDay(
            hourMinutePairs
        )

    }

    private fun checkDay(list: Collection<HourMinutePair>) {


        val day = List(
            size = 24,
            init = {
                0
            }
        ).toMutableList()


        var nightShift = false

        prepare(list).forEach {

            nightShift = it.nightShift
            val from = it.from.hours

            //if is marked as all night convert to 24 so the list would fill
            val to = if (it.nightShift) 24 else it.to.hours

            for (i in from..to) {

                //check for minutes 'from' to complete hours
                if (i == from) {
                    if (it.from.minutes > 0) {
                        day[i] = it.from.minutes
                    } else {
                        day[i] = 60
                    }
                }


                //check for minutes 'to' to complete hours
                else if (i == to) {
                    if (it.to.minutes > 0) {
                        day[i] = it.to.minutes
                    }
                } else {
                    day[i] = 60
                }

            }


        }

        if (!nightShift) {
            throw TimeTableException("Night shift not filled")
        }

        val result = mutableListOf<String>()
        day.forEachIndexed { index, i ->
            if (i < 60) {
                result.add(HourMinute.parse(index).toString())
            }
        }

        if (result.size > 0)
            throw TimeTableException("Hours not fully assigned : $result")

    }


    private fun prepare(list: Collection<HourMinutePair>): Set<HourMinutePair> {
        var fromHour = 0
        var fromMinute = 0
        var toHour = 0
        var toMinute = 0


        val set = mutableSetOf<HourMinutePair>()
        list.sortedBy {
            it.from.hours
        }.forEach {

            if (toHour <= it.to.hours && it.from.hours <= toHour) {
                toHour = it.to.hours

                if (toMinute < it.to.minutes) toMinute = it.to.minutes


            } else {

                set.add(
                    HourMinutePair.create(
                        fromH = fromHour,
                        fromM = fromMinute,
                        toH = toHour,
                        toM = toMinute
                    )
                )
                fromHour = it.from.hours
                fromMinute = it.from.minutes
                toHour = it.to.hours
                toMinute = it.to.minutes

            }


        }

        set.add(
            HourMinutePair.create(
                fromH = fromHour,
                fromM = fromMinute,
                toH = toHour,
                toM = toMinute
            )
        )

        return set

    }


    private fun hoursAddList(list: Collection<HourMinute>): HourMinute {

        var result = HourMinute.zero()

        list.forEach {
            result = HourMinute.add(result, it)
        }

        return result
    }

    private fun totalHoursWorked(list: Collection<HourMinutePair>): HourMinute {
        return hoursAddList(
            list.stream().map {
                it.duration
            }.toList()
        )
    }


}