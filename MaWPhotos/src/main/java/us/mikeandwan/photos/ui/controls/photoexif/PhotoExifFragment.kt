package us.mikeandwan.photos.ui.controls.photoexif

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.databinding.FragmentPhotoExifBinding
import us.mikeandwan.photos.domain.models.PhotoExifData

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PhotoExifFragment : Fragment() {
    companion object {
        fun newInstance() = PhotoExifFragment()
    }

    private var _2dp = 0
    private var _4dp = 0
    private var _8dp = 0
    private lateinit var binding: FragmentPhotoExifBinding
    val viewModel by viewModels<PhotoExifViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoExifBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initStateObservers()

        return binding.root
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.exifData
                    .onEach {
                        displayExifData(it)
                    }
                    .launchIn(this)
            }
        }
    }

    private fun displayExifData(exif: PhotoExifData?) {
        if (exif != null) {
            // exif
            addExifRow("Bits Per Sample", ExifDataFormatter.format(exif.bitsPerSample))
            addExifRow("Compression", ExifDataFormatter.format(exif.compression))
            addExifRow("Contrast", ExifDataFormatter.format(exif.contrast))
            addExifRow("Create Date", ExifDataFormatter.format(exif.createDate))
            addExifRow("Digital Zoom Ratio", ExifDataFormatter.format(exif.digitalZoomRatio))
            addExifRow("Exposure Compensation", ExifDataFormatter.format(exif.exposureCompensation))
            addExifRow("Exposure Mode", ExifDataFormatter.format(exif.exposureMode))
            addExifRow("Exposure Program", ExifDataFormatter.format(exif.exposureProgram))
            addExifRow("Exposure Time", ExifDataFormatter.format(exif.exposureTime))
            addExifRow("F Number", ExifDataFormatter.format(exif.fNumber))
            addExifRow("Flash", ExifDataFormatter.format(exif.flash))
            addExifRow("Focal Length", ExifDataFormatter.formatMillimeters(exif.focalLength))
            addExifRow("Focal Length In 35mm Format", ExifDataFormatter.formatMillimeters(exif.focalLengthIn35mmFormat))
            addExifRow("Gain Control", ExifDataFormatter.format(exif.gainControl))
            addExifRow("Gps Altitude", ExifDataFormatter.formatAltitude(exif.gpsAltitude))
            addExifRow("Gps Date Stamp", ExifDataFormatter.format(exif.gpsDateStamp))
            addExifRow("Gps Direction", ExifDataFormatter.format(exif.gpsDirection))
            addExifRow("Gps Latitude", ExifDataFormatter.formatLatitude(exif.gpsLatitude))
            addExifRow("Gps Longitude", ExifDataFormatter.formatLongitude(exif.gpsLongitude))
            addExifRow("Gps Measure Mode", ExifDataFormatter.format(exif.gpsMeasureMode))
            addExifRow("Gps Satellites", ExifDataFormatter.format(exif.gpsSatellites))
            addExifRow("Gps Status", ExifDataFormatter.format(exif.gpsStatus))
            addExifRow("Gps Version Id", ExifDataFormatter.format(exif.gpsVersionId))
            addExifRow("Iso", ExifDataFormatter.format(exif.iso))
            addExifRow("Light Source", ExifDataFormatter.format(exif.lightSource))
            addExifRow("Make", ExifDataFormatter.format(exif.make))
            addExifRow("Metering Mode", ExifDataFormatter.format(exif.meteringMode))
            addExifRow("Model", ExifDataFormatter.format(exif.model))
            addExifRow("Orientation", ExifDataFormatter.format(exif.orientation))
            addExifRow("Saturation", ExifDataFormatter.format(exif.saturation))
            addExifRow("Scene Capture Type", ExifDataFormatter.format(exif.sceneCaptureType))
            addExifRow("Scene Type", ExifDataFormatter.format(exif.sceneType))
            addExifRow("Sensing Method", ExifDataFormatter.format(exif.sensingMethod))
            addExifRow("Sharpness", ExifDataFormatter.format(exif.sharpness))

            // nikon
            addExifRow("Auto Focus Area Mode", ExifDataFormatter.format(exif.autoFocusAreaMode))
            addExifRow("Auto Focus Point", ExifDataFormatter.format(exif.autoFocusPoint))
            addExifRow("Active D Lighting", ExifDataFormatter.format(exif.activeDLighting))
            addExifRow("Colorspace", ExifDataFormatter.format(exif.colorspace))
            addExifRow("Exposure Difference", ExifDataFormatter.formatFourDecimals(exif.exposureDifference))
            addExifRow("Flash Color Filter", ExifDataFormatter.format(exif.flashColorFilter))
            addExifRow("Flash Compensation", ExifDataFormatter.format(exif.flashCompensation))
            addExifRow("Flash Control Mode", ExifDataFormatter.format(exif.flashControlMode))
            addExifRow("Flash Exposure Compensation", ExifDataFormatter.format(exif.flashExposureCompensation))
            addExifRow("Flash Focal Length", ExifDataFormatter.formatMillimeters(exif.flashFocalLength))
            addExifRow("Flash Mode", ExifDataFormatter.format(exif.flashMode))
            addExifRow("Flash Setting", ExifDataFormatter.format(exif.flashSetting))
            addExifRow("Flash Type", ExifDataFormatter.format(exif.flashType))
            addExifRow("Focus Distance", ExifDataFormatter.formatMeters(exif.focusDistance))
            addExifRow("Focus Mode", ExifDataFormatter.format(exif.focusMode))
            addExifRow("Focus Position", ExifDataFormatter.format(exif.focusPosition))
            addExifRow("High Iso Noise Reduction", ExifDataFormatter.format(exif.highIsoNoiseReduction))
            addExifRow("Hue Adjustment", ExifDataFormatter.format(exif.hueAdjustment))
            addExifRow("Noise Reduction", ExifDataFormatter.format(exif.noiseReduction))
            addExifRow("Picture Control Name", ExifDataFormatter.format(exif.pictureControlName))
            addExifRow("Primary AF Point", ExifDataFormatter.format(exif.primaryAFPoint))
            addExifRow("VR Mode", ExifDataFormatter.format(exif.vrMode))
            addExifRow("Vibration Reduction", ExifDataFormatter.format(exif.vibrationReduction))
            addExifRow("Vignette Control", ExifDataFormatter.format(exif.vignetteControl))
            addExifRow("White Balance", ExifDataFormatter.format(exif.whiteBalance))

            // composite
            addExifRow("Aperture", ExifDataFormatter.format(exif.aperture))
            addExifRow("Auto Focus", ExifDataFormatter.format(exif.autoFocus))
            addExifRow("Depth Of Field", ExifDataFormatter.format(exif.depthOfField))
            addExifRow("Field Of View", ExifDataFormatter.format(exif.fieldOfView))
            addExifRow("Hyperfocal Distance", ExifDataFormatter.formatMeters(exif.hyperfocalDistance))
            addExifRow("Lens Id", ExifDataFormatter.format(exif.lensId))
            addExifRow("Light Value", ExifDataFormatter.formatFourDecimals(exif.lightValue))
            addExifRow("Scale Factor 35 Efl", ExifDataFormatter.formatOneDecimal(exif.scaleFactor35Efl))
            addExifRow("Shutter Speed", ExifDataFormatter.format(exif.shutterSpeed))
        }
    }

    private fun addExifRow(name: String, value: String?) {
        val ctx = context
        val row = TableRow(ctx)

        binding.exifView.addView(row)

        if (binding.exifView.childCount % 2 == 1) {
            row.setBackgroundColor(-0xddddde)
        }

        val nameView = TextView(ctx)
        nameView.text = name
        nameView.setPadding(_4dp, _2dp, _4dp, _2dp)

        val valueView = TextView(ctx)
        valueView.text = value ?: "--"
        valueView.setPadding(_8dp, _2dp, _4dp, _2dp)

        row.addView(nameView)
        row.addView(valueView)
    }
}