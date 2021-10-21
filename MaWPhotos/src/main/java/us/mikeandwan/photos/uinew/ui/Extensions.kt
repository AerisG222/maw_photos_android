package us.mikeandwan.photos.uinew.ui

import us.mikeandwan.photos.domain.Photo
import us.mikeandwan.photos.domain.PhotoCategory
import us.mikeandwan.photos.uinew.ui.imagegrid.ImageGridItem

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