package us.mikeandwan.photos.domain.models

data class CategoryPreference(
    val displayType: CategoryDisplayType,
    val gridThumbnailSize: GridThumbnailSize
)

val CATEGORY_PREFERENCE_DEFAULT = CategoryPreference(
    CategoryDisplayType.Grid,
    GridThumbnailSize.Medium
)