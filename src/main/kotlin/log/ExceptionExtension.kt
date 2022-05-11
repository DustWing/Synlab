package services.exceptionlog

fun Throwable.logError() {
    MyLog.error("", this)
}