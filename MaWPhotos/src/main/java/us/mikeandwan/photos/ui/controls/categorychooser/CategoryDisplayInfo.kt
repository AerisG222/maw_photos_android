package us.mikeandwan.photos.ui.controls.categorychooser

import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.PhotoCategory

data class CategoryDisplayInfo(
    val displayType: CategoryDisplayType,
    val categories: List<PhotoCategory>,
    val gridThumbnailSize: GridThumbnailSize,
    val showYearInList: Boolean,
    val enableRefresh: Boolean
)

val CATEGORY_DISPLAY_INFO_DEFAULT = CategoryDisplayInfo(
    CategoryDisplayType.Unspecified,
    emptyList(),
    GridThumbnailSize.Unspecified,
    false,
    false
)