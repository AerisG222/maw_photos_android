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