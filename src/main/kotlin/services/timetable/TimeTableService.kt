package services.timetable

import enums.Day
import model.view.User
import time.HourMinute
import time.HourMinutePair
import kotlin.streams.toList

class TimeTableService(

) {


    fun validateHour(string: String) {

        val list = string.split(";")
        list.forEach {
            HourMinutePair.parse(it)

        }
    }

    fun calculateUserTotalHours(user: User): String {
        return totalHoursWorked(

            user.dayHours.stream().filter {

                if (it.readOnly.value) {
                    return@filter it.dropDownItem.value.addInTotal.value && it.hours.value.isNotBlank()
                }
                return@filter it.hours.value.isNotBlank()


            }.map {

                val list = it.hours.value.split(";")
                list.stream().map { e ->
                    HourMinutePair.parse(e)
                }.toList()


            }.flatMap(List<HourMinutePair>::stream).toList()

        ).toString()
    }


    fun validateDays(users: List<User>) {

        val map = buildDayHoursMap(users)


        map.forEach {
            try {
                println("${it.key}")
                validateDay(it.value)
            } catch (e: Exception) {
                e.printStackTrace()
                throw TimeTableException("${it.key}:  ${e.message}")
            }

        }


    }

    private fun buildDayHoursMap(users: List<User>): Map<Day, MutableList<String>> {
        return buildMap {
            users.forEach { user ->

                user.dayHours.forEach {

                    if (this.containsKey(it.day)) {
                        if ((it.readOnly.value && it.dropDownItem.value.addInDay.value) || it.hours.value.isNotBlank()) {
                            this[it.day]?.add(it.hours.value)
                        }

                    } else {

                        this[it.day] = mutableListOf()

                        if ((it.readOnly.value && it.dropDownItem.value.addInDay.value) || it.hours.value.isNotBlank()) {
                            this[it.day]?.add(it.hours.value)
                        }

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
                val list2 = it.split(";")
                list2.stream().map { e ->
                    HourMinutePair.parse(e)
                }.toList()


            }.flatMap(List<HourMinutePair>::stream).toList()

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
                    if (it.from.minutes > 0 && day[i] < 60) {
                        day[i] = it.from.minutes
                    } else {
                        day[i] = 60
                    }
                }


                //check for minutes 'to' to complete hours
                else if (i == to) {
                    if (it.to.minutes > 0 && day[i] < 60) {
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

        println(day)

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

            if (it.nightShift && it.from.hours <= toHour) {
                toHour = 0
                toMinute = 0
            } else if (toHour <= it.to.hours && it.from.hours <= toHour) {
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

        println(list)
        println(set)
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