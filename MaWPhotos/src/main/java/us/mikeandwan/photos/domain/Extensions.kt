package us.mikeandwan.photos.domain

import us.mikeandwan.photos.domain.models.*

fun us.mikeandwan.photos.database.PhotoCategory.toDomainPhotoCategory(): PhotoCategory {
    return PhotoCategory(
        this.id,
        this.year,
        this.name,
        this.teaserHeight,
        this.teaserWidth,
        this.teaserUrl
    )
}

fun us.mikeandwan.photos.api.Category.toDatabasePhotoCategory(): us.mikeandwan.photos.database.PhotoCategory {
    return us.mikeandwan.photos.database.PhotoCategory(
        this.id,
        this.year,
        this.name,
        this.teaserImage.height,
        this.teaserImage.width,
        this.teaserImage.url
    )
}

fun us.mikeandwan.photos.database.CategoryPreference.toDomainCategoryPreference(): CategoryPreference {
    return CategoryPreference(
        this.displayType,
        this.gridThumbnailSize
    )
}

fun us.mikeandwan.photos.database.NotificationPreference.toDomainNotificationPreference(): NotificationPreference {
    return NotificationPreference(
        this.doNotify,
        this.doVibrate
    )
}


fun us.mikeandwan.photos.database.PhotoPreference.toDomainPhotoPreference(): PhotoPreference {
    return PhotoPreference(
        this.slideshowIntervalSeconds,
        this.gridThumbnailSize
    )
}

fun us.mikeandwan.photos.database.RandomPreference.toDomainRandomPreference(): RandomPreference {
    return RandomPreference(
        this.slideshowIntervalSeconds,
        this.gridThumbnailSize
    )
}

fun us.mikeandwan.photos.api.Photo.toDomainPhoto(): Photo {
    return Photo(
        this.id,
        this.categoryId,
        this.imageMd.height,
        this.imageMd.width,
        this.imageMd.url,
        this.imageXs.height,
        this.imageXs.width,
        this.imageXs.url
    )
}

fun us.mikeandwan.photos.api.Rating.toDomainPhotoRating(): PhotoRating {
    return PhotoRating(
        this.userRating,
        this.averageRating
    )
}

fun us.mikeandwan.photos.api.Comment.toDomainPhotoComment(): PhotoComment {
    return PhotoComment(
        this.entryDate,
        this.commentText,
        this.username
    )
}

fun us.mikeandwan.photos.api.ExifData.toDomainExifData(): PhotoExifData {
    return PhotoExifData(
        // exif
        this.bitsPerSample,
        this.compression,
        this.contrast,
        this.createDate,
        this.digitalZoomRatio,
        this.exposureCompensation,
        this.exposureMode,
        this.exposureProgram,
        this.exposureTime,
        this.fNumber,
        this.flash,
        this.focalLength,
        this.focalLengthIn35mmFormat,
        this.gainControl,
        this.gpsAltitude,
        this.gpsAltitudeRef,
        this.gpsDateStamp,
        this.gpsDirection,
        this.gpsDirectionRef,
        this.gpsLatitude,
        this.gpsLatitudeRef,
        this.gpsLongitude,
        this.gpsLongitudeRef,
        this.gpsMeasureMode,
        this.gpsSatellites,
        this.gpsStatus,
        this.gpsVersionId,
        this.iso,
        this.lightSource,
        this.make,
        this.meteringMode,
        this.model,
        this.orientation,
        this.saturation,
        this.sceneCaptureType,
        this.sceneType,
        this.sensingMethod,
        this.sharpness,

        // nikon
        this.autoFocusAreaMode,
        this.autoFocusPoint,
        this.activeDLighting,
        this.colorspace,
        this.exposureDifference,
        this.flashColorFilter,
        this.flashCompensation,
        this.flashControlMode,
        this.flashExposureCompensation,
        this.flashFocalLength,
        this.flashMode,
        this.flashSetting,
        this.flashType,
        this.focusDistance,
        this.focusMode,
        this.focusPosition,
        this.highIsoNoiseReduction,
        this.hueAdjustment,
        this.noiseReduction,
        this.pictureControlName,
        this.primaryAFPoint,
        this.vrMode,
        this.vibrationReduction,
        this.vignetteControl,
        this.whiteBalance,

        // composite
        this.aperture,
        this.autoFocus,
        this.depthOfField,
        this.fieldOfView,
        this.hyperfocalDistance,
        this.lensId,
        this.lightValue,
        this.scaleFactor35Efl,
        this.shutterSpeed
    )
}