package us.mikeandwan.photos.uinew.ui

import us.mikeandwan.photos.domain.Photo
import us.mikeandwan.photos.uinew.ui.imageGrid.ImageGridItem

fun Photo.toImageGridItem(): ImageGridItem {
    return ImageGridItem(
        this.id,
        this.mdUrl,
        this
    )
}