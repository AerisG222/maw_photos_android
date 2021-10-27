package us.mikeandwan.photos.uiold.photos

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object ExifDataFormatter {
    @JvmStatic
    fun format(value: Date?): String {
        if (value == null) {
            return "--"
        }
        val sdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US)
        return sdf.format(value)
    }

    @JvmStatic
    fun format(value: String?): String {
        return value ?: "--"
    }

    @JvmStatic
    fun format(value: Double?): String {
        return value?.toString() ?: "--"
    }

    @JvmStatic
    fun format(value: Short?): String {
        return value?.toString() ?: "--"
    }

    @JvmStatic
    fun format(value: Int?): String {
        return value?.toString() ?: "--"
    }

    @JvmStatic
    fun formatOneDecimal(value: Double?): String {
        if (value == null) {
            return "--"
        }
        val df = DecimalFormat("0.0")
        return df.format(value)
    }

    @JvmStatic
    fun formatFourDecimals(value: Double?): String {
        if (value == null) {
            return "--"
        }
        val df = DecimalFormat("0.0000")
        return df.format(value)
    }

    @JvmStatic
    fun formatMillimeters(value: Double?): String {
        if (value == null) {
            return "--"
        }
        val df = DecimalFormat("0.0")
        return df.format(value) + " mm"
    }

    @JvmStatic
    fun formatMeters(value: Double?): String {
        if (value == null) {
            return "--"
        }
        val df = DecimalFormat("0.0")
        return df.format(value) + " m"
    }

    @JvmStatic
    fun formatLatitude(value: Double?): String {
        if (value == null) {
            return "--"
        }
        return if (value >= 0) {
            "$value (North)"
        } else {
            "$value (South)"
        }
    }

    @JvmStatic
    fun formatLongitude(value: Double?): String {
        if (value == null) {
            return "--"
        }
        return if (value >= 0) {
            "$value (East)"
        } else {
            "$value (West)"
        }
    }

    @JvmStatic
    fun formatAltitude(value: Double?): String {
        if (value == null) {
            return "--"
        }
        return if (value >= 0) {
            "$value m Above Sea Level"
        } else {
            "$value m Below Sea Level"
        }
    }
}