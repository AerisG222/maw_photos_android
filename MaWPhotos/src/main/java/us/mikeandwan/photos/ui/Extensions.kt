package us.mikeandwan.photos.ui

import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.domain.models.SearchResultCategory
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

fun SearchResultCategory.toImageGridItem(): ImageGridItem {
    val idOffset = when(this.multimediaType) {
        "Videos" -> 1_000_000
        else -> 0
    }

    return ImageGridItem(
        idOffset + this.id,
        this.teaserPhotoMdPath,
        this
    )
}