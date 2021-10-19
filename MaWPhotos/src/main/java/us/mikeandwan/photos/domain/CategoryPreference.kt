package us.mikeandwan.photos.domain

data class CategoryPreference(
    val displayType: CategoryDisplayType,
    val gridThumbnailSize: GridThumbnailSize
)

val CATEGORY_PREFERENCE_DEFAULT = CategoryPreference(
    CategoryDisplayType.Grid,
    GridThumbnailSize.Medium
)