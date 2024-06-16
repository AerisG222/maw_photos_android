package us.mikeandwan.photos.utils

import okhttp3.internal.toImmutableList
import us.mikeandwan.photos.domain.models.ExifData
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object ExifDataFormatter {
    @JvmStatic
    fun prepareForDisplay(exif: ExifData): List<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()

        // exif
        list.add(Pair("Bits Per Sample", format(exif.bitsPerSample)))
        list.add(Pair("Compression", format(exif.compression)))
        list.add(Pair("Contrast", format(exif.contrast)))
        list.add(Pair("Create Date", format(exif.createDate)))
        list.add(Pair("Digital Zoom Ratio", format(exif.digitalZoomRatio)))
        list.add(Pair("Exposure Compensation", format(exif.exposureCompensation)))
        list.add(Pair("Exposure Mode", format(exif.exposureMode)))
        list.add(Pair("Exposure Program", format(exif.exposureProgram)))
        list.add(Pair("Exposure Time", format(exif.exposureTime)))
        list.add(Pair("F Number", format(exif.fNumber)))
        list.add(Pair("Flash", format(exif.flash)))
        list.add(Pair("Focal Length", formatMillimeters(exif.focalLength)))
        list.add(Pair("Focal Length In 35mm Format", formatMillimeters(exif.focalLengthIn35mmFormat)))
        list.add(Pair("Gain Control", format(exif.gainControl)))
        list.add(Pair("Gps Altitude", formatAltitude(exif.gpsAltitude)))
        list.add(Pair("Gps Date Stamp", format(exif.gpsDateStamp)))
        list.add(Pair("Gps Direction", format(exif.gpsDirection)))
        list.add(Pair("Gps Latitude", formatLatitude(exif.gpsLatitude)))
        list.add(Pair("Gps Longitude", formatLongitude(exif.gpsLongitude)))
        list.add(Pair("Gps Measure Mode", format(exif.gpsMeasureMode)))
        list.add(Pair("Gps Satellites", format(exif.gpsSatellites)))
        list.add(Pair("Gps Status", format(exif.gpsStatus)))
        list.add(Pair("Gps Version Id", format(exif.gpsVersionId)))
        list.add(Pair("Iso", format(exif.iso)))
        list.add(Pair("Light Source", format(exif.lightSource)))
        list.add(Pair("Make", format(exif.make)))
        list.add(Pair("Metering Mode", format(exif.meteringMode)))
        list.add(Pair("Model", format(exif.model)))
        list.add(Pair("Orientation", format(exif.orientation)))
        list.add(Pair("Saturation", format(exif.saturation)))
        list.add(Pair("Scene Capture Type", format(exif.sceneCaptureType)))
        list.add(Pair("Scene Type", format(exif.sceneType)))
        list.add(Pair("Sensing Method", format(exif.sensingMethod)))
        list.add(Pair("Sharpness", format(exif.sharpness)))

        // nikon
        list.add(Pair("Auto Focus Area Mode", format(exif.autoFocusAreaMode)))
        list.add(Pair("Auto Focus Point", format(exif.autoFocusPoint)))
        list.add(Pair("Active D Lighting", format(exif.activeDLighting)))
        list.add(Pair("Colorspace", format(exif.colorspace)))
        list.add(Pair("Exposure Difference", formatFourDecimals(exif.exposureDifference)))
        list.add(Pair("Flash Color Filter", format(exif.flashColorFilter)))
        list.add(Pair("Flash Compensation", format(exif.flashCompensation)))
        list.add(Pair("Flash Control Mode", format(exif.flashControlMode)))
        list.add(Pair("Flash Exposure Compensation", format(exif.flashExposureCompensation)))
        list.add(Pair("Flash Focal Length", formatMillimeters(exif.flashFocalLength)))
        list.add(Pair("Flash Mode", format(exif.flashMode)))
        list.add(Pair("Flash Setting", format(exif.flashSetting)))
        list.add(Pair("Flash Type", format(exif.flashType)))
        list.add(Pair("Focus Distance", formatMeters(exif.focusDistance)))
        list.add(Pair("Focus Mode", format(exif.focusMode)))
        list.add(Pair("Focus Position", format(exif.focusPosition)))
        list.add(Pair("High Iso Noise Reduction", format(exif.highIsoNoiseReduction)))
        list.add(Pair("Hue Adjustment", format(exif.hueAdjustment)))
        list.add(Pair("Noise Reduction", format(exif.noiseReduction)))
        list.add(Pair("Picture Control Name", format(exif.pictureControlName)))
        list.add(Pair("Primary AF Point", format(exif.primaryAFPoint)))
        list.add(Pair("VR Mode", format(exif.vrMode)))
        list.add(Pair("Vibration Reduction", format(exif.vibrationReduction)))
        list.add(Pair("Vignette Control", format(exif.vignetteControl)))
        list.add(Pair("White Balance", format(exif.whiteBalance)))

        // composite
        list.add(Pair("Aperture", format(exif.aperture)))
        list.add(Pair("Auto Focus", format(exif.autoFocus)))
        list.add(Pair("Depth Of Field", format(exif.depthOfField)))
        list.add(Pair("Field Of View", format(exif.fieldOfView)))
        list.add(Pair("Hyperfocal Distance", formatMeters(exif.hyperfocalDistance)))
        list.add(Pair("Lens Id", format(exif.lensId)))
        list.add(Pair("Light Value", formatFourDecimals(exif.lightValue)))
        list.add(Pair("Scale Factor 35 Efl", formatOneDecimal(exif.scaleFactor35Efl)))
        list.add(Pair("Shutter Speed", format(exif.shutterSpeed)))

        return list.toImmutableList()
    }

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
