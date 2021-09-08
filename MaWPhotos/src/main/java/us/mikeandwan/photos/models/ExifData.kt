package us.mikeandwan.photos.models

import us.mikeandwan.photos.models.MultimediaAsset
import okhttp3.ResponseBody
import us.mikeandwan.photos.models.FileLocation
import com.fasterxml.jackson.annotation.JsonFormat
import us.mikeandwan.photos.models.UploadedFile
import java.util.*

class ExifData {
    // exif
    var bitsPerSample: Short? = null
    var compression: String? = null
    var contrast: String? = null
    var createDate: Date? = null
    var digitalZoomRatio: Double? = null
    var exposureCompensation: String? = null
    var exposureMode: String? = null
    var exposureProgram: String? = null
    var exposureTime: String? = null
    var fNumber: Double? = null
    var flash: String? = null
    var focalLength: Double? = null
    var focalLengthIn35mmFormat: Double? = null
    var gainControl: String? = null
    var gpsAltitude: Double? = null
    var gpsAltitudeRef: String? = null
    var gpsDateStamp: Date? = null
    var gpsDirection: Double? = null
    var gpsDirectionRef: String? = null
    var gpsLatitude: Double? = null
    var gpsLatitudeRef: String? = null
    var gpsLongitude: Double? = null
    var gpsLongitudeRef: String? = null
    var gpsMeasureMode: String? = null
    var gpsSatellites: String? = null
    var gpsStatus: String? = null
    var gpsVersionId: String? = null
    var iso: Int? = null
    var lightSource: String? = null
    var make: String? = null
    var meteringMode: String? = null
    var model: String? = null
    var orientation: String? = null
    var saturation: String? = null
    var sceneCaptureType: String? = null
    var sceneType: String? = null
    var sensingMethod: String? = null
    var sharpness: String? = null

    // nikon
    var autoFocusAreaMode: String? = null
    var autoFocusPoint: String? = null
    var activeDLighting: String? = null
    var colorspace: String? = null
    var exposureDifference: Double? = null
    var flashColorFilter: String? = null
    var flashCompensation: String? = null
    var flashControlMode: Short? = null
    var flashExposureCompensation: String? = null
    var flashFocalLength: Double? = null
    var flashMode: String? = null
    var flashSetting: String? = null
    var flashType: String? = null
    var focusDistance: Double? = null
    var focusMode: String? = null
    var focusPosition: Int? = null
    var highIsoNoiseReduction: String? = null
    var hueAdjustment: String? = null
    var noiseReduction: String? = null
    var pictureControlName: String? = null
    var primaryAFPoint: String? = null
    var vrMode: String? = null
    var vibrationReduction: String? = null
    var vignetteControl: String? = null
    var whiteBalance: String? = null

    // composite
    var aperture: Double? = null
    var autoFocus: String? = null
    var depthOfField: String? = null
    var fieldOfView: String? = null
    var hyperfocalDistance: Double? = null
    var lensId: String? = null
    var lightValue: Double? = null
    var scaleFactor35Efl: Double? = null
    var shutterSpeed: String? = null
}