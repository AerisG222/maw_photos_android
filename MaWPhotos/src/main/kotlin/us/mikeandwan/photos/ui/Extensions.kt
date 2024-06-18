package us.mikeandwan.photos.ui

import us.mikeandwan.photos.api.ApiResult
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.Media
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.MediaCategory
import us.mikeandwan.photos.domain.models.MediaType
import us.mikeandwan.photos.domain.models.SearchResultCategory
import us.mikeandwan.photos.domain.models.Video
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem

fun Photo.toImageGridItem(): ImageGridItem<Media> {
    return ImageGridItem (
        this.id,
        this.mdUrl,
        this
    )
}

fun Video.toImageGridItem(): ImageGridItem<Media> {
    return ImageGridItem (
        this.id,
        this.thumbnailUrl,
        this
    )
}

fun MediaCategory.toImageGridItem(): ImageGridItem<MediaCategory> {
    return ImageGridItem(
        this.id,
        this.teaserUrl.replace("/xs/", "/md/"),
        this
    )
}

fun SearchResultCategory.toDomainMediaCategory(): MediaCategory {
    val type = if ( this.multimediaType == "photo" ) { MediaType.Photo } else { MediaType.Video }

    return MediaCategory(
        type,
        this.id,
        this.year,
        this.name,
        this.teaserPhotoHeight,
        this.teaserPhotoWidth,
        this.teaserPhotoPath
    )
}

fun <T> ApiResult<T>.toExternalCallStatus(): ExternalCallStatus<T> {
    return when(this) {
        is ApiResult.Success -> ExternalCallStatus.Success(this.result)
        is ApiResult.Empty -> ExternalCallStatus.Error("Unexpected result")
        is ApiResult.Error -> ExternalCallStatus.Error(this.error, this.exception)
    }
}
