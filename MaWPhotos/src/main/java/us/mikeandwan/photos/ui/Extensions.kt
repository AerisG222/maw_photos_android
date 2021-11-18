package us.mikeandwan.photos.ui

import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItemWithSize

fun Photo.toImageGridItem(): ImageGridItem {
    return ImageGridItem(
        this.id,
        this.mdUrl,
        this
    )
}

fun PhotoCategory.toImageGridItem(): ImageGridItem {
    return ImageGridItem(
        this.id,
        this.teaserUrl.replace("/xs/", "/md/"),
        this
    )
}

fun ImageGridItem.toImageGridItemWithSize(size: Int): ImageGridItemWithSize {
    return ImageGridItemWithSize(
        this.id,
        this.url,
        size,
        this.data
    )
}