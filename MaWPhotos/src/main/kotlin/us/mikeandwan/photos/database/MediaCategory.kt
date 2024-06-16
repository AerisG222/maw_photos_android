package us.mikeandwan.photos.database

import androidx.room.ColumnInfo
import us.mikeandwan.photos.domain.models.MediaType

data class MediaCategory(
    val id: Int,
    @ColumnInfo(name = "category_type") val type: MediaType,
    val year: Int,
    val name: String,
    @ColumnInfo(name = "teaser_height") val teaserHeight: Int,
    @ColumnInfo(name = "teaser_width") val teaserWidth: Int,
    @ColumnInfo(name = "teaser_url") val teaserUrl: String
)
