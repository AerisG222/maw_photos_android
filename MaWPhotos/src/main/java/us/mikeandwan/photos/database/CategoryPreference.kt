package us.mikeandwan.photos.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import us.mikeandwan.photos.domain.CategoryDisplayType
import us.mikeandwan.photos.domain.GridThumbnailSize

@Entity(tableName = "category_preference")
data class CategoryPreference(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "display_type") val displayType: CategoryDisplayType,
    @ColumnInfo(name="grid_thumbnail_size") val gridThumbnailSize: GridThumbnailSize
)