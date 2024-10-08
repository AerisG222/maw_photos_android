package us.mikeandwan.photos.domain.models

import java.util.*

data class ExifData(
    // exif
    val bitsPerSample: Short?,
    val compression: String?,
    val contrast: String?,
    val createDate: Date?,
    val digitalZoomRatio: Double?,
    val exposureCompensation: String?,
    val exposureMode: String?,
    val exposureProgram: String?,
    val exposureTime: String?,
    val fNumber: Double?,
    val flash: String?,
    val focalLength: Double?,
    val focalLengthIn35mmFormat: Double?,
    val gainControl: String?,
    val gpsAltitude: Double?,
    val gpsAltitudeRef: String?,
    val gpsDateStamp: Date?,
    val gpsDirection: Double?,
    val gpsDirectionRef: String?,
    val gpsLatitude: Double?,
    val gpsLatitudeRef: String?,
    val gpsLongitude: Double?,
    val gpsLongitudeRef: String?,
    val gpsMeasureMode: String?,
    val gpsSatellites: String?,
    val gpsStatus: String?,
    val gpsVersionId: String?,
    val iso: Int?,
    val lightSource: String?,
    val make: String?,
    val meteringMode: String?,
    val model: String?,
    val orientation: String?,
    val saturation: String?,
    val sceneCaptureType: String?,
    val sceneType: String?,
    val sensingMethod: String?,
    val sharpness: String?,

    // nikon
    val autoFocusAreaMode: String?,
    val autoFocusPoint: String?,
    val activeDLighting: String?,
    val colorspace: String?,
    val exposureDifference: Double?,
    val flashColorFilter: String?,
    val flashCompensation: String?,
    val flashControlMode: Short?,
    val flashExposureCompensation: String?,
    val flashFocalLength: Double?,
    val flashMode: String?,
    val flashSetting: String?,
    val flashType: String?,
    val focusDistance: Double?,
    val focusMode: String?,
    val focusPosition: Int?,
    val highIsoNoiseReduction: String?,
    val hueAdjustment: String?,
    val noiseReduction: String?,
    val pictureControlName: String?,
    val primaryAFPoint: String?,
    val vrMode: String?,
    val vibrationReduction: String?,
    val vignetteControl: String?,
    val whiteBalance: String?,

    // composite
    val aperture: Double?,
    val autoFocus: String?,
    val depthOfField: String?,
    val fieldOfView: String?,
    val hyperfocalDistance: Double?,
    val lensId: String?,
    val lightValue: Double?,
    val scaleFactor35Efl: Double?,
    val shutterSpeed: String?
)
