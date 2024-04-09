package us.mikeandwan.photos.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "photo_category",
    indices = [Index(value = ["year"])]
)
data class PhotoCategory(
    @PrimaryKey val id: Int,
    val year: Int,
    val name: String,
    @ColumnInfo(name = "teaser_height") val teaserHeight: Int,
    @ColumnInfo(name = "teaser_width") val teaserWidth: Int,
    @ColumnInfo(name = "teaser_url") val teaserUrl: String
)
