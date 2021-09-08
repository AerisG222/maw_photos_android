package us.mikeandwan.photos.ui.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.DialogExifBinding
import us.mikeandwan.photos.models.ExifData
import us.mikeandwan.photos.services.DataServices
import us.mikeandwan.photos.ui.photos.ExifDataFormatter.format
import us.mikeandwan.photos.ui.photos.ExifDataFormatter.formatAltitude
import us.mikeandwan.photos.ui.photos.ExifDataFormatter.formatFourDecimals
import us.mikeandwan.photos.ui.photos.ExifDataFormatter.formatLatitude
import us.mikeandwan.photos.ui.photos.ExifDataFormatter.formatLongitude
import us.mikeandwan.photos.ui.photos.ExifDataFormatter.formatMeters
import us.mikeandwan.photos.ui.photos.ExifDataFormatter.formatMillimeters
import us.mikeandwan.photos.ui.photos.ExifDataFormatter.formatOneDecimal
import javax.inject.Inject

@AndroidEntryPoint
class ExifDialogFragment : BasePhotoDialogFragment() {
    private val _disposables = CompositeDisposable()
    private var _binding: DialogExifBinding? = null
    private val binding get() = _binding!!
    private var _2dp = 0
    private var _4dp = 0
    private var _8dp = 0

    @JvmField
    @Inject
    var _dataServices: DataServices? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogExifBinding.inflate(inflater, container, false)

        _2dp = resources.getDimension(R.dimen._2dp).toInt()
        _4dp = resources.getDimension(R.dimen._4dp).toInt()
        _8dp = resources.getDimension(R.dimen._8dp).toInt()

        requireDialog().setTitle("Exif Data")

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        exifData
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _disposables.clear() // do not send event after activity has been destroyed
    }

    private fun displayExifData(exif: ExifData?) {
        if (exif != null) {
            // exif
            addExifRow("Bits Per Sample", format(exif.bitsPerSample))
            addExifRow("Compression", format(exif.compression))
            addExifRow("Contrast", format(exif.contrast))
            addExifRow("Create Date", format(exif.createDate))
            addExifRow("Digital Zoom Ratio", format(exif.digitalZoomRatio))
            addExifRow("Exposure Compensation", format(exif.exposureCompensation))
            addExifRow("Exposure Mode", format(exif.exposureMode))
            addExifRow("Exposure Program", format(exif.exposureProgram))
            addExifRow("Exposure Time", format(exif.exposureTime))
            addExifRow("F Number", format(exif.fNumber))
            addExifRow("Flash", format(exif.flash))
            addExifRow("Focal Length", formatMillimeters(exif.focalLength))
            addExifRow(
                "Focal Length In 35mm Format",
                formatMillimeters(exif.focalLengthIn35mmFormat)
            )
            addExifRow("Gain Control", format(exif.gainControl))
            addExifRow("Gps Altitude", formatAltitude(exif.gpsAltitude))
            addExifRow("Gps Date Stamp", format(exif.gpsDateStamp))
            addExifRow("Gps Direction", format(exif.gpsDirection))
            addExifRow("Gps Latitude", formatLatitude(exif.gpsLatitude))
            addExifRow("Gps Longitude", formatLongitude(exif.gpsLongitude))
            addExifRow("Gps Measure Mode", format(exif.gpsMeasureMode))
            addExifRow("Gps Satellites", format(exif.gpsSatellites))
            addExifRow("Gps Status", format(exif.gpsStatus))
            addExifRow("Gps Version Id", format(exif.gpsVersionId))
            addExifRow("Iso", format(exif.iso))
            addExifRow("Light Source", format(exif.lightSource))
            addExifRow("Make", format(exif.make))
            addExifRow("Metering Mode", format(exif.meteringMode))
            addExifRow("Model", format(exif.model))
            addExifRow("Orientation", format(exif.orientation))
            addExifRow("Saturation", format(exif.saturation))
            addExifRow("Scene Capture Type", format(exif.sceneCaptureType))
            addExifRow("Scene Type", format(exif.sceneType))
            addExifRow("Sensing Method", format(exif.sensingMethod))
            addExifRow("Sharpness", format(exif.sharpness))

            // nikon
            addExifRow("Auto Focus Area Mode", format(exif.autoFocusAreaMode))
            addExifRow("Auto Focus Point", format(exif.autoFocusPoint))
            addExifRow("Active D Lighting", format(exif.activeDLighting))
            addExifRow("Colorspace", format(exif.colorspace))
            addExifRow("Exposure Difference", formatFourDecimals(exif.exposureDifference))
            addExifRow("Flash Color Filter", format(exif.flashColorFilter))
            addExifRow("Flash Compensation", format(exif.flashCompensation))
            addExifRow("Flash Control Mode", format(exif.flashControlMode))
            addExifRow("Flash Exposure Compensation", format(exif.flashExposureCompensation))
            addExifRow("Flash Focal Length", formatMillimeters(exif.flashFocalLength))
            addExifRow("Flash Mode", format(exif.flashMode))
            addExifRow("Flash Setting", format(exif.flashSetting))
            addExifRow("Flash Type", format(exif.flashType))
            addExifRow("Focus Distance", formatMeters(exif.focusDistance))
            addExifRow("Focus Mode", format(exif.focusMode))
            addExifRow("Focus Position", format(exif.focusPosition))
            addExifRow("High Iso Noise Reduction", format(exif.highIsoNoiseReduction))
            addExifRow("Hue Adjustment", format(exif.hueAdjustment))
            addExifRow("Noise Reduction", format(exif.noiseReduction))
            addExifRow("Picture Control Name", format(exif.pictureControlName))
            addExifRow("Primary AF Point", format(exif.primaryAFPoint))
            addExifRow("VR Mode", format(exif.vrMode))
            addExifRow("Vibration Reduction", format(exif.vibrationReduction))
            addExifRow("Vignette Control", format(exif.vignetteControl))
            addExifRow("White Balance", format(exif.whiteBalance))

            // composite
            addExifRow("Aperture", format(exif.aperture))
            addExifRow("Auto Focus", format(exif.autoFocus))
            addExifRow("Depth Of Field", format(exif.depthOfField))
            addExifRow("Field Of View", format(exif.fieldOfView))
            addExifRow("Hyperfocal Distance", formatMeters(exif.hyperfocalDistance))
            addExifRow("Lens Id", format(exif.lensId))
            addExifRow("Light Value", formatFourDecimals(exif.lightValue))
            addExifRow("Scale Factor 35 Efl", formatOneDecimal(exif.scaleFactor35Efl))
            addExifRow("Shutter Speed", format(exif.shutterSpeed))
        }
    }

    private val exifData: Unit
        private get() {
            _disposables.add(Flowable.fromCallable {
                addWork()
                _dataServices!!.getExifData(currentPhoto.id)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { x: ExifData? ->
                        removeWork()
                        displayExifData(x)
                    }
                ) { ex: Throwable? ->
                    removeWork()
                    photoActivity.onApiException(ex)
                }
            )
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