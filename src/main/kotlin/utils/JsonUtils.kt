package utils

import com.google.gson.*
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.util.*


object JsonUtils {
    val GSON: Gson = GsonBuilder()
        .registerTypeAdapter(OffsetDateTime::class.java, offsetDateTimeTypeAdapter())
        .registerTypeAdapter(Date::class.java, dateTypeAdapter())
        .create()


    private fun dateTypeAdapter(): JsonDeserializer<Date?> {
        return object : JsonDeserializer<Date?> {
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")

            @Throws(JsonParseException::class)
            override fun deserialize(
                json: JsonElement,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): Date? {
                return try {
                    df.parse(json.asString)
                } catch (e: ParseException) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }


    private fun offsetDateTimeTypeAdapter(): JsonDeserializer<OffsetDateTime?> {
        return JsonDeserializer<OffsetDateTime?> { json, _, _ ->
            try {
                OffsetDateTime.parse(json.asString)
            } catch (e: DateTimeParseException) {
                e.printStackTrace()
                null
            }
        }
    }


}