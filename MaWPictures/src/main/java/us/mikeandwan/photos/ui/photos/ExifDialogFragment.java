package us.mikeandwan.photos.ui.photos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.models.ExifData;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.tasks.GetExifDataTask;


public class ExifDialogFragment extends BasePhotoDialogFragment {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private Unbinder _unbinder;

    @BindDimen(R.dimen._2dp) int _2dp;
    @BindDimen(R.dimen._4dp) int _4dp;
    @BindDimen(R.dimen._8dp) int _8dp;

    @BindView(R.id.exifView) TableLayout _exifView;

    @Inject GetExifDataTask _getExifDataTask;
    @Inject AuthenticationExceptionHandler _authHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_exif, container, false);
        _unbinder = ButterKnife.bind(this, view);

        getDialog().setTitle("Exif Data");

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.getComponent(TaskComponent.class).inject(this);
    }


    @Override
    public void onResume() {
        getExifData();

        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        _disposables.clear(); // do not send event after activity has been destroyed
        _unbinder.unbind();
    }


    private void displayExifData(ExifData exif) {
        if (exif != null) {
            // exif
            addExifRow("Bits Per Sample", ExifDataFormatter.format(exif.getBitsPerSample()));
            addExifRow("Compression", ExifDataFormatter.format(exif.getCompression()));
            addExifRow("Contrast", ExifDataFormatter.format(exif.getContrast()));
            addExifRow("Create Date", ExifDataFormatter.format(exif.getCreateDate()));
            addExifRow("Digital Zoom Ratio", ExifDataFormatter.format(exif.getDigitalZoomRatio()));
            addExifRow("Exposure Compensation", ExifDataFormatter.format(exif.getExposureCompensation()));
            addExifRow("Exposure Mode", ExifDataFormatter.format(exif.getExposureMode()));
            addExifRow("Exposure Program", ExifDataFormatter.format(exif.getExposureProgram()));
            addExifRow("Exposure Time", ExifDataFormatter.format(exif.getExposureTime()));
            addExifRow("F Number", ExifDataFormatter.format(exif.getfNumber()));
            addExifRow("Flash", ExifDataFormatter.format(exif.getFlash()));
            addExifRow("Focal Length", ExifDataFormatter.formatMillimeters(exif.getFocalLength()));
            addExifRow("Focal Length In 35mm Format", ExifDataFormatter.formatMillimeters(exif.getFocalLengthIn35mmFormat()));
            addExifRow("Gain Control", ExifDataFormatter.format(exif.getGainControl()));
            addExifRow("Gps Altitude", ExifDataFormatter.formatAltitude(exif.getGpsAltitude()));
            addExifRow("Gps Date Stamp", ExifDataFormatter.format(exif.getGpsDateStamp()));
            addExifRow("Gps Direction", ExifDataFormatter.format(exif.getGpsDirection()));
            addExifRow("Gps Latitude", ExifDataFormatter.formatLatitude(exif.getGpsLatitude()));
            addExifRow("Gps Longitude", ExifDataFormatter.formatLongitude(exif.getGpsLongitude()));
            addExifRow("Gps Measure Mode", ExifDataFormatter.format(exif.getGpsMeasureMode()));
            addExifRow("Gps Satellites", ExifDataFormatter.format(exif.getGpsSatellites()));
            addExifRow("Gps Status", ExifDataFormatter.format(exif.getGpsStatus()));
            addExifRow("Gps Version Id", ExifDataFormatter.format(exif.getGpsVersionId()));
            addExifRow("Iso", ExifDataFormatter.format(exif.getIso()));
            addExifRow("Light Source", ExifDataFormatter.format(exif.getLightSource()));
            addExifRow("Make", ExifDataFormatter.format(exif.getMake()));
            addExifRow("Metering Mode", ExifDataFormatter.format(exif.getMeteringMode()));
            addExifRow("Model", ExifDataFormatter.format(exif.getModel()));
            addExifRow("Orientation", ExifDataFormatter.format(exif.getOrientation()));
            addExifRow("Saturation", ExifDataFormatter.format(exif.getSaturation()));
            addExifRow("Scene Capture Type", ExifDataFormatter.format(exif.getSceneCaptureType()));
            addExifRow("Scene Type", ExifDataFormatter.format(exif.getSceneType()));
            addExifRow("Sensing Method", ExifDataFormatter.format(exif.getSensingMethod()));
            addExifRow("Sharpness", ExifDataFormatter.format(exif.getSharpness()));

            // nikon
            addExifRow("Auto Focus Area Mode", ExifDataFormatter.format(exif.getAutoFocusAreaMode()));
            addExifRow("Auto Focus Point", ExifDataFormatter.format(exif.getAutoFocusPoint()));
            addExifRow("Active D Lighting", ExifDataFormatter.format(exif.getActiveDLighting()));
            addExifRow("Colorspace", ExifDataFormatter.format(exif.getColorspace()));
            addExifRow("Exposure Difference", ExifDataFormatter.formatFourDecimals(exif.getExposureDifference()));
            addExifRow("Flash Color Filter", ExifDataFormatter.format(exif.getFlashColorFilter()));
            addExifRow("Flash Compensation", ExifDataFormatter.format(exif.getFlashCompensation()));
            addExifRow("Flash Control Mode", ExifDataFormatter.format(exif.getFlashControlMode()));
            addExifRow("Flash Exposure Compensation", ExifDataFormatter.format(exif.getFlashExposureCompensation()));
            addExifRow("Flash Focal Length", ExifDataFormatter.formatMillimeters(exif.getFlashFocalLength()));
            addExifRow("Flash Mode", ExifDataFormatter.format(exif.getFlashMode()));
            addExifRow("Flash Setting", ExifDataFormatter.format(exif.getFlashSetting()));
            addExifRow("Flash Type", ExifDataFormatter.format(exif.getFlashType()));
            addExifRow("Focus Distance", ExifDataFormatter.formatMeters(exif.getFocusDistance()));
            addExifRow("Focus Mode", ExifDataFormatter.format(exif.getFocusMode()));
            addExifRow("Focus Position", ExifDataFormatter.format(exif.getFocusPosition()));
            addExifRow("High Iso Noise Reduction", ExifDataFormatter.format(exif.getHighIsoNoiseReduction()));
            addExifRow("Hue Adjustment", ExifDataFormatter.format(exif.getHueAdjustment()));
            addExifRow("Noise Reduction", ExifDataFormatter.format(exif.getNoiseReduction()));
            addExifRow("Picture Control Name", ExifDataFormatter.format(exif.getPictureControlName()));
            addExifRow("Primary AF Point", ExifDataFormatter.format(exif.getPrimaryAFPoint()));
            addExifRow("VR Mode", ExifDataFormatter.format(exif.getVRMode()));
            addExifRow("Vibration Reduction", ExifDataFormatter.format(exif.getVibrationReduction()));
            addExifRow("Vignette Control", ExifDataFormatter.format(exif.getVignetteControl()));
            addExifRow("White Balance", ExifDataFormatter.format(exif.getWhiteBalance()));

            // composite
            addExifRow("Aperture", ExifDataFormatter.format(exif.getAperture()));
            addExifRow("Auto Focus", ExifDataFormatter.format(exif.getAutoFocus()));
            addExifRow("Depth Of Field", ExifDataFormatter.format(exif.getDepthOfField()));
            addExifRow("Field Of View", ExifDataFormatter.format(exif.getFieldOfView()));
            addExifRow("Hyperfocal Distance", ExifDataFormatter.formatMeters(exif.getHyperfocalDistance()));
            addExifRow("Lens Id", ExifDataFormatter.format(exif.getLensId()));
            addExifRow("Light Value", ExifDataFormatter.formatFourDecimals(exif.getLightValue()));
            addExifRow("Scale Factor 35 Efl", ExifDataFormatter.formatOneDecimal(exif.getScaleFactor35Efl()));
            addExifRow("Shutter Speed", ExifDataFormatter.format(exif.getShutterSpeed()));

            // processing info
            addExifRow("Raw Conversion Mode", ExifDataFormatter.format(exif.getRawConversionMode()));
            addExifRow("Sigmoidal Contrast Adjustment", ExifDataFormatter.formatFourDecimals(exif.getSigmoidalContrastAdjustment()));
            addExifRow("Saturation Adjustment", ExifDataFormatter.formatFourDecimals(exif.getSaturationAdjustment()));
            addExifRow("Compression Quality", ExifDataFormatter.format(exif.getCompressionQuality()));
        }

        updateProgress();
    }


    private void getExifData() {
        _disposables.add(Flowable.fromCallable(() -> _getExifDataTask.call(getCurrentPhoto().getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> displayExifData(x),
                        ex -> _authHandler.handleException(ex)
                )
        );

        updateProgress();
    }


    private void addExifRow(String name, String value) {
        Context ctx = getContext();
        TableRow row = new TableRow(ctx);

        _exifView.addView(row);

        if (_exifView.getChildCount() % 2 == 1) {
            row.setBackgroundColor(0xFF222222);
        }

        TextView nameView = new TextView(ctx);
        nameView.setText(name);
        nameView.setPadding(_4dp, _2dp, _4dp, _2dp);

        TextView valueView = new TextView(ctx);
        valueView.setText(value == null ? "--" : value);
        valueView.setPadding(_8dp, _2dp, _4dp, _2dp);

        row.addView(nameView);
        row.addView(valueView);
    }
}
