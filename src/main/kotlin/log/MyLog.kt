package services.exceptionlog

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import utils.FileUtils
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

@OptIn(DelicateCoroutinesApi::class)
object MyLog {

    private val mAppdata = System.getenv("APPDATA")

    private val mFilePath = "$mAppdata/synlab/log.txt"

    private enum class OS {
        WINDOWS,
        UNIX,
        SOLARIS,
        MAC;
    }

    /**
     * System OS the app is running on*/
    private val systemOs: OS
        get() = getOS(System.getProperty("os.name").lowercase(Locale.getDefault()))

    private fun getOS(string: String): OS {
        if (string.contains("windows")) {
            return OS.WINDOWS
        }
        if (string.contains("nix") || string.contains("nux") || string.contains("aix")) {
            return OS.UNIX
        }
        if (string.contains("sunos")) {
            return OS.SOLARIS
        }
        if (string.contains("mac")) {
            return OS.MAC
        }

        throw RuntimeException("Invalid OS - WTF dude")
    }

    enum class Level {
        ALL,
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        NONE
    }

    private var _level = 0
    private var _filePath = ""
    private var _saveToFile = false
    private val _queue = ConcurrentLinkedQueue<String>()


    fun trace(s: String) {
        _log(1, "[TRACE]", s)
    }

    fun debug(s: String) {
        _log(2, "[DEBUG]", s)
    }

    fun info(s: String) {
        _log(3, "[INFO]", s)
    }

    fun warn(s: String) {
        _log(4, "[WARN]", s)
    }

    fun error(s: String) {
        _log(5, "[ERROR]", s)
    }

    fun error(s: String, t: Throwable) {
        _log(5, "[ERROR]", s, t)
    }

    fun _time(): String {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
    }

    private fun _log(level: Int, prefix: String, s: String) {
        if (level >= _level) {
            val line = _time() + " " + prefix + " " + s
            println(line)
            if (_saveToFile) _queue.add(line)
        }

    }

    private fun _log(level: Int, prefix: String, s: String, t: Throwable) {
        if (level >= _level) {
            val line = _time() + " " + prefix + " " + s + t.stackTraceToString()
            println(line)
            if (_saveToFile) _queue.add(line)
        }

    }

    init {
        //configuration
        val property = Level.ALL

        _saveToFile = true

        _filePath = when (systemOs) {
            OS.WINDOWS -> mFilePath
            OS.MAC -> throw RuntimeException("Not implemented")
            OS.SOLARIS -> throw RuntimeException("Not implemented")
            OS.UNIX -> throw RuntimeException("Not implemented")
        }


        _level = when (property) {
            Level.ALL -> 0
            Level.TRACE -> 1
            Level.DEBUG -> 2
            Level.INFO -> 3
            Level.WARN -> 4
            Level.ERROR -> 5
            Level.NONE -> 6
        }


        if (_saveToFile) {
            GlobalScope.launch(Dispatchers.IO) {
                consumeQueue()
            }

        }


    }

    //TODO fix this - is not optimised, maybe write multiple messages in one write
    private suspend fun consumeQueue() {
        while (true) {
            val s = _queue.poll()
            if (s != null) {
                FileUtils.appendToFile(
                    filePath = _filePath,
                    value = s
                )
            }
        }
    }
}