package us.mikeandwan.photos.domain

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
        this.displayType
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
        this.displayToolbar,
        this.displayThumbnails,
        this.displayTopToolbar,
        this.doFadeControls,
        this.slideshowIntervalSeconds
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