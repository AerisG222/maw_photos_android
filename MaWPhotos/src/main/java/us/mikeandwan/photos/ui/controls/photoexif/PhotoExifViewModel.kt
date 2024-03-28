package us.mikeandwan.photos.ui.controls.photoexif

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import okhttp3.internal.toImmutableList
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.PhotoRepository
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.PhotoExifData
import javax.inject.Inject

@HiltViewModel
class PhotoExifViewModel @Inject constructor (
    activeIdRepository: ActiveIdRepository,
    photoRepository: PhotoRepository
): ViewModel() {
    val exifData = activeIdRepository
        .getActivePhotoId()
        .filter { it != null }
        .flatMapLatest { photoRepository.getExifData(it!!) }
        .filter { it is ExternalCallStatus.Success }
        .map { it as ExternalCallStatus.Success }
        .map { prepareForDisplay(it.result) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private fun prepareForDisplay(exif: PhotoExifData): List<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()

        // exif
        list.add(Pair("Bits Per Sample", ExifDataFormatter.format(exif.bitsPerSample)))
        list.add(Pair("Compression", ExifDataFormatter.format(exif.compression)))
        list.add(Pair("Contrast", ExifDataFormatter.format(exif.contrast)))
        list.add(Pair("Create Date", ExifDataFormatter.format(exif.createDate)))
        list.add(Pair("Digital Zoom Ratio", ExifDataFormatter.format(exif.digitalZoomRatio)))
        list.add(Pair("Exposure Compensation", ExifDataFormatter.format(exif.exposureCompensation)))
        list.add(Pair("Exposure Mode", ExifDataFormatter.format(exif.exposureMode)))
        list.add(Pair("Exposure Program", ExifDataFormatter.format(exif.exposureProgram)))
        list.add(Pair("Exposure Time", ExifDataFormatter.format(exif.exposureTime)))
        list.add(Pair("F Number", ExifDataFormatter.format(exif.fNumber)))
        list.add(Pair("Flash", ExifDataFormatter.format(exif.flash)))
        list.add(Pair("Focal Length", ExifDataFormatter.formatMillimeters(exif.focalLength)))
        list.add(Pair("Focal Length In 35mm Format", ExifDataFormatter.formatMillimeters(exif.focalLengthIn35mmFormat)))
        list.add(Pair("Gain Control", ExifDataFormatter.format(exif.gainControl)))
        list.add(Pair("Gps Altitude", ExifDataFormatter.formatAltitude(exif.gpsAltitude)))
        list.add(Pair("Gps Date Stamp", ExifDataFormatter.format(exif.gpsDateStamp)))
        list.add(Pair("Gps Direction", ExifDataFormatter.format(exif.gpsDirection)))
        list.add(Pair("Gps Latitude", ExifDataFormatter.formatLatitude(exif.gpsLatitude)))
        list.add(Pair("Gps Longitude", ExifDataFormatter.formatLongitude(exif.gpsLongitude)))
        list.add(Pair("Gps Measure Mode", ExifDataFormatter.format(exif.gpsMeasureMode)))
        list.add(Pair("Gps Satellites", ExifDataFormatter.format(exif.gpsSatellites)))
        list.add(Pair("Gps Status", ExifDataFormatter.format(exif.gpsStatus)))
        list.add(Pair("Gps Version Id", ExifDataFormatter.format(exif.gpsVersionId)))
        list.add(Pair("Iso", ExifDataFormatter.format(exif.iso)))
        list.add(Pair("Light Source", ExifDataFormatter.format(exif.lightSource)))
        list.add(Pair("Make", ExifDataFormatter.format(exif.make)))
        list.add(Pair("Metering Mode", ExifDataFormatter.format(exif.meteringMode)))
        list.add(Pair("Model", ExifDataFormatter.format(exif.model)))
        list.add(Pair("Orientation", ExifDataFormatter.format(exif.orientation)))
        list.add(Pair("Saturation", ExifDataFormatter.format(exif.saturation)))
        list.add(Pair("Scene Capture Type", ExifDataFormatter.format(exif.sceneCaptureType)))
        list.add(Pair("Scene Type", ExifDataFormatter.format(exif.sceneType)))
        list.add(Pair("Sensing Method", ExifDataFormatter.format(exif.sensingMethod)))
        list.add(Pair("Sharpness", ExifDataFormatter.format(exif.sharpness)))

        // nikon
        list.add(Pair("Auto Focus Area Mode", ExifDataFormatter.format(exif.autoFocusAreaMode)))
        list.add(Pair("Auto Focus Point", ExifDataFormatter.format(exif.autoFocusPoint)))
        list.add(Pair("Active D Lighting", ExifDataFormatter.format(exif.activeDLighting)))
        list.add(Pair("Colorspace", ExifDataFormatter.format(exif.colorspace)))
        list.add(Pair("Exposure Difference", ExifDataFormatter.formatFourDecimals(exif.exposureDifference)))
        list.add(Pair("Flash Color Filter", ExifDataFormatter.format(exif.flashColorFilter)))
        list.add(Pair("Flash Compensation", ExifDataFormatter.format(exif.flashCompensation)))
        list.add(Pair("Flash Control Mode", ExifDataFormatter.format(exif.flashControlMode)))
        list.add(Pair("Flash Exposure Compensation", ExifDataFormatter.format(exif.flashExposureCompensation)))
        list.add(Pair("Flash Focal Length", ExifDataFormatter.formatMillimeters(exif.flashFocalLength)))
        list.add(Pair("Flash Mode", ExifDataFormatter.format(exif.flashMode)))
        list.add(Pair("Flash Setting", ExifDataFormatter.format(exif.flashSetting)))
        list.add(Pair("Flash Type", ExifDataFormatter.format(exif.flashType)))
        list.add(Pair("Focus Distance", ExifDataFormatter.formatMeters(exif.focusDistance)))
        list.add(Pair("Focus Mode", ExifDataFormatter.format(exif.focusMode)))
        list.add(Pair("Focus Position", ExifDataFormatter.format(exif.focusPosition)))
        list.add(Pair("High Iso Noise Reduction", ExifDataFormatter.format(exif.highIsoNoiseReduction)))
        list.add(Pair("Hue Adjustment", ExifDataFormatter.format(exif.hueAdjustment)))
        list.add(Pair("Noise Reduction", ExifDataFormatter.format(exif.noiseReduction)))
        list.add(Pair("Picture Control Name", ExifDataFormatter.format(exif.pictureControlName)))
        list.add(Pair("Primary AF Point", ExifDataFormatter.format(exif.primaryAFPoint)))
        list.add(Pair("VR Mode", ExifDataFormatter.format(exif.vrMode)))
        list.add(Pair("Vibration Reduction", ExifDataFormatter.format(exif.vibrationReduction)))
        list.add(Pair("Vignette Control", ExifDataFormatter.format(exif.vignetteControl)))
        list.add(Pair("White Balance", ExifDataFormatter.format(exif.whiteBalance)))

        // composite
        list.add(Pair("Aperture", ExifDataFormatter.format(exif.aperture)))
        list.add(Pair("Auto Focus", ExifDataFormatter.format(exif.autoFocus)))
        list.add(Pair("Depth Of Field", ExifDataFormatter.format(exif.depthOfField)))
        list.add(Pair("Field Of View", ExifDataFormatter.format(exif.fieldOfView)))
        list.add(Pair("Hyperfocal Distance", ExifDataFormatter.formatMeters(exif.hyperfocalDistance)))
        list.add(Pair("Lens Id", ExifDataFormatter.format(exif.lensId)))
        list.add(Pair("Light Value", ExifDataFormatter.formatFourDecimals(exif.lightValue)))
        list.add(Pair("Scale Factor 35 Efl", ExifDataFormatter.formatOneDecimal(exif.scaleFactor35Efl)))
        list.add(Pair("Shutter Speed", ExifDataFormatter.format(exif.shutterSpeed)))
        
        return list.toImmutableList()
    }
}