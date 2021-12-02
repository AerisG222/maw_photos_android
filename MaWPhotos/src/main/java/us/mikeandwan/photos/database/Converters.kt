package us.mikeandwan.photos.database

import androidx.room.TypeConverter
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
}