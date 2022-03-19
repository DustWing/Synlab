package utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.function.Function


object FileUtils {
    @Throws(IOException::class)
    fun <T> readByLine(
        filePath: String,
        collection: MutableCollection<T>,
        function: Function<String, T>
    ) {
        BufferedReader(FileReader(filePath)).use { reader ->
            var currentLine: String
            while (reader.readLine().also { currentLine = it } != null) {
                collection.add(function.apply(currentLine))
            }
        }
    }

    @Throws(IOException::class)
    suspend fun read(filePath: String): String {
        val resultStringBuilder = StringBuilder()
        BufferedReader(withContext(Dispatchers.IO) {
            FileReader(filePath)
        }).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                resultStringBuilder.append(line).append("\n")
            }
        }
        return resultStringBuilder.toString()
    }

    @Throws(IOException::class)
    suspend fun write(filePath: String, value: String) {
        withContext(Dispatchers.IO) {
            FileWriter(filePath)
        }.use { myWriter -> myWriter.write(value) }
    }

    suspend fun appendToFile(filePath: String, value: String) {
        withContext(Dispatchers.IO) {
            FileWriter(filePath, true)
        }.use {
            val bw = BufferedWriter(it)
            withContext(Dispatchers.IO) {
                bw.write(value)
                bw.newLine()
                bw.close()
            }
        }

    }

}