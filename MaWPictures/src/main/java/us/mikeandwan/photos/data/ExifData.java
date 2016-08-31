package us.mikeandwan.photos.data;

import java.util.Date;


@SuppressWarnings("ALL")
public class ExifData {
    // exif
    private Short _bitsPerSample;
    private String _compression;
    private String _contrast;
    private Date _createDate;
    private Double _digitalZoomRatio;
    private String _exposureCompensation;
    private String _exposureMode;
    private String _exposureProgram;
    private String _exposureTime;
    private Double _fNumber;
    private String _flash;
    private Double _focalLength;
    private Double _focalLengthIn35mmFormat;
    private String _gainControl;
    private Double _gpsAltitude;
    private String _gpsAltitudeRef;
    private Date _gpsDateStamp;
    private Double _gpsDirection;
    private String _gpsDirectionRef;
    private Double _gpsLatitude;
    private String _gpsLatitudeRef;
    private Double _gpsLongitude;
    private String _gpsLongitudeRef;
    private String _gpsMeasureMode;
    private String _gpsSatellites;
    private String _gpsStatus;
    private String _gpsVersionId;
    private Integer _iso;
    private String _lightSource;
    private String _make;
    private String _meteringMode;
    private String _model;
    private String _orientation;
    private String _saturation;
    private String _sceneCaptureType;
    private String _sceneType;
    private String _sensingMethod;
    private String _sharpness;

    // nikon
    private String _autoFocusAreaMode;
    private String _autoFocusPoint;
    private String _activeDLighting;
    private String _colorspace;
    private Double _exposureDifference;
    private String _flashColorFilter;
    private String _flashCompensation;
    private Short _flashControlMode;
    private String _flashExposureCompensation;
    private Double _flashFocalLength;
    private String _flashMode;
    private String _flashSetting;
    private String _flashType;
    private Double _focusDistance;
    private String _focusMode;
    private Integer _focusPosition;
    private String _highIsoNoiseReduction;
    private String _hueAdjustment;
    private String _noiseReduction;
    private String _pictureControlName;
    private String _primaryAFPoint;
    private String _vRMode;
    private String _vibrationReduction;
    private String _vignetteControl;
    private String _whiteBalance;

    // composite
    private Double _aperture;
    private String _autoFocus;
    private String _depthOfField;
    private String _fieldOfView;
    private Double _hyperfocalDistance;
    private String _lensId;
    private Double _lightValue;
    private Double _scaleFactor35Efl;
    private String _shutterSpeed;

    // processing info
    private String _rawConversionMode;
    private Double _sigmoidalContrastAdjustment;
    private Double _saturationAdjustment;
    private Short _compressionQuality;


    // exif
    public void setBitsPerSample(Short bitsPerSample) {
        _bitsPerSample = bitsPerSample;
    }

    public void setCompression(String compression) {
        _compression = compression;
    }

    public void setContrast(String contrast) {
        _contrast = contrast;
    }

    public void setCreateDate(Date createDate) {
        _createDate = createDate;
    }

    public void setDigitalZoomRatio(Double digitalZoomRatio) {
        _digitalZoomRatio = digitalZoomRatio;
    }

    public void setExposureCompensation(String exposureCompensation) {
        _exposureCompensation = exposureCompensation;
    }

    public void setExposureMode(String exposureMode) {
        _exposureMode = exposureMode;
    }

    public void setExposureProgram(String exposureProgram) {
        _exposureProgram = exposureProgram;
    }

    public void setExposureTime(String exposureTime) {
        _exposureTime = exposureTime;
    }

    public void setfNumber(Double fNumber) {
        _fNumber = fNumber;
    }

    public void setFlash(String flash) {
        _flash = flash;
    }

    public void setFocalLength(Double focalLength) {
        _focalLength = focalLength;
    }

    public void setFocalLengthIn35mmFormat(Double focalLengthIn35mmFormat) {
        _focalLengthIn35mmFormat = focalLengthIn35mmFormat;
    }

    public void setGainControl(String gainControl) {
        _gainControl = gainControl;
    }

    public void setGpsAltitude(Double gpsAltitude) {
        _gpsAltitude = gpsAltitude;
    }

    public void setGpsAltitudeRef(String gpsAltitudeRef) {
        _gpsAltitudeRef = gpsAltitudeRef;
    }

    public void setGpsDateStamp(Date gpsDateStamp) {
        _gpsDateStamp = gpsDateStamp;
    }

    public void setGpsDirection(Double gpsDirection) {
        _gpsDirection = gpsDirection;
    }

    public void setGpsDirectionRef(String gpsDirectionRef) {
        _gpsDirectionRef = gpsDirectionRef;
    }

    public void setGpsLatitude(Double gpsLatitude) {
        _gpsLatitude = gpsLatitude;
    }

    public void setGpsLatitudeRef(String gpsLatitudeRef) {
        _gpsLatitudeRef = gpsLatitudeRef;
    }

    public void setGpsLongitude(Double gpsLongitude) {
        _gpsLongitude = gpsLongitude;
    }

    public void setGpsLongitudeRef(String gpsLongitudeRef) {
        _gpsLongitudeRef = gpsLongitudeRef;
    }

    public void setGpsMeasureMode(String gpsMeasureMode) {
        _gpsMeasureMode = gpsMeasureMode;
    }

    public void setGpsSatellites(String gpsSatellites) {
        _gpsSatellites = gpsSatellites;
    }

    public void setGpsStatus(String gpsStatus) {
        _gpsStatus = gpsStatus;
    }

    public void setGpsVersionId(String gpsVersionId) {
        _gpsVersionId = gpsVersionId;
    }

    public void setIso(Integer iso) {
        _iso = iso;
    }

    public void setLightSource(String lightSource) {
        _lightSource = lightSource;
    }

    public void setMake(String make) {
        _make = make;
    }

    public void setMeteringMode(String meteringMode) {
        _meteringMode = meteringMode;
    }

    public void setModel(String model) {
        _model = model;
    }

    public void setOrientation(String orientation) {
        _orientation = orientation;
    }

    public void setSaturation(String saturation) {
        _saturation = saturation;
    }

    public void setSceneCaptureType(String sceneCaptureType) {
        _sceneCaptureType = sceneCaptureType;
    }

    public void setSceneType(String sceneType) {
        _sceneType = sceneType;
    }

    public void setSensingMethod(String sensingMethod) {
        _sensingMethod = sensingMethod;
    }

    public void setSharpness(String sharpness) {
        _sharpness = sharpness;
    }

    // nikon
    public void setAutoFocusAreaMode(String autoFocusAreaMode) {
        _autoFocusAreaMode = autoFocusAreaMode;
    }

    public void setAutoFocusPoint(String autoFocusPoint) {
        _autoFocusPoint = autoFocusPoint;
    }

    public void setActiveDLighting(String activeDLighting) {
        _activeDLighting = activeDLighting;
    }

    public void setColorspace(String colorspace) {
        _colorspace = colorspace;
    }

    public void setExposureDifference(Double exposureDifference) {
        _exposureDifference = exposureDifference;
    }

    public void setFlashColorFilter(String flashColorFilter) {
        _flashColorFilter = flashColorFilter;
    }

    public void setFlashCompensation(String flashCompensation) {
        _flashCompensation = flashCompensation;
    }

    public void setFlashControlMode(Short flashControlMode) {
        _flashControlMode = flashControlMode;
    }

    public void setFlashExposureCompensation(String flashExposureCompensation) {
        _flashExposureCompensation = flashExposureCompensation;
    }

    public void setFlashFocalLength(Double flashFocalLength) {
        _flashFocalLength = flashFocalLength;
    }

    public void setFlashMode(String flashMode) {
        _flashMode = flashMode;
    }

    public void setFlashSetting(String flashSetting) {
        _flashSetting = flashSetting;
    }

    public void setFlashType(String flashType) {
        _flashType = flashType;
    }

    public void setFocusDistance(Double focusDistance) {
        _focusDistance = focusDistance;
    }

    public void setFocusMode(String focusMode) {
        _focusMode = focusMode;
    }

    public void setFocusPosition(Integer focusPosition) {
        _focusPosition = focusPosition;
    }

    public void setHighIsoNoiseReduction(String highIsoNoiseReduction) {
        _highIsoNoiseReduction = highIsoNoiseReduction;
    }

    public void setHueAdjustment(String hueAdjustment) {
        _hueAdjustment = hueAdjustment;
    }

    public void setNoiseReduction(String noiseReduction) {
        _noiseReduction = noiseReduction;
    }

    public void setPictureControlName(String pictureControlName) {
        _pictureControlName = pictureControlName;
    }

    public void setPrimaryAFPoint(String primaryAFPoint) {
        _primaryAFPoint = primaryAFPoint;
    }

    public void setVrMode(String vRMode) {
        _vRMode = vRMode;
    }

    public void setVibrationReduction(String vibrationReduction) {
        _vibrationReduction = vibrationReduction;
    }

    public void setVignetteControl(String vignetteControl) {
        _vignetteControl = vignetteControl;
    }

    public void setWhiteBalance(String whiteBalance) {
        _whiteBalance = whiteBalance;
    }

    // composite
    public void setAperture(Double aperture) {
        _aperture = aperture;
    }

    public void setAutoFocus(String autoFocus) {
        _autoFocus = autoFocus;
    }

    public void setDepthOfField(String depthOfField) {
        _depthOfField = depthOfField;
    }

    public void setFieldOfView(String fieldOfView) {
        _fieldOfView = fieldOfView;
    }

    public void setHyperfocalDistance(Double hyperfocalDistance) {
        _hyperfocalDistance = hyperfocalDistance;
    }

    public void setLensId(String lensId) {
        _lensId = lensId;
    }

    public void setLightValue(Double lightValue) {
        _lightValue = lightValue;
    }

    public void setScaleFactor35Efl(Double scaleFactor35Efl) {
        _scaleFactor35Efl = scaleFactor35Efl;
    }

    public void setShutterSpeed(String shutterSpeed) {
        _shutterSpeed = shutterSpeed;
    }

    // processing info
    public void setRawConversionMode(String rawConversionMode) {
        _rawConversionMode = rawConversionMode;
    }

    public void setSigmoidalContrastAdjustment(Double sigmoidalContrastAdjustment) {
        _sigmoidalContrastAdjustment = sigmoidalContrastAdjustment;
    }

    public void setSaturationAdjustment(Double saturationAdjustment) {
        _saturationAdjustment = saturationAdjustment;
    }

    public void setCompressionQuality(Short compressionQuality) {
        _compressionQuality = compressionQuality;
    }


    // exif
    public Short getBitsPerSample() {
        return _bitsPerSample;
    }

    public String getCompression() {
        return _compression;
    }

    public String getContrast() {
        return _contrast;
    }

    public Date getCreateDate() {
        return _createDate;
    }

    public Double getDigitalZoomRatio() {
        return _digitalZoomRatio;
    }

    public String getExposureCompensation() {
        return _exposureCompensation;
    }

    public String getExposureMode() {
        return _exposureMode;
    }

    public String getExposureProgram() {
        return _exposureProgram;
    }

    public String getExposureTime() {
        return _exposureTime;
    }

    public Double getfNumber() {
        return _fNumber;
    }

    public String getFlash() {
        return _flash;
    }

    public Double getFocalLength() {
        return _focalLength;
    }

    public Double getFocalLengthIn35mmFormat() {
        return _focalLengthIn35mmFormat;
    }

    public String getGainControl() {
        return _gainControl;
    }

    public Double getGpsAltitude() {
        return _gpsAltitude;
    }

    public String getGpsAltitudeRef() {
        return _gpsAltitudeRef;
    }

    public Date getGpsDateStamp() {
        return _gpsDateStamp;
    }

    public Double getGpsDirection() {
        return _gpsDirection;
    }

    public String getGpsDirectionRef() {
        return _gpsDirectionRef;
    }

    public Double getGpsLatitude() {
        return _gpsLatitude;
    }

    public String getGpsLatitudeRef() {
        return _gpsLatitudeRef;
    }

    public Double getGpsLongitude() {
        return _gpsLongitude;
    }

    public String getGpsLongitudeRef() {
        return _gpsLongitudeRef;
    }

    public String getGpsMeasureMode() {
        return _gpsMeasureMode;
    }

    public String getGpsSatellites() {
        return _gpsSatellites;
    }

    public String getGpsStatus() {
        return _gpsStatus;
    }

    public String getGpsVersionId() {
        return _gpsVersionId;
    }

    public Integer getIso() {
        return _iso;
    }

    public String getLightSource() {
        return _lightSource;
    }

    public String getMake() {
        return _make;
    }

    public String getMeteringMode() {
        return _meteringMode;
    }

    public String getModel() {
        return _model;
    }

    public String getOrientation() {
        return _orientation;
    }

    public String getSaturation() {
        return _saturation;
    }

    public String getSceneCaptureType() {
        return _sceneCaptureType;
    }

    public String getSceneType() {
        return _sceneType;
    }

    public String getSensingMethod() {
        return _sensingMethod;
    }

    public String getSharpness() {
        return _sharpness;
    }

    // nikon
    public String getAutoFocusAreaMode() {
        return _autoFocusAreaMode;
    }

    public String getAutoFocusPoint() {
        return _autoFocusPoint;
    }

    public String getActiveDLighting() {
        return _activeDLighting;
    }

    public String getColorspace() {
        return _colorspace;
    }

    public Double getExposureDifference() {
        return _exposureDifference;
    }

    public String getFlashColorFilter() {
        return _flashColorFilter;
    }

    public String getFlashCompensation() {
        return _flashCompensation;
    }

    public Short getFlashControlMode() {
        return _flashControlMode;
    }

    public String getFlashExposureCompensation() {
        return _flashExposureCompensation;
    }

    public Double getFlashFocalLength() {
        return _flashFocalLength;
    }

    public String getFlashMode() {
        return _flashMode;
    }

    public String getFlashSetting() {
        return _flashSetting;
    }

    public String getFlashType() {
        return _flashType;
    }

    public Double getFocusDistance() {
        return _focusDistance;
    }

    public String getFocusMode() {
        return _focusMode;
    }

    public Integer getFocusPosition() {
        return _focusPosition;
    }

    public String getHighIsoNoiseReduction() {
        return _highIsoNoiseReduction;
    }

    public String getHueAdjustment() {
        return _hueAdjustment;
    }

    public String getNoiseReduction() {
        return _noiseReduction;
    }

    public String getPictureControlName() {
        return _pictureControlName;
    }

    public String getPrimaryAFPoint() {
        return _primaryAFPoint;
    }

    public String getVRMode() {
        return _vRMode;
    }

    public String getVibrationReduction() {
        return _vibrationReduction;
    }

    public String getVignetteControl() {
        return _vignetteControl;
    }

    public String getWhiteBalance() {
        return _whiteBalance;
    }

    // composite
    public Double getAperture() {
        return _aperture;
    }

    public String getAutoFocus() {
        return _autoFocus;
    }

    public String getDepthOfField() {
        return _depthOfField;
    }

    public String getFieldOfView() {
        return _fieldOfView;
    }

    public Double getHyperfocalDistance() {
        return _hyperfocalDistance;
    }

    public String getLensId() {
        return _lensId;
    }

    public Double getLightValue() {
        return _lightValue;
    }

    public Double getScaleFactor35Efl() {
        return _scaleFactor35Efl;
    }

    public String getShutterSpeed() {
        return _shutterSpeed;
    }

    // processing info
    public String getRawConversionMode() {
        return _rawConversionMode;
    }

    public Double getSigmoidalContrastAdjustment() {
        return _sigmoidalContrastAdjustment;
    }

    public Double getSaturationAdjustment() {
        return _saturationAdjustment;
    }

    public Short getCompressionQuality() {
        return _compressionQuality;
    }
}
