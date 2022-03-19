package enums

enum class Day {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;


    fun short(): String {
        if (this.name.length < 3) {
            return this.name
        }

        return this.name.substring(0, 3)
    }

}