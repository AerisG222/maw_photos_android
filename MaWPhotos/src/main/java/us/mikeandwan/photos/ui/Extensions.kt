package us.mikeandwan.photos.ui

import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.domain.models.SearchResultCategory
import us.mikeandwan.photos.ui.controls.categorylist.CategoryWithYearVisibility
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

fun PhotoCategory.toCategoryWithYearVisibility(showYear: Boolean): CategoryWithYearVisibility {
    return CategoryWithYearVisibility(
        this,
        showYear
    )
}

fun SearchResultCategory.toDomainPhotoCategory(): PhotoCategory {
    return PhotoCategory(
        this.id,
        this.year,
        this.name,
        this.teaserPhotoHeight,
        this.teaserPhotoWidth,
        this.teaserPhotoPath
    )
}