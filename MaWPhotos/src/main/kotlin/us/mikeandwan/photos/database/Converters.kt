package us.mikeandwan.photos.database

import androidx.room.TypeConverter
import us.mikeandwan.photos.domain.models.MediaType
import java.util.*

class Converters {
    @TypeConverter
    fun toCalendar(l: Long?): Calendar? {
        val cal = Calendar.getInstance()

        cal.timeInMillis = l!!

        return cal
    }

    @TypeConverter
    fun fromCalendar(cal: Calendar?): Long? {
        return cal?.timeInMillis
    }

    @TypeConverter
    fun fromMediaType(value: MediaType): String {
        return value.name
    }

    @TypeConverter
    fun toMediaType(value: String): MediaType {
        return MediaType.valueOf(value)
    }
}
