package us.mikeandwan.photos.fragments;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;

import java.util.concurrent.ExecutionException;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.activities.LoginActivity_;
import us.mikeandwan.photos.data.ExifData;
import us.mikeandwan.photos.data.ExifDataFormatter;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.tasks.BackgroundTaskExecutor;
import us.mikeandwan.photos.tasks.GetExifDataBackgroundTask;


@EFragment(R.layout.dialog_exif)
public class ExifDialogFragment extends BasePhotoDialogFragment {
    @DimensionPixelSizeRes(R.dimen._2dp)
    int _2dp;

    @DimensionPixelSizeRes(R.dimen._4dp)
    int _4dp;

    @DimensionPixelSizeRes(R.dimen._8dp)
    int _8dp;

    @ViewById(R.id.exifView)
    protected TableLayout _exifView;


    @AfterViews
    protected void afterViews() {
        getDialog().setTitle("Exif Data");
    }


    @Override
    public void onResume() {
        getExifData();

        super.onResume();
    }


    private void displayExifData(ExifData exif) {
        if (exif != null) {
            addExifRow("AF Point", ExifDataFormatter.format(exif.getAfPoint()), true);
            addExifRow("Aperture", ExifDataFormatter.format(exif.getAperture()), false);
            addExifRow("Contrast", ExifDataFormatter.format(exif.getContrast()), true);
            addExifRow("Depth of Field", ExifDataFormatter.format(exif.getDepthOfField()), false);
            addExifRow("Digital Zoom Ratio", ExifDataFormatter.formatOneDecimal(exif.getDigitalZoomRation()), true);
            addExifRow("Exposure Compensation", ExifDataFormatter.format(exif.getExposureCompensation()), false);
            addExifRow("Exposure Difference", ExifDataFormatter.format(exif.getExposureDifference()), true);
            addExifRow("Exposure Mode", ExifDataFormatter.format(exif.getExposureMode()), false);
            addExifRow("Exposure Time", ExifDataFormatter.formatInvertTime(exif.getExposureTime()), true);
            addExifRow("F Number", ExifDataFormatter.format(exif.getfNumber()), false);
            addExifRow("Flash", ExifDataFormatter.format(exif.getFlash()), true);
            addExifRow("Flash Exposure Compensation", ExifDataFormatter.format(exif.getFlashExposureCompensation()), false);
            addExifRow("Flash Mode", ExifDataFormatter.format(exif.getFlashMode()), true);
            addExifRow("Flash Setting", ExifDataFormatter.format(exif.getFlashSetting()), false);
            addExifRow("Flash Type", ExifDataFormatter.format(exif.getFlashType()), true);
            addExifRow("Focal Length", ExifDataFormatter.formatFocalLengthOneDecimal(exif.getFocalLength()), false);
            addExifRow("Focal Length in 35mm Format", ExifDataFormatter.formatFocalLengthNoDecimals(exif.getFocalLengthIn35mmFormat()), true);
            addExifRow("Focus Distance", ExifDataFormatter.formatDistance(exif.getFocusDistance()), false);
            addExifRow("Focus Mode", ExifDataFormatter.format(exif.getFocusMode()), true);
            addExifRow("Focus Position", ExifDataFormatter.format(exif.getFocusPosition()), false);
            addExifRow("Gain Control", ExifDataFormatter.format(exif.getGainControl()), true);
            addExifRow("Hue Adjustment", ExifDataFormatter.format(exif.getHueAdjustment()), false);
            addExifRow("Hyperfocal Distance", ExifDataFormatter.formatDistance(exif.getHyperFocalDistance()), true);
            addExifRow("ISO", ExifDataFormatter.format(exif.getIso()), false);
            addExifRow("Lens ID", ExifDataFormatter.format(exif.getLensId()), true);
            addExifRow("Light Source", ExifDataFormatter.format(exif.getLightSource()), false);
            addExifRow("Make", ExifDataFormatter.format(exif.getMake()), true);
            addExifRow("Metering Mode", ExifDataFormatter.format(exif.getMeteringMode()), false);
            addExifRow("Model", ExifDataFormatter.format(exif.getModel()), true);
            addExifRow("Noise Reduction", ExifDataFormatter.format(exif.getNoiseReduction()), false);
            addExifRow("Orientation", ExifDataFormatter.format(exif.getOrientation()), true);
            addExifRow("Saturation", ExifDataFormatter.format(exif.getSaturation()), false);
            addExifRow("Scale Factor 35 EFL", ExifDataFormatter.formatOneDecimalMm(exif.getScaleFactor35Efl()), true);
            addExifRow("Scene Capture Type", ExifDataFormatter.format(exif.getSceneCaptureType()), false);
            addExifRow("Scene Type", ExifDataFormatter.format(exif.getSceneType()), true);
            addExifRow("Sensing Method", ExifDataFormatter.format(exif.getSensingMethod()), false);
            addExifRow("Sharpness", ExifDataFormatter.format(exif.getSharpness()), true);
            addExifRow("Shutter Speed", ExifDataFormatter.formatInvertTime(exif.getShutterSpeed()), false);
            addExifRow("White Balance", ExifDataFormatter.format(exif.getWhiteBalance()), true);
            addExifRow("Shot Taken Date", ExifDataFormatter.format(exif.getShotTakenDate()), false);
            addExifRow("Exposure Program", ExifDataFormatter.format(exif.getExposureProgram()), true);
            addExifRow("GPS Version ID", ExifDataFormatter.format(exif.getGpsVersionId()), false);
            addExifRow("GPS Latitude", ExifDataFormatter.formatLatitude(exif.getGpsLatitude()), true);
            addExifRow("GPS Longitude", ExifDataFormatter.formatLongitude(exif.getGpsLongitude()), false);
            addExifRow("GPS Altitude", ExifDataFormatter.formatAltitude(exif.getGpsAltitude()), true);
            addExifRow("GPS Time Stamp", ExifDataFormatter.format(exif.getGpsTime()), false);
            addExifRow("GPS Satellites", ExifDataFormatter.format(exif.getGpsSatellites()), true);
        }

        updateProgress();
    }


    private void getExifData() {
        GetExifDataBackgroundTask task = new GetExifDataBackgroundTask(getContext(), getCurrentPhoto().getId()) {
            @Override
            protected void postExecuteTask(ExifData exif) {
                displayExifData(exif);
            }

            @Override
            protected void handleException(ExecutionException ex) {
                Log.e(MawApplication.LOG_TAG, "exception getting the exif data: " + ex.getMessage());

                if (ex.getCause() instanceof MawAuthenticationException) {
                    startActivity(new Intent(getContext(), LoginActivity_.class));
                }
            }
        };

        BackgroundTaskExecutor.getInstance().enqueueTask(task);
        updateProgress();
    }


    private void addExifRow(String name, String value, boolean isOdd) {
        Context ctx = getContext();
        TableRow row = new TableRow(ctx);

        _exifView.addView(row);

        if (isOdd) {
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
