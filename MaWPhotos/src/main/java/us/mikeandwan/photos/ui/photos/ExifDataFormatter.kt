package us.mikeandwan.photos.ui.photos

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object ExifDataFormatter {
    @JvmStatic
    fun format(`val`: Date?): String {
        if (`val` == null) {
            return "--"
        }
        val sdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US)
        return sdf.format(`val`)
    }

    @JvmStatic
    fun format(`val`: String?): String {
        return `val` ?: "--"
    }

    @JvmStatic
    fun format(`val`: Double?): String {
        return `val`?.toString() ?: "--"
    }

    @JvmStatic
    fun format(`val`: Short?): String {
        return `val`?.toString() ?: "--"
    }

    @JvmStatic
    fun format(`val`: Int?): String {
        return `val`?.toString() ?: "--"
    }

    @JvmStatic
    fun formatOneDecimal(`val`: Double?): String {
        if (`val` == null) {
            return "--"
        }
        val df = DecimalFormat("0.0")
        return df.format(`val`)
    }

    @JvmStatic
    fun formatFourDecimals(`val`: Double?): String {
        if (`val` == null) {
            return "--"
        }
        val df = DecimalFormat("0.0000")
        return df.format(`val`)
    }

    @JvmStatic
    fun formatMillimeters(`val`: Double?): String {
        if (`val` == null) {
            return "--"
        }
        val df = DecimalFormat("0.0")
        return df.format(`val`) + " mm"
    }

    @JvmStatic
    fun formatMeters(`val`: Double?): String {
        if (`val` == null) {
            return "--"
        }
        val df = DecimalFormat("0.0")
        return df.format(`val`) + " m"
    }

    @JvmStatic
    fun formatLatitude(`val`: Double?): String {
        if (`val` == null) {
            return "--"
        }
        return if (`val` >= 0) {
            "$`val` (North)"
        } else {
            "$`val` (South)"
        }
    }

    @JvmStatic
    fun formatLongitude(`val`: Double?): String {
        if (`val` == null) {
            return "--"
        }
        return if (`val` >= 0) {
            "$`val` (East)"
        } else {
            "$`val` (West)"
        }
    }

    @JvmStatic
    fun formatAltitude(`val`: Double?): String {
        if (`val` == null) {
            return "--"
        }
        return if (`val` >= 0) {
            "$`val` m Above Sea Level"
        } else {
            "$`val` m Below Sea Level"
        }
    }
}