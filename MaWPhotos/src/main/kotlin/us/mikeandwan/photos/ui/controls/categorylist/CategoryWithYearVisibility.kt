package us.mikeandwan.photos.ui.controls.categorylist

import us.mikeandwan.photos.domain.models.PhotoCategory

data class CategoryWithYearVisibility(
    val category: PhotoCategory,
    val doShowYear: Boolean
)
