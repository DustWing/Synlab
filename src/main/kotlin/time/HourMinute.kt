package time

data class HourMinute(
    val hours: Int,
    val minutes: Int
) {
    override fun toString(): String {
        return "${if (hours < 10) "0$hours" else hours}:${if (minutes < 10) "0$minutes" else minutes}"
    }

    companion object {

        fun parse(value: String): HourMinute {

            if (value.isBlank()) {
                throw HourMinuteParseException("Value cannot be blank")
            }

            val list = value.split(":")
            if (list.size != 2) {
                throw HourMinuteParseException("Value cannot be parse into two parts")
            }

            val hours = list[0].toInt()

            val minutes = list[1].toInt()

            return HourMinute(
                hours = hours,
                minutes = minutes
            )

        }

        fun parse(hour: Int): HourMinute {

            val str = if (hour < 10) "0$hour:00" else "$hour:00"

            return parse(str)

        }

        fun zero(): HourMinute {
            return HourMinute(
                hours = 0,
                minutes = 0
            )
        }

        fun add(from: HourMinute, to: HourMinute): HourMinute {


            var hours = to.hours + from.hours

            var minutes = to.minutes + from.minutes

            if (minutes >= 60) {
                hours += minutes / 60
                minutes %= 60
            }

            return HourMinute(
                hours = hours,
                minutes = minutes
            )
        }

        fun isNightShift(from: HourMinute, to: HourMinute): Boolean {

            if (to.hours <= from.hours) {

                if (to.hours == from.hours && to.minutes > from.minutes) {
                    return false
                }

                return true
            }

            return false
        }

        fun between(from: HourMinute, to: HourMinute): HourMinute {


            if (from.hours > 23) {
                throw NumberFormatException("Hours cannot be larger than 23")
            }

            if (from.minutes > 59) {
                throw NumberFormatException("Minutes cannot be larger than 59")
            }

            if (to.hours > 23) {
                throw NumberFormatException("Hours cannot be larger than 23")
            }


            if (to.minutes > 59) {
                throw NumberFormatException("Minutes cannot be larger than 59")
            }

            val fromHour = from.hours
            val toHour = to.hours

            val fromMinutes = from.minutes
            val toMinutes = to.minutes

            var hours = if (toHour <= fromHour && toMinutes <=fromMinutes ) toHour + 24 - fromHour else toHour - fromHour

            var minutes = toMinutes - fromMinutes

            if (minutes < 0) {
                hours--
                minutes += 60
            }

            return HourMinute(
                hours = hours,
                minutes = minutes
            )
        }

    }

}