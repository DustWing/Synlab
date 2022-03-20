package time

data class HourMinutePair(
    val from: HourMinute,
    val to: HourMinute,
    val nightShift: Boolean,
    val duration: HourMinute
) {
    override fun toString(): String {
        return "$from-$to"
    }

    companion object {

        fun parse(value: String): HourMinutePair {

            if(value.isBlank()){
                throw HourMinuteParseException("Invalid Hour Minute Pair: Empty value")
            }

            val list = value.split("-")

            if (list.size != 2) {
                throw HourMinuteParseException("Invalid Hour Minute Pair:$value")
            }

            val from = HourMinute.parse(list[0])
            val to = HourMinute.parse(list[1])
            return create(from, to)
        }

        fun create(fromH: Int, fromM: Int, toH: Int, toM: Int): HourMinutePair {

            val from = HourMinute(fromH, fromM)
            val to = HourMinute(toH, toM)

            return create(from, to)
        }

        private fun create(from: HourMinute, to: HourMinute): HourMinutePair {
            val nightShift = HourMinute.isNightShift(from, to)
            val duration = HourMinute.between(from, to)

            return HourMinutePair(
                from = from,
                to = to,
                nightShift = nightShift,
                duration = duration
            )
        }
    }
}
